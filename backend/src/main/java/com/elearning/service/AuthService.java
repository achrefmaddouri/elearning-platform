package com.elearning.service;

import com.elearning.dto.*;
import com.elearning.model.*;
import com.elearning.repository.*;
import com.elearning.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
@Transactional
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OtpCodeRepository otpCodeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private EmailService emailService;

    @Autowired
    private GamificationService gamificationService;

    public String register(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Error: Email is already taken!");
        }

        // Generate OTP
        String otpCode = generateOTP();
        
        // Save OTP
        OtpCode otp = new OtpCode(registerRequest.getEmail(), otpCode);
        otpCodeRepository.save(otp);

        // Send OTP email
        String subject = "Email Verification - E-Learning Platform";
        String body = "Your verification code is: " + otpCode + "\nThis code will expire in 10 minutes.";
        emailService.sendEmail(registerRequest.getEmail(), subject, body);

        return "OTP sent to your email. Please verify to complete registration.";
    }

    public JwtResponse verifyOtpAndCreateAccount(VerifyOtpRequest verifyRequest) {
        // Verify OTP
        OtpCode otpCode = otpCodeRepository
                .findByEmailAndOtpCodeAndIsUsedFalse(verifyRequest.getEmail(), verifyRequest.getOtpCode())
                .orElseThrow(() -> new RuntimeException("Invalid OTP code"));

        if (otpCode.isExpired()) {
            throw new RuntimeException("OTP code has expired");
        }

        // Mark OTP as used
        otpCode.setUsed(true);
        otpCodeRepository.save(otpCode);

        // Create user account
        User user = new User();
        user.setEmail(verifyRequest.getEmail());
        user.setPassword(passwordEncoder.encode(verifyRequest.getPassword()));
        user.setEmailVerified(true);
        user.setStatus(UserStatus.ACTIVE);
        user.setRole(Role.USER);

        // Extract first and last name from email if not provided in registration
        String[] emailParts = verifyRequest.getEmail().split("@");
        user.setFirstName(emailParts[0]);
        user.setLastName("User");

        userRepository.save(user);

        // Initialize gamification profile for new user
        gamificationService.initializeUserGamification(user);

        // Auto login after registration
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(verifyRequest.getEmail(), verifyRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        return new JwtResponse(jwt, user.getId(), user.getEmail(), 
                user.getFirstName(), user.getLastName(), user.getRole());
    }

    public JwtResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        User user = (User) authentication.getPrincipal();
        
        // Handle daily login for streak tracking
        gamificationService.handleDailyLogin(user);
        
        return new JwtResponse(jwt, user.getId(), user.getEmail(), 
                user.getFirstName(), user.getLastName(), user.getRole());
    }

    private String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
}
