package com.elearning.service;

import com.elearning.model.*;
import com.elearning.repository.*;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class PaymentService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseService courseService;

    @Value("${stripe.api.key}")
    private String stripeSecretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    public Map<String, String> createPaymentIntent(Long courseId, User user) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (course.isFree()) {
            throw new RuntimeException("This course is free");
        }

        try {
            // Convert price to cents (Stripe requires amount in smallest currency unit)
            long amountInCents = course.getPrice().multiply(new BigDecimal("100")).longValue();

            Map<String, Object> params = new HashMap<>();
            params.put("amount", amountInCents);
            params.put("currency", "usd");
            params.put("automatic_payment_methods", Map.of("enabled", true));
            
            // Add metadata
            Map<String, String> metadata = new HashMap<>();
            metadata.put("courseId", courseId.toString());
            metadata.put("userId", user.getId().toString());
            metadata.put("courseName", course.getTitle());
            params.put("metadata", metadata);

            PaymentIntent paymentIntent = PaymentIntent.create(params);

            Map<String, String> response = new HashMap<>();
            response.put("clientSecret", paymentIntent.getClientSecret());
            response.put("paymentIntentId", paymentIntent.getId());

            return response;

        } catch (StripeException e) {
            throw new RuntimeException("Failed to create payment intent: " + e.getMessage());
        }
    }

    public boolean confirmPayment(String paymentIntentId, User user) {
        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            
            if ("succeeded".equals(paymentIntent.getStatus())) {
                // Get course ID from payment intent metadata
                String courseIdStr = paymentIntent.getMetadata().get("courseId");
                Long courseId = Long.valueOf(courseIdStr);
                
                // Payment successful, enroll user in course
                Course course = courseRepository.findById(courseId)
                        .orElseThrow(() -> new RuntimeException("Course not found"));
                
                courseService.enrollUserInCourse(user, course);
                return true;
            }
            
            return false;

        } catch (StripeException e) {
            throw new RuntimeException("Failed to confirm payment: " + e.getMessage());
        }
    }

    public Map<String, Object> enrollInFreeCourse(Long courseId, User user) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!course.isFree()) {
            throw new RuntimeException("This course is not free");
        }

        // Enroll user in free course
        courseService.enrollUserInCourse(user, course);
        
        // Return enrollment info
        Map<String, Object> response = new HashMap<>();
        response.put("courseId", courseId);
        response.put("userId", user.getId());
        response.put("enrolledAt", java.time.LocalDateTime.now().toString());
        response.put("paymentStatus", "COMPLETED");
        response.put("amount", 0);

        return response;
    }
}
