package com.elearning.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import com.elearning.model.User;
import com.elearning.service.PaymentService;

import java.util.HashMap;

public class PaymentControllerUnitTest {

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController controller;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void createPaymentIntent_returnsOk() {
        Authentication auth = org.mockito.Mockito.mock(Authentication.class);
        User user = org.mockito.Mockito.mock(User.class);
        when(auth.getPrincipal()).thenReturn(user);

        Map<String,Object> req = new HashMap<>();
        req.put("courseId", 5L);

        Map<String,String> intent = Map.of("id", "pi_123");
        when(paymentService.createPaymentIntent(5L, user)).thenReturn(intent);

        ResponseEntity<?> resp = controller.createPaymentIntent(req, auth);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    public void confirmPayment_success_and_failure() {
        Authentication auth = org.mockito.Mockito.mock(Authentication.class);
        User user = org.mockito.Mockito.mock(User.class);
        when(auth.getPrincipal()).thenReturn(user);

        Map<String,Object> req = new HashMap<>();
        req.put("paymentIntentId", "pi_123");

        when(paymentService.confirmPayment("pi_123", user)).thenReturn(true);
        ResponseEntity<?> resp = controller.confirmPayment(req, auth);
        assertEquals(HttpStatus.OK, resp.getStatusCode());

        when(paymentService.confirmPayment("pi_123", user)).thenReturn(false);
        ResponseEntity<?> resp2 = controller.confirmPayment(req, auth);
        assertEquals(HttpStatus.BAD_REQUEST, resp2.getStatusCode());
    }

    @Test
    public void enrollInFreeCourse_returnsOk() {
        Authentication auth = org.mockito.Mockito.mock(Authentication.class);
        User user = org.mockito.Mockito.mock(User.class);
        when(auth.getPrincipal()).thenReturn(user);

        Map<String,Object> req = new HashMap<>();
        req.put("courseId", 8L);

        when(paymentService.enrollInFreeCourse(8L, user)).thenReturn(Map.of("enrolled", true));

        ResponseEntity<?> resp = controller.enrollInFreeCourse(req, auth);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }
}
