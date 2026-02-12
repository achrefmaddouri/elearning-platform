package com.elearning.dto;

import java.time.LocalDateTime;

public class CourseRecommendationDTO {
    private Long courseId;
    private String title;
    private String description;
    private String category;
    private String difficulty;
    private Double price;
    private boolean isFree;
    private String thumbnailUrl;
    private String instructorName;
    private Double averageRating;
    private Integer enrollmentCount;
    private Integer durationHours;
    
    // AI-specific fields
    private Double predictedRating;
    private Double confidenceScore;
    private String recommendationReason;
    private Double relevanceScore;
    private LocalDateTime generatedAt;
    
    // Constructors
    public CourseRecommendationDTO() {}
    
    public CourseRecommendationDTO(Long courseId, String title, String category, 
                                 Double predictedRating, String recommendationReason) {
        this.courseId = courseId;
        this.title = title;
        this.category = category;
        this.predictedRating = predictedRating;
        this.recommendationReason = recommendationReason;
        this.generatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public boolean isFree() { return isFree; }
    public void setFree(boolean free) { isFree = free; }

    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }

    public String getInstructorName() { return instructorName; }
    public void setInstructorName(String instructorName) { this.instructorName = instructorName; }

    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }

    public Integer getEnrollmentCount() { return enrollmentCount; }
    public void setEnrollmentCount(Integer enrollmentCount) { this.enrollmentCount = enrollmentCount; }

    public Integer getDurationHours() { return durationHours; }
    public void setDurationHours(Integer durationHours) { this.durationHours = durationHours; }

    public Double getPredictedRating() { return predictedRating; }
    public void setPredictedRating(Double predictedRating) { this.predictedRating = predictedRating; }

    public Double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(Double confidenceScore) { this.confidenceScore = confidenceScore; }

    public String getRecommendationReason() { return recommendationReason; }
    public void setRecommendationReason(String recommendationReason) { this.recommendationReason = recommendationReason; }

    public Double getRelevanceScore() { return relevanceScore; }
    public void setRelevanceScore(Double relevanceScore) { this.relevanceScore = relevanceScore; }

    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
}