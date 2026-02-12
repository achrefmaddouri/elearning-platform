package com.elearning.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.elearning.dto.CourseRecommendationDTO;
import com.elearning.model.User;
import com.elearning.service.AIRecommendationService;
import com.elearning.service.UserService;

public class AIControllerUnitTest {

    @Mock
    private AIRecommendationService aiRecommendationService;

    @Mock
    private UserService userService;

    @InjectMocks
    private AIController controller;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getPersonalizedRecommendations_returns401_whenUserNull() {
        ResponseEntity<?> resp = controller.getPersonalizedRecommendations(null, 5);
        assertEquals(401, resp.getStatusCodeValue());
    }

    @Test
    public void getPersonalizedRecommendations_returnsOk_onSuccess() {
        User user = org.mockito.Mockito.mock(User.class);
        when(user.getId()).thenReturn(100L);

        List<CourseRecommendationDTO> recs = Collections.singletonList(
            new CourseRecommendationDTO(1L, "Title", "Category", 4.5, "Because you liked X")
        );

        when(aiRecommendationService.getPersonalizedRecommendations(100L, 10)).thenReturn(recs);

        ResponseEntity<?> resp = controller.getPersonalizedRecommendations(user, 10);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        Map<?,?> body = (Map<?,?>) resp.getBody();
        assertEquals(true, body.get("success"));
    }

    @Test
    public void predictRating_returnsOk_onSuccess() {
        User user = org.mockito.Mockito.mock(User.class);
        when(user.getId()).thenReturn(200L);

        when(aiRecommendationService.predictUserCourseRating(200L, 5L)).thenReturn(3.789);
        when(aiRecommendationService.getConfidenceScore(200L, 5L)).thenReturn(0.85);

        ResponseEntity<?> resp = controller.predictRating(user, 5L);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        Map<?,?> body = (Map<?,?>) resp.getBody();
        assertEquals(5L, ((Number) body.get("courseId")).longValue());
    }

    @Test
    public void updateUserPreferences_success_and_failure() {
        User user = org.mockito.Mockito.mock(User.class);
        when(user.getId()).thenReturn(300L);

        when(aiRecommendationService.updateUserPreferences(org.mockito.Mockito.eq(300L), org.mockito.Mockito.anyMap()))
            .thenReturn(true);

        ResponseEntity<?> resp = controller.updateUserPreferences(user, Collections.emptyMap());
        assertEquals(HttpStatus.OK, resp.getStatusCode());

        when(aiRecommendationService.updateUserPreferences(org.mockito.Mockito.eq(300L), org.mockito.Mockito.anyMap()))
            .thenReturn(false);

        ResponseEntity<?> resp2 = controller.updateUserPreferences(user, Collections.emptyMap());
        assertEquals(HttpStatus.BAD_REQUEST, resp2.getStatusCode());
    }

    @Test
    public void getModelMetrics_and_retrainModels() {
        Map<String,Object> metrics = new HashMap<>();
        metrics.put("rmse", 0.95);
        when(aiRecommendationService.getModelMetrics()).thenReturn(metrics);

        ResponseEntity<?> resp = controller.getModelMetrics();
        assertEquals(HttpStatus.OK, resp.getStatusCode());

        when(aiRecommendationService.triggerModelRetraining()).thenReturn(true);
        ResponseEntity<?> resp2 = controller.retrainModels();
        assertEquals(HttpStatus.OK, resp2.getStatusCode());
    }
}
