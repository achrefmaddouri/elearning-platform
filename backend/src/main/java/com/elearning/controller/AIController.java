package com.elearning.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.elearning.dto.CourseRecommendationDTO;
import com.elearning.model.User;
import com.elearning.service.AIRecommendationService;
import com.elearning.service.UserService;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AIController {

    @Autowired
    private AIRecommendationService aiRecommendationService;

    @Autowired
    private UserService userService;

    /**
     * Get personalized course recommendations for the authenticated user
     */
    @GetMapping("/recommendations")
    // @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('INSTRUCTOR')")
    public ResponseEntity<?> getPersonalizedRecommendations(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "10") int limit) {
        
        System.out.println("=== AI RECOMMENDATIONS ENDPOINT CALLED ===");
        System.out.println("Current user: " + (currentUser != null ? currentUser.getEmail() : "null"));
        System.out.println("Limit: " + limit);
        
        try {
            if (currentUser == null) {
                System.out.println("ERROR: Current user is null!");
                return ResponseEntity.status(401).body(Map.of(
                    "success", false,
                    "message", "User not authenticated"
                ));
            }
            
            List<CourseRecommendationDTO> recommendations = aiRecommendationService
                    .getPersonalizedRecommendations(currentUser.getId(), limit);
            
            System.out.println("Recommendations generated: " + recommendations.size());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "recommendations", recommendations,
                "total", recommendations.size(),
                "message", "Recommendations generated successfully"
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Failed to generate recommendations: " + e.getMessage()
            ));
        }
    }

    /**
     * Get course recommendations for a specific user (admin only)
     */
    @GetMapping("/recommendations/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getRecommendationsForUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int limit) {
        
        try {
            // Verify user exists
            User user = userService.getUserById(userId);
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "User not found"
                ));
            }

            List<CourseRecommendationDTO> recommendations = aiRecommendationService
                    .getPersonalizedRecommendations(userId, limit);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "userId", userId,
                "userName", user.getFirstName() + " " + user.getLastName(),
                "recommendations", recommendations,
                "total", recommendations.size()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Failed to generate recommendations: " + e.getMessage()
            ));
        }
    }

    /**
     * Predict rating for a specific user-course pair
     */
    @GetMapping("/predict-rating")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('INSTRUCTOR')")
    public ResponseEntity<?> predictRating(
            @AuthenticationPrincipal User currentUser,
            @RequestParam Long courseId) {
        
        try {
            double predictedRating = aiRecommendationService
                    .predictUserCourseRating(currentUser.getId(), courseId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "userId", currentUser.getId(),
                "courseId", courseId,
                "predictedRating", Math.round(predictedRating * 100.0) / 100.0,
                "confidence", aiRecommendationService.getConfidenceScore(currentUser.getId(), courseId)
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Failed to predict rating: " + e.getMessage()
            ));
        }
    }

    /**
     * Get similar courses to a given course
     */
    @GetMapping("/similar-courses/{courseId}")
    public ResponseEntity<?> getSimilarCourses(
            @PathVariable Long courseId,
            @RequestParam(defaultValue = "5") int limit) {
        
        try {
            List<CourseRecommendationDTO> similarCourses = aiRecommendationService
                    .getSimilarCourses(courseId, limit);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "courseId", courseId,
                "similarCourses", similarCourses,
                "total", similarCourses.size()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Failed to find similar courses: " + e.getMessage()
            ));
        }
    }

    /**
     * Get trending courses based on AI analysis
     */
    @GetMapping("/trending-courses")
    public ResponseEntity<?> getTrendingCourses(
            @RequestParam(defaultValue = "10") int limit) {
        
        try {
            List<CourseRecommendationDTO> trendingCourses = aiRecommendationService
                    .getTrendingCourses(limit);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "trendingCourses", trendingCourses,
                "total", trendingCourses.size(),
                "generatedAt", System.currentTimeMillis()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Failed to get trending courses: " + e.getMessage()
            ));
        }
    }

    /**
     * Get AI insights for course performance (instructor/admin only)
     */
    @GetMapping("/course-insights/{courseId}")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<?> getCourseInsights(
            @PathVariable Long courseId,
            @AuthenticationPrincipal User currentUser) {
        
        try {
            Map<String, Object> insights = aiRecommendationService
                    .getCourseInsights(courseId, currentUser);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "courseId", courseId,
                "insights", insights
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Failed to generate course insights: " + e.getMessage()
            ));
        }
    }

    /**
     * Update user preferences for better recommendations
     */
    @PostMapping("/update-preferences")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('INSTRUCTOR')")
    public ResponseEntity<?> updateUserPreferences(
            @AuthenticationPrincipal User currentUser,
            @RequestBody Map<String, Object> preferences) {
        
        try {
            boolean updated = aiRecommendationService
                    .updateUserPreferences(currentUser.getId(), preferences);
            
            if (updated) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "User preferences updated successfully"
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Failed to update preferences"
                ));
            }
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Failed to update preferences: " + e.getMessage()
            ));
        }
    }

    /**
     * Get general AI insights dashboard data (instructor/admin only)
     */
    @GetMapping("/insights")
    // @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<?> getAIInsights(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(required = false) Long instructorId) {
        
        try {
            // If instructorId is provided and user is admin, get insights for that instructor
            // Otherwise, get insights for the current user (if instructor) or general admin insights
            Long targetInstructorId = instructorId;
            boolean isAdmin = currentUser.getRole().toString().equals("ADMIN");
            
            if (targetInstructorId == null && currentUser.getRole().toString().equals("INSTRUCTOR")) {
                targetInstructorId = currentUser.getId();
            }
            
            // Get model metrics and general statistics
            Map<String, Object> modelMetrics = aiRecommendationService.getModelMetrics();
            
            // Create general insights combining model metrics with basic stats
            Map<String, Object> insightsData = new HashMap<>();
            insightsData.put("modelMetrics", modelMetrics);
            insightsData.put("userRole", currentUser.getRole().toString());
            insightsData.put("instructorId", targetInstructorId);
            insightsData.put("isAdmin", isAdmin);
            insightsData.put("lastUpdated", System.currentTimeMillis());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "insights", insightsData,
                "userId", currentUser.getId(),
                "isAdmin", isAdmin
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Failed to get AI insights: " + e.getMessage()
            ));
        }
    }

    /**
     * Get AI model performance metrics (admin only)
     */
    @GetMapping("/model-metrics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getModelMetrics() {
        
        try {
            Map<String, Object> metrics = aiRecommendationService.getModelMetrics();
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "metrics", metrics,
                "lastUpdated", System.currentTimeMillis()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Failed to get model metrics: " + e.getMessage()
            ));
        }
    }

    /**
     * Retrain AI models with latest data (admin only)
     */
    @PostMapping("/retrain-models")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> retrainModels() {
        
        try {
            // This would trigger a background job to retrain models
            boolean success = aiRecommendationService.triggerModelRetraining();
            
            if (success) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Model retraining initiated. This may take some time to complete."
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Failed to initiate model retraining"
                ));
            }
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Failed to retrain models: " + e.getMessage()
            ));
        }
    }
}