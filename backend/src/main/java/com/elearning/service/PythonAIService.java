package com.elearning.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.elearning.config.AIModelConfig;
import com.elearning.dto.CourseRecommendationDTO;
import com.elearning.model.Course;

@Service
public class PythonAIService {
    
    private static final Logger logger = Logger.getLogger(PythonAIService.class.getName());
    
    @Autowired
    private AIModelConfig aiModelConfig;
    
    /**
     * Get personalized course recommendations using the trained Python model
     */
    public CompletableFuture<List<CourseRecommendationDTO>> getRecommendations(
            Long userId, List<Course> allCourses, int limit) {
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (!aiModelConfig.areModelsLoaded()) {
                    logger.warning("AI models not loaded, using fallback recommendations");
                    return getFallbackRecommendations(allCourses, limit);
                }
                
                // Create Python script to get recommendations
                String pythonScript = createRecommendationScript(userId, limit);
                
                // Execute Python script
                List<CourseRecommendationDTO> recommendations = executePythonScript(pythonScript, allCourses);
                
                return recommendations.isEmpty() ? getFallbackRecommendations(allCourses, limit) : recommendations;
                
            } catch (Exception e) {
                logger.severe("Error getting AI recommendations: " + e.getMessage());
                return getFallbackRecommendations(allCourses, limit);
            }
        });
    }
    
    /**
     * Predict rating for a specific user-course pair
     */
    public CompletableFuture<Double> predictRating(Long userId, Long courseId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (!aiModelConfig.areModelsLoaded()) {
                    return 4.0; // Default prediction
                }
                
                String pythonScript = createRatingPredictionScript(userId, courseId);
                return executePythonRatingPrediction(pythonScript);
                
            } catch (Exception e) {
                logger.severe("Error predicting rating: " + e.getMessage());
                return 4.0; // Default rating
            }
        });
    }
    
    /**
     * Find similar courses using the trained model
     */
    public CompletableFuture<List<CourseRecommendationDTO>> getSimilarCourses(
            Long courseId, List<Course> allCourses, int limit) {
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (!aiModelConfig.areModelsLoaded()) {
                    return getFallbackSimilarCourses(courseId, allCourses, limit);
                }
                
                String pythonScript = createSimilarCoursesScript(courseId, limit);
                List<CourseRecommendationDTO> similar = executePythonScript(pythonScript, allCourses);
                
                return similar.isEmpty() ? getFallbackSimilarCourses(courseId, allCourses, limit) : similar;
                
            } catch (Exception e) {
                logger.severe("Error finding similar courses: " + e.getMessage());
                return getFallbackSimilarCourses(courseId, allCourses, limit);
            }
        });
    }
    
    private String createRecommendationScript(Long userId, int limit) throws IOException {
        return String.format("""
            import pickle
            import numpy as np
            import pandas as pd
            
            # Load models
            svd_model_path = "%s"
            user_encoder_path = "%s"
            course_encoder_path = "%s"
            
            try:
                with open(svd_model_path, 'rb') as f:
                    svd_model = pickle.load(f)
                with open(user_encoder_path, 'rb') as f:
                    user_encoder = pickle.load(f)
                with open(course_encoder_path, 'rb') as f:
                    course_encoder = pickle.load(f)
                
                # Generate recommendations for user
                user_id = %d
                n_recommendations = %d
                
                # Get all course IDs
                all_course_ids = list(range(1, 801))  # Assuming course IDs 1-800
                
                # Predict ratings for all courses
                predictions = []
                for course_id in all_course_ids:
                    try:
                        pred = svd_model.predict(user_id, course_id)
                        predictions.append((course_id, pred.est, pred.est))  # course_id, rating, confidence
                    except:
                        predictions.append((course_id, 3.5, 0.5))  # Default values
                
                # Sort by predicted rating and get top N
                predictions.sort(key=lambda x: x[1], reverse=True)
                top_predictions = predictions[:n_recommendations]
                
                # Output results (course_id,predicted_rating,confidence)
                for course_id, rating, confidence in top_predictions:
                    print(f"{course_id},{rating:.2f},{confidence:.2f}")
                    
            except Exception as e:
                print(f"ERROR: {str(e)}")
            """, 
            aiModelConfig.getAbsoluteModelPath("svd_model"),
            aiModelConfig.getAbsoluteModelPath("user_encoder"),
            aiModelConfig.getAbsoluteModelPath("course_encoder"),
            userId, limit);
    }
    
    private String createRatingPredictionScript(Long userId, Long courseId) throws IOException {
        return String.format("""
            import pickle
            
            # Load SVD model
            svd_model_path = "%s"
            
            try:
                with open(svd_model_path, 'rb') as f:
                    svd_model = pickle.load(f)
                
                # Predict rating
                pred = svd_model.predict(%d, %d)
                print(f"{pred.est:.2f}")
                
            except Exception as e:
                print("4.0")  # Default rating on error
            """, 
            aiModelConfig.getAbsoluteModelPath("svd_model"), userId, courseId);
    }
    
    private String createSimilarCoursesScript(Long courseId, int limit) throws IOException {
        return String.format("""
            import pickle
            import numpy as np
            
            # Load course similarity matrix
            similarity_path = "%s"
            
            try:
                with open(similarity_path, 'rb') as f:
                    similarity_matrix = pickle.load(f)
                
                # Get similar courses
                course_idx = %d - 1  # Convert to 0-based index
                if course_idx < len(similarity_matrix):
                    similarities = similarity_matrix[course_idx]
                    
                    # Get top similar courses (excluding self)
                    similar_indices = np.argsort(similarities)[::-1][1:%d+1]
                    
                    for idx in similar_indices:
                        course_id = idx + 1  # Convert back to 1-based ID
                        similarity_score = similarities[idx]
                        print(f"{course_id},{similarity_score:.2f},{similarity_score:.2f}")
                else:
                    print("ERROR: Course index out of range")
                    
            except Exception as e:
                print(f"ERROR: {str(e)}")
            """, 
            aiModelConfig.getAbsoluteModelPath("course_similarity"), courseId, limit);
    }
    
    private List<CourseRecommendationDTO> executePythonScript(String script, List<Course> allCourses) {
        List<CourseRecommendationDTO> recommendations = new ArrayList<>();
        
        try {
            // Create temporary Python file
            File tempScript = File.createTempFile("ai_script", ".py");
            tempScript.deleteOnExit();
            
            try (FileWriter writer = new FileWriter(tempScript)) {
                writer.write(script);
            }
            
            // Execute Python script
            ProcessBuilder pb = new ProcessBuilder("python", tempScript.getAbsolutePath());
            Process process = pb.start();
            
            // Read output
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.startsWith("ERROR:") && line.contains(",")) {
                        String[] parts = line.split(",");
                        if (parts.length >= 3) {
                            try {
                                Long courseId = Long.parseLong(parts[0]);
                                Double predictedRating = Double.parseDouble(parts[1]);
                                Double confidence = Double.parseDouble(parts[2]);
                                
                                // Find corresponding course
                                Course course = findCourseById(allCourses, courseId);
                                if (course != null) {
                                    CourseRecommendationDTO dto = new CourseRecommendationDTO();
                                    dto.setCourseId(course.getId());
                                    dto.setTitle(course.getTitle());
                                    dto.setDescription(course.getDescription());
                                    dto.setCategory(course.getCategory() != null ? course.getCategory().getName() : "General");
                                    dto.setDifficulty("Intermediate"); // Default difficulty
                                    dto.setPrice(course.getPrice() != null ? course.getPrice().doubleValue() : 0.0);
                                    dto.setFree(course.isFree());
                                    dto.setThumbnailUrl(course.getThumbnailUrl());
                                    dto.setInstructorName(course.getInstructor() != null ? 
                                        (course.getInstructor().getFirstName() + " " + course.getInstructor().getLastName()) : "Unknown");
                                    dto.setEnrollmentCount(course.getEnrollments() != null ? course.getEnrollments().size() : 0);
                                    dto.setDurationHours(8); // Default duration
                                    dto.setPredictedRating(predictedRating);
                                    dto.setConfidenceScore(confidence);
                                    dto.setRecommendationReason("AI-powered recommendation based on your learning history");
                                    recommendations.add(dto);
                                }
                            } catch (NumberFormatException e) {
                                logger.warning("Failed to parse recommendation: " + line);
                            }
                        }
                    }
                }
            }
            
            process.waitFor();
            
        } catch (Exception e) {
            logger.severe("Error executing Python script: " + e.getMessage());
        }
        
        return recommendations;
    }
    
    private Double executePythonRatingPrediction(String script) {
        try {
            // Create temporary Python file
            File tempScript = File.createTempFile("rating_script", ".py");
            tempScript.deleteOnExit();
            
            try (FileWriter writer = new FileWriter(tempScript)) {
                writer.write(script);
            }
            
            // Execute Python script
            ProcessBuilder pb = new ProcessBuilder("python", tempScript.getAbsolutePath());
            Process process = pb.start();
            
            // Read output
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line = reader.readLine();
                if (line != null && !line.isEmpty()) {
                    return Double.parseDouble(line.trim());
                }
            }
            
            process.waitFor();
            
        } catch (Exception e) {
            logger.severe("Error executing rating prediction script: " + e.getMessage());
        }
        
        return 4.0; // Default rating
    }
    
    private Course findCourseById(List<Course> courses, Long courseId) {
        return courses.stream()
                .filter(course -> course.getId().equals(courseId))
                .findFirst()
                .orElse(null);
    }
    
    private List<CourseRecommendationDTO> getFallbackRecommendations(List<Course> courses, int limit) {
        List<CourseRecommendationDTO> fallback = new ArrayList<>();
        
        // Use simple enrollment-based recommendations as fallback
        courses.stream()
                .sorted((c1, c2) -> Integer.compare(
                    c2.getEnrollments() != null ? c2.getEnrollments().size() : 0,
                    c1.getEnrollments() != null ? c1.getEnrollments().size() : 0))
                .limit(limit)
                .forEach(course -> {
                    CourseRecommendationDTO dto = new CourseRecommendationDTO();
                    dto.setCourseId(course.getId());
                    dto.setTitle(course.getTitle());
                    dto.setDescription(course.getDescription());
                    dto.setCategory(course.getCategory() != null ? course.getCategory().getName() : "General");
                    dto.setDifficulty("Intermediate");
                    dto.setPrice(course.getPrice() != null ? course.getPrice().doubleValue() : 0.0);
                    dto.setFree(course.isFree());
                    dto.setThumbnailUrl(course.getThumbnailUrl());
                    dto.setInstructorName(course.getInstructor() != null ? 
                        (course.getInstructor().getFirstName() + " " + course.getInstructor().getLastName()) : "Unknown");
                    dto.setEnrollmentCount(course.getEnrollments() != null ? course.getEnrollments().size() : 0);
                    dto.setDurationHours(8);
                    dto.setPredictedRating(4.0); // Default rating
                    dto.setConfidenceScore(0.6);
                    dto.setRecommendationReason("Popular course recommendation (AI models not available)");
                    fallback.add(dto);
                });
        
        return fallback;
    }
    
    private List<CourseRecommendationDTO> getFallbackSimilarCourses(Long courseId, List<Course> allCourses, int limit) {
        List<CourseRecommendationDTO> similar = new ArrayList<>();
        
        // Find the target course
        Course targetCourse = findCourseById(allCourses, courseId);
        if (targetCourse == null) return similar;
        
        // Simple category-based similarity as fallback
        allCourses.stream()
                .filter(course -> !course.getId().equals(courseId))
                .filter(course -> course.getCategory() != null && targetCourse.getCategory() != null &&
                        course.getCategory().getId().equals(targetCourse.getCategory().getId()))
                .sorted((c1, c2) -> Integer.compare(
                    c2.getEnrollments() != null ? c2.getEnrollments().size() : 0,
                    c1.getEnrollments() != null ? c1.getEnrollments().size() : 0))
                .limit(limit)
                .forEach(course -> {
                    CourseRecommendationDTO dto = new CourseRecommendationDTO();
                    dto.setCourseId(course.getId());
                    dto.setTitle(course.getTitle());
                    dto.setDescription(course.getDescription());
                    dto.setCategory(course.getCategory() != null ? course.getCategory().getName() : "General");
                    dto.setDifficulty("Intermediate");
                    dto.setPrice(course.getPrice() != null ? course.getPrice().doubleValue() : 0.0);
                    dto.setFree(course.isFree());
                    dto.setThumbnailUrl(course.getThumbnailUrl());
                    dto.setInstructorName(course.getInstructor() != null ? 
                        (course.getInstructor().getFirstName() + " " + course.getInstructor().getLastName()) : "Unknown");
                    dto.setEnrollmentCount(course.getEnrollments() != null ? course.getEnrollments().size() : 0);
                    dto.setDurationHours(8);
                    dto.setPredictedRating(4.0); // Default rating
                    dto.setConfidenceScore(0.5);
                    dto.setRecommendationReason("Similar category course (AI models not available)");
                    similar.add(dto);
                });
        
        return similar;
    }
}