package com.elearning.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.elearning.model.LearningSession;
import com.elearning.model.QuizAttempt;
import com.elearning.model.User;
import com.elearning.model.UserRecommendation;
import com.elearning.repository.LearningSessionRepository;
import com.elearning.repository.QuizAttemptRepository;
import com.elearning.service.AnalyticsService;
import com.elearning.service.UserService;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private QuizAttemptRepository quizAttemptRepository;
    
    @Autowired
    private LearningSessionRepository learningSessionRepository;

    // Learning Analytics
    @GetMapping("/user")
    @PreAuthorize("hasRole('USER') or hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<?> getUserAnalytics() {
        try {
            String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            User currentUser = userService.findByEmail(currentUserEmail);
            
            Map<String, Object> analytics = analyticsService.getUserLearningAnalytics(currentUser);
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error getting user analytics: " + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or (@userService.findById(#userId).email == authentication.name)")
    public ResponseEntity<?> getUserAnalytics(@PathVariable Long userId) {
        try {
            User user = userService.findById(userId);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            
            Map<String, Object> analytics = analyticsService.getUserLearningAnalytics(user);
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error getting user analytics: " + e.getMessage());
        }
    }

    // Course Analytics
    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<?> getCourseAnalytics(@PathVariable Long courseId) {
        try {
            Map<String, Object> analytics = analyticsService.getCourseAnalytics(courseId);
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error getting course analytics: " + e.getMessage());
        }
    }

    // Instructor Analytics
    @GetMapping("/instructor")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<?> getInstructorAnalytics() {
        try {
            String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            User instructor = userService.findByEmail(currentUserEmail);
            
            Map<String, Object> analytics = analyticsService.getInstructorAnalytics(instructor);
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error getting instructor analytics: " + e.getMessage());
        }
    }

    @GetMapping("/instructor/{instructorId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getInstructorAnalytics(@PathVariable Long instructorId) {
        try {
            User instructor = userService.findById(instructorId);
            if (instructor == null) {
                return ResponseEntity.notFound().build();
            }
            
            Map<String, Object> analytics = analyticsService.getInstructorAnalytics(instructor);
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error getting instructor analytics: " + e.getMessage());
        }
    }

    // Recommendations
    @PostMapping("/recommendations/generate")
    @PreAuthorize("hasRole('USER') or hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<?> generateRecommendations() {
        try {
            String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            User currentUser = userService.findByEmail(currentUserEmail);
            
            List<UserRecommendation> recommendations = analyticsService.generateRecommendations(currentUser);
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error generating recommendations: " + e.getMessage());
        }
    }

    @GetMapping("/recommendations")
    @PreAuthorize("hasRole('USER') or hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<?> getUserRecommendations() {
        try {
            String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            User currentUser = userService.findByEmail(currentUserEmail);
            
            List<UserRecommendation> recommendations = analyticsService.getUserRecommendations(currentUser);
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error getting recommendations: " + e.getMessage());
        }
    }

    @PostMapping("/recommendations/{recommendationId}/view")
    @PreAuthorize("hasRole('USER') or hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<?> markRecommendationAsViewed(@PathVariable Long recommendationId) {
        try {
            analyticsService.markRecommendationAsViewed(recommendationId);
            return ResponseEntity.ok().body("Recommendation marked as viewed");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error marking recommendation: " + e.getMessage());
        }
    }

    @PostMapping("/recommendations/{recommendationId}/act")
    @PreAuthorize("hasRole('USER') or hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<?> markRecommendationAsActedUpon(@PathVariable Long recommendationId) {
        try {
            analyticsService.markRecommendationAsActedUpon(recommendationId);
            return ResponseEntity.ok().body("Recommendation marked as acted upon");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error marking recommendation: " + e.getMessage());
        }
    }

    // Learning Session Management
    @PostMapping("/session/start")
    @PreAuthorize("hasRole('USER') or hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<?> startLearningSession(@RequestParam Long courseId, @RequestParam String activityType) {
        try {
            // This would need CourseService to get the course
            // For now, we'll return a simple response
            return ResponseEntity.ok().body("Learning session start endpoint - needs CourseService integration");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error starting learning session: " + e.getMessage());
        }
    }

    @PostMapping("/session/{sessionId}/end")
    @PreAuthorize("hasRole('USER') or hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<?> endLearningSession(@PathVariable Long sessionId, @RequestParam(required = false) Double engagementScore) {
        try {
            analyticsService.endLearningSession(sessionId, engagementScore);
            return ResponseEntity.ok().body("Learning session ended");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error ending learning session: " + e.getMessage());
        }
    }

    // Dashboard Summary
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('USER') or hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<?> getDashboardAnalytics() {
        try {
            String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            User currentUser = userService.findByEmail(currentUserEmail);
            
            Map<String, Object> dashboard = Map.of(
                "userAnalytics", analyticsService.getUserLearningAnalytics(currentUser),
                "recommendations", analyticsService.getUserRecommendations(currentUser)
            );
            
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error getting dashboard analytics: " + e.getMessage());
        }
    }

    @GetMapping("/instructor/dashboard")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<?> getInstructorDashboard() {
        try {
            String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            User instructor = userService.findByEmail(currentUserEmail);
            
            Map<String, Object> dashboard = Map.of(
                "instructorAnalytics", analyticsService.getInstructorAnalytics(instructor),
                "userAnalytics", analyticsService.getUserLearningAnalytics(instructor),
                "recommendations", analyticsService.getUserRecommendations(instructor)
            );
            
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error getting instructor dashboard: " + e.getMessage());
        }
    }
    
    // Endpoint to create sample learning session data for testing
    @PostMapping("/sample-data")
    @PreAuthorize("hasRole('USER') or hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> createSampleData() {
        try {
            String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            User currentUser = userService.findByEmail(currentUserEmail);
            
            analyticsService.createSampleLearningData(currentUser);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Sample learning session data created successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to create sample data: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/create-from-activity")
    @PreAuthorize("hasRole('USER') or hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> createSessionsFromActivity() {
        try {
            String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            User currentUser = userService.findByEmail(currentUserEmail);
            
            // Create learning sessions based on existing quiz attempts
            List<QuizAttempt> quizAttempts = quizAttemptRepository.findByUser(currentUser);
            List<LearningSession> sessions = new ArrayList<>();
            
            for (QuizAttempt attempt : quizAttempts) {
                LearningSession session = new LearningSession();
                session.setUser(currentUser);
                session.setCourse(attempt.getQuiz().getCourse());
                session.setActivityType(LearningSession.ActivityType.QUIZ_ATTEMPT);
                session.setSessionStart(attempt.getAttemptedAt());
                session.setSessionEnd(attempt.getAttemptedAt().plusMinutes(15));
                session.setDurationMinutes(15);
                session.setEngagementScore((attempt.getScore() / (double) attempt.getTotalQuestions()) * 0.8 + 0.2);
                sessions.add(session);
            }
            
            learningSessionRepository.saveAll(sessions);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Created " + sessions.size() + " learning sessions from quiz attempts");
            response.put("sessionsCreated", sessions.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Failed to create sessions from activity: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
