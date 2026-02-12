package com.elearning.controller;

import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/stripe-test")
public class StripeTestController {

    @Value("${stripe.api.key}")
    private String stripeSecretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    @PostMapping("/test-payment")
    public ResponseEntity<?> testStripeConnection() {
        try {
            // Create a test payment intent for $10.00
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(1000L) // Amount in cents ($10.00)
                    .setCurrency("usd")
                    .setDescription("Test payment for E-Learning Platform")
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Stripe connection successful!");
            response.put("paymentIntentId", paymentIntent.getId());
            response.put("clientSecret", paymentIntent.getClientSecret());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Stripe connection failed: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/test-key")
    public ResponseEntity<Map<String, Object>> testStripeKey() {
        Map<String, Object> response = new HashMap<>();
        
        if (stripeSecretKey != null && stripeSecretKey.startsWith("sk_test_")) {
            response.put("success", true);
            response.put("message", "Stripe test key is properly configured");
            response.put("keyPrefix", stripeSecretKey.substring(0, 12) + "...");
        } else if (stripeSecretKey != null && stripeSecretKey.startsWith("sk_live_")) {
            response.put("success", true);
            response.put("message", "Warning: Using live Stripe key in development");
            response.put("keyPrefix", stripeSecretKey.substring(0, 12) + "...");
        } else {
            response.put("success", false);
            response.put("message", "Invalid or missing Stripe key");
        }
        
        return ResponseEntity.ok(response);
    }
}
