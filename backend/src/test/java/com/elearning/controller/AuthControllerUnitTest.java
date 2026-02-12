package com.elearning.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import com.elearning.dto.JwtResponse;
import com.elearning.model.Role;
import com.elearning.dto.LoginRequest;
import com.elearning.dto.RegisterRequest;
import com.elearning.dto.VerifyOtpRequest;
import com.elearning.model.User;
import com.elearning.service.AuthService;

public class AuthControllerUnitTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController controller;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void registerUser_returnsOk_onSuccess() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("test@example.com");
        when(authService.register(req)).thenReturn("Registered");

        ResponseEntity<?> resp = controller.registerUser(req);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    public void authenticateUser_returnsJwt_onSuccess() {
        LoginRequest req = new LoginRequest();
        req.setEmail("a@b.com");
        req.setPassword("pwd");
        JwtResponse jwt = new JwtResponse("token", 1L, "a@b.com", "First", "Last", Role.USER);
        when(authService.login(req)).thenReturn(jwt);

        ResponseEntity<?> resp = controller.authenticateUser(req);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    public void verifyToken_returnsOk_whenValid() {
        Authentication auth = org.mockito.Mockito.mock(Authentication.class);
        User user = org.mockito.Mockito.mock(User.class);
        when(user.getEmail()).thenReturn("u@e.com");
        when(user.getRole()).thenReturn(Role.USER);
        when(auth.getPrincipal()).thenReturn(user);

        ResponseEntity<?> resp = controller.verifyToken(auth);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }
}
