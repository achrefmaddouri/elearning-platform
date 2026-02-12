package com.elearning.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.elearning.dto.CourseRecommendationDTO;
import com.elearning.model.Course;
import com.elearning.model.Enrollment;
import com.elearning.model.Progress;
import com.elearning.model.User;
import com.elearning.repository.CourseRepository;
import com.elearning.repository.EnrollmentRepository;
import com.elearning.repository.ProgressRepository;
import com.elearning.repository.UserRepository;

@Service
@Transactional
public class AIRecommendationService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private ProgressRepository progressRepository;

    // In production, this would be a Python service call or ML model integration
    private final Random random = new Random(42); // For demo purposes

    /**
     * Get personalized course recommendations for a user
     */
    public List<CourseRecommendationDTO> getPersonalizedRecommendations(Long userId, int limit) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get user's enrollment history
        List<Enrollment> userEnrollments = enrollmentRepository.findByUser(user);
        Set<Long> enrolledCourseIds = userEnrollments.stream()
                .map(enrollment -> enrollment.getCourse().getId())
                .collect(Collectors.toSet());

        // Get user's progress and preferences
        List<Progress> userProgress = progressRepository.findByUser(user);
        Map<String, Double> categoryPreferences = calculateUserCategoryPreferences(userProgress);

        // Get all available courses (excluding already enrolled)
        List<Course> availableCourses = courseRepository.findAll().stream()
                .filter(course -> !enrolledCourseIds.contains(course.getId()))
                .filter(course -> course.getStatus().toString().equals("ACCEPTED"))
                .collect(Collectors.toList());

        // Generate recommendations using hybrid approach
        List<CourseRecommendationDTO> recommendations = availableCourses.stream()
                .map(course -> generateRecommendation(user, course, categoryPreferences))
                .sorted((r1, r2) -> Double.compare(r2.getPredictedRating(), r1.getPredictedRating()))
                .limit(limit)
                .collect(Collectors.toList());

        return recommendations;
    }

    /**
     * Predict rating for a specific user-course pair
     */
    public double predictUserCourseRating(Long userId, Long courseId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        return calculatePredictedRating(user, course);
    }

    /**
     * Get confidence score for a recommendation
     */
    public double getConfidenceScore(Long userId, Long courseId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Enrollment> userHistory = enrollmentRepository.findByUser(user);
        
        // More enrollments = higher confidence
        double baseConfidence = Math.min(0.9, userHistory.size() * 0.1 + 0.3);
        
        // Add some randomness for demo
        return Math.round((baseConfidence + random.nextGaussian() * 0.1) * 100.0) / 100.0;
    }

    /**
     * Get similar courses to a given course
     */
    public List<CourseRecommendationDTO> getSimilarCourses(Long courseId, int limit) {
        Course targetCourse = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        List<Course> similarCourses = courseRepository.findAll().stream()
                .filter(course -> !course.getId().equals(courseId))
                .filter(course -> course.getStatus().toString().equals("ACCEPTED"))
                .filter(course -> course.getCategory() != null && 
                                targetCourse.getCategory() != null &&
                                course.getCategory().getId().equals(targetCourse.getCategory().getId()))
                .limit(limit)
                .collect(Collectors.toList());

        return similarCourses.stream()
                .map(course -> {
                    CourseRecommendationDTO dto = convertCourseToRecommendationDTO(course);
                    dto.setPredictedRating(4.0 + random.nextDouble()); // Demo rating
                    dto.setRecommendationReason("Similar to: " + targetCourse.getTitle());
                    dto.setRelevanceScore(0.8 + random.nextDouble() * 0.2);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Get trending courses based on recent enrollments and ratings
     */
    public List<CourseRecommendationDTO> getTrendingCourses(int limit) {
        // Get courses with high recent enrollment activity
        List<Course> trendingCourses = courseRepository.findAll().stream()
                .filter(course -> course.getStatus().toString().equals("ACCEPTED"))
                .sorted((c1, c2) -> Long.compare(
                    enrollmentRepository.countByCourse(c2),
                    enrollmentRepository.countByCourse(c1)
                ))
                .limit(limit)
                .collect(Collectors.toList());

        return trendingCourses.stream()
                .map(course -> {
                    CourseRecommendationDTO dto = convertCourseToRecommendationDTO(course);
                    dto.setPredictedRating(4.2 + random.nextDouble() * 0.8);
                    dto.setRecommendationReason("Trending course with high enrollment");
                    dto.setRelevanceScore(0.9);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Get course insights for instructors/admins
     */
    public Map<String, Object> getCourseInsights(Long courseId, User currentUser) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // Check if user has permission to view insights
        if (!currentUser.getRole().toString().equals("ADMIN") && 
            !course.getInstructor().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Unauthorized to view course insights");
        }

        Map<String, Object> insights = new HashMap<>();
        
        // Basic metrics
        List<Enrollment> enrollments = enrollmentRepository.findByCourse(course);
        List<Progress> courseProgress = progressRepository.findByCourse(course);
        
        insights.put("totalEnrollments", enrollments.size());
        insights.put("completionRate", calculateCompletionRate(courseProgress));
        insights.put("averageProgress", calculateAverageProgress(courseProgress));
        
        // AI-generated insights
        insights.put("predictedEnrollmentGrowth", 15 + random.nextInt(25)); // Percentage
        insights.put("recommendationScore", 4.0 + random.nextDouble());
        insights.put("difficultyRating", course.getTitle().toLowerCase().contains("advanced") ? "High" : 
                                       course.getTitle().toLowerCase().contains("beginner") ? "Low" : "Medium");
        
        // Improvement suggestions
        List<String> suggestions = generateImprovementSuggestions(courseProgress);
        insights.put("improvementSuggestions", suggestions);
        
        insights.put("lastAnalyzed", LocalDateTime.now());
        
        return insights;
    }

    /**
     * Update user preferences for better recommendations
     */
    public boolean updateUserPreferences(Long userId, Map<String, Object> preferences) {
        // In a real implementation, you would store these preferences in a user_preferences table
        // For now, we'll just return true to indicate success
        return true;
    }

    /**
     * Get AI model performance metrics
     */
    public Map<String, Object> getModelMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        // Simulated metrics - in production, these would come from your ML model monitoring
        metrics.put("accuracy", 0.85 + random.nextDouble() * 0.1);
        metrics.put("precision", 0.82 + random.nextDouble() * 0.1);
        metrics.put("recall", 0.78 + random.nextDouble() * 0.1);
        metrics.put("f1Score", 0.80 + random.nextDouble() * 0.1);
        metrics.put("rmse", 0.65 + random.nextDouble() * 0.2);
        metrics.put("mae", 0.52 + random.nextDouble() * 0.15);
        
        metrics.put("recommendationsGenerated", 10000 + random.nextInt(5000));
        metrics.put("averageResponseTime", 120 + random.nextInt(50)); // milliseconds
        metrics.put("modelVersion", "1.0.0");
        metrics.put("trainingDataSize", 50000 + random.nextInt(10000));
        
        return metrics;
    }

    /**
     * Trigger model retraining (placeholder)
     */
    public boolean triggerModelRetraining() {
        // In production, this would trigger a background job to retrain ML models
        // For now, we'll simulate success
        return true;
    }

    // Private helper methods

    private Map<String, Double> calculateUserCategoryPreferences(List<Progress> userProgress) {
        Map<String, Double> preferences = new HashMap<>();
        
        for (Progress progress : userProgress) {
            String categoryName = progress.getCourse().getCategory() != null ? 
                                progress.getCourse().getCategory().getName() : "General";
            
            double score = progress.getProgressPercentage() / 100.0;
            if (progress.isCompleted()) {
                score += 0.5; // Bonus for completion
            }
            
            preferences.merge(categoryName, score, Double::sum);
        }
        
        return preferences;
    }

    private CourseRecommendationDTO generateRecommendation(User user, Course course, 
                                                         Map<String, Double> categoryPreferences) {
        CourseRecommendationDTO dto = convertCourseToRecommendationDTO(course);
        
        // Calculate predicted rating using simplified collaborative filtering
        double predictedRating = calculatePredictedRating(user, course);
        dto.setPredictedRating(predictedRating);
        
        // Generate recommendation reason
        String reason = generateRecommendationReason(user, course, categoryPreferences);
        dto.setRecommendationReason(reason);
        
        // Calculate confidence score
        double confidence = getConfidenceScore(user.getId(), course.getId());
        dto.setConfidenceScore(confidence);
        
        // Calculate relevance score
        double relevance = calculateRelevanceScore(user, course, categoryPreferences);
        dto.setRelevanceScore(relevance);
        
        return dto;
    }

    private double calculatePredictedRating(User user, Course course) {
        // Simplified rating prediction algorithm
        double baseRating = 3.5; // Default rating
        
        // Factor in course category preference
        List<Progress> userProgress = progressRepository.findByUser(user);
        String courseCategoryName = course.getCategory() != null ? course.getCategory().getName() : "General";
        
        long categoryExperience = userProgress.stream()
                .filter(p -> p.getCourse().getCategory() != null && 
                           p.getCourse().getCategory().getName().equals(courseCategoryName))
                .count();
        
        // Higher rating for preferred categories
        if (categoryExperience > 0) {
            baseRating += 0.5;
        }
        
        // Factor in course price (free courses might be rated slightly lower)
        if (course.isFree()) {
            baseRating -= 0.2;
        } else if (course.getPrice().doubleValue() > 100) {
            baseRating += 0.3; // Premium courses expected to be better
        }
        
        // Add some randomness while keeping it realistic
        baseRating += (random.nextGaussian() * 0.5);
        
        // Clamp to 1-5 range
        return Math.max(1.0, Math.min(5.0, baseRating));
    }

    private String generateRecommendationReason(User user, Course course, 
                                              Map<String, Double> categoryPreferences) {
        List<String> reasons = new ArrayList<>();
        
        String categoryName = course.getCategory() != null ? course.getCategory().getName() : "General";
        
        if (categoryPreferences.containsKey(categoryName) && categoryPreferences.get(categoryName) > 1.0) {
            reasons.add("Based on your interest in " + categoryName);
        }
        
        if (course.isFree()) {
            reasons.add("Free course to expand your knowledge");
        }
        
        if (course.getTitle().toLowerCase().contains("beginner") && 
            enrollmentRepository.findByUser(user).size() < 3) {
            reasons.add("Perfect for getting started");
        }
        
        if (reasons.isEmpty()) {
            reasons.add("Recommended for you based on trending courses");
        }
        
        return String.join(", ", reasons);
    }

    private double calculateRelevanceScore(User user, Course course, Map<String, Double> categoryPreferences) {
        double relevance = 0.5; // Base relevance
        
        String categoryName = course.getCategory() != null ? course.getCategory().getName() : "General";
        
        if (categoryPreferences.containsKey(categoryName)) {
            relevance += Math.min(0.4, categoryPreferences.get(categoryName) * 0.1);
        }
        
        // Factor in user experience level
        int userCourseCount = enrollmentRepository.findByUser(user).size();
        boolean isBeginnerCourse = course.getTitle().toLowerCase().contains("beginner");
        boolean isAdvancedCourse = course.getTitle().toLowerCase().contains("advanced");
        
        if (userCourseCount < 3 && isBeginnerCourse) {
            relevance += 0.2;
        } else if (userCourseCount > 10 && isAdvancedCourse) {
            relevance += 0.2;
        }
        
        return Math.min(1.0, relevance);
    }

    private CourseRecommendationDTO convertCourseToRecommendationDTO(Course course) {
        CourseRecommendationDTO dto = new CourseRecommendationDTO();
        
        dto.setCourseId(course.getId());
        dto.setTitle(course.getTitle());
        dto.setDescription(course.getDescription());
        dto.setCategory(course.getCategory() != null ? course.getCategory().getName() : "General");
        dto.setPrice(course.getPrice().doubleValue());
        dto.setFree(course.isFree());
        dto.setThumbnailUrl(course.getThumbnailUrl());
        dto.setInstructorName(course.getInstructor().getFirstName() + " " + course.getInstructor().getLastName());
        dto.setEnrollmentCount((int) enrollmentRepository.countByCourse(course));
        dto.setGeneratedAt(LocalDateTime.now());
        
        // Set some default values that would normally come from other sources
        dto.setAverageRating(4.0 + random.nextDouble()); // Demo rating
        dto.setDurationHours(10 + random.nextInt(40)); // Demo duration
        
        return dto;
    }

    private double calculateCompletionRate(List<Progress> courseProgress) {
        if (courseProgress.isEmpty()) return 0.0;
        
        long completedCount = courseProgress.stream()
                .filter(Progress::isCompleted)
                .count();
        
        return (double) completedCount / courseProgress.size() * 100;
    }

    private double calculateAverageProgress(List<Progress> courseProgress) {
        if (courseProgress.isEmpty()) return 0.0;
        
        return courseProgress.stream()
                .mapToInt(Progress::getProgressPercentage)
                .average()
                .orElse(0.0);
    }

    private List<String> generateImprovementSuggestions(List<Progress> courseProgress) {
        List<String> suggestions = new ArrayList<>();
        
        double completionRate = calculateCompletionRate(courseProgress);
        double averageProgress = calculateAverageProgress(courseProgress);
        
        if (completionRate < 30) {
            suggestions.add("Consider adding more interactive content to improve engagement");
            suggestions.add("Break down complex topics into smaller, manageable sections");
        }
        
        if (averageProgress < 50) {
            suggestions.add("Add progress checkpoints and achievements to motivate learners");
            suggestions.add("Include more practical examples and exercises");
        }
        
        if (courseProgress.size() < 10) {
            suggestions.add("Improve course marketing and visibility");
            suggestions.add("Consider offering early bird discounts or promotions");
        }
        
        suggestions.add("Collect student feedback to identify specific areas for improvement");
        suggestions.add("Update course content regularly to keep it current and relevant");
        
        return suggestions;
    }
}