package com.elearning.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "course_analytics")
public class CourseAnalytics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false, unique = true)
    private Course course;

    @Column(name = "total_enrollments", columnDefinition = "INT DEFAULT 0")
    private Integer totalEnrollments = 0;

    @Column(name = "active_learners", columnDefinition = "INT DEFAULT 0")
    private Integer activeLearners = 0;

    @Column(name = "completion_rate", precision = 5, scale = 2)
    private BigDecimal completionRate = BigDecimal.ZERO;

    @Column(name = "average_completion_time", precision = 8, scale = 2)
    private BigDecimal averageCompletionTime = BigDecimal.ZERO; // in hours

    @Column(name = "average_rating", precision = 3, scale = 2)
    private BigDecimal averageRating = BigDecimal.ZERO;

    @Column(name = "difficulty_rating", precision = 3, scale = 2)
    private BigDecimal difficultyRating = BigDecimal.ZERO;

    @Column(name = "engagement_score", precision = 3, scale = 2)
    private BigDecimal engagementScore = BigDecimal.ZERO;

    @Column(name = "revenue", precision = 10, scale = 2)
    private BigDecimal revenue = BigDecimal.ZERO;

    @Column(name = "refund_rate", precision = 5, scale = 2)
    private BigDecimal refundRate = BigDecimal.ZERO;

    @Column(name = "last_calculated", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime lastCalculated;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    // Constructors
    public CourseAnalytics() {
        this.lastCalculated = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public CourseAnalytics(Course course) {
        this();
        this.course = course;
    }

    // Utility methods
    public String getPerformanceCategory() {
        double completionRateValue = completionRate.doubleValue();
        double engagementValue = engagementScore.doubleValue();
        
        if (completionRateValue >= 80 && engagementValue >= 0.8) {
            return "EXCELLENT";
        } else if (completionRateValue >= 60 && engagementValue >= 0.6) {
            return "GOOD";
        } else if (completionRateValue >= 40 && engagementValue >= 0.4) {
            return "AVERAGE";
        } else {
            return "NEEDS_IMPROVEMENT";
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }

    public Integer getTotalEnrollments() { return totalEnrollments; }
    public void setTotalEnrollments(Integer totalEnrollments) { this.totalEnrollments = totalEnrollments; }

    public Integer getActiveLearners() { return activeLearners; }
    public void setActiveLearners(Integer activeLearners) { this.activeLearners = activeLearners; }

    public BigDecimal getCompletionRate() { return completionRate; }
    public void setCompletionRate(BigDecimal completionRate) { this.completionRate = completionRate; }

    public BigDecimal getAverageCompletionTime() { return averageCompletionTime; }
    public void setAverageCompletionTime(BigDecimal averageCompletionTime) { this.averageCompletionTime = averageCompletionTime; }

    public BigDecimal getAverageRating() { return averageRating; }
    public void setAverageRating(BigDecimal averageRating) { this.averageRating = averageRating; }

    public BigDecimal getDifficultyRating() { return difficultyRating; }
    public void setDifficultyRating(BigDecimal difficultyRating) { this.difficultyRating = difficultyRating; }

    public BigDecimal getEngagementScore() { return engagementScore; }
    public void setEngagementScore(BigDecimal engagementScore) { this.engagementScore = engagementScore; }

    public BigDecimal getRevenue() { return revenue; }
    public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }

    public BigDecimal getRefundRate() { return refundRate; }
    public void setRefundRate(BigDecimal refundRate) { this.refundRate = refundRate; }

    public LocalDateTime getLastCalculated() { return lastCalculated; }
    public void setLastCalculated(LocalDateTime lastCalculated) { this.lastCalculated = lastCalculated; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
