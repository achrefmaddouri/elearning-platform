package com.elearning.controller;

import com.elearning.model.*;
import com.elearning.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("hasRole('USER') or hasRole('INSTRUCTOR') or hasRole('ADMIN')")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create-intent")
    public ResponseEntity<?> createPaymentIntent(@RequestBody Map<String, Object> request,
                                                Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Long courseId = Long.valueOf(request.get("courseId").toString());
            Map<String, String> paymentIntent = paymentService.createPaymentIntent(courseId, user);
            return ResponseEntity.ok(paymentIntent);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new AuthController.MessageResponse("Error: " + e.getMessage()));
        }
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirmPayment(@RequestBody Map<String, Object> request,
                                           Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            String paymentIntentId = request.get("paymentIntentId").toString();
            boolean success = paymentService.confirmPayment(paymentIntentId, user);
            if (success) {
                return ResponseEntity.ok(new AuthController.MessageResponse("Payment successful and enrollment completed"));
            } else {
                return ResponseEntity.badRequest()
                        .body(new AuthController.MessageResponse("Payment failed"));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new AuthController.MessageResponse("Error: " + e.getMessage()));
        }
    }

    @PostMapping("/enroll-free")
    public ResponseEntity<?> enrollInFreeCourse(@RequestBody Map<String, Object> request,
                                               Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Long courseId = Long.valueOf(request.get("courseId").toString());
            Map<String, Object> enrollment = paymentService.enrollInFreeCourse(courseId, user);
            return ResponseEntity.ok(enrollment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new AuthController.MessageResponse("Error: " + e.getMessage()));
        }
    }
}
