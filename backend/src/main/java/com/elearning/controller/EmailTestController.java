package com.elearning.controller;

import com.elearning.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class EmailTestController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send-email")
    public ResponseEntity<String> testEmail(@RequestParam String toEmail) {
        try {
            emailService.sendEmail(
                toEmail,
                "Test Email from ELearning Platform",
                "This is a test email to verify SMTP configuration is working correctly."
            );
            return ResponseEntity.ok("Email sent successfully to " + toEmail);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body("Failed to send email: " + e.getMessage());
        }
    }
}
