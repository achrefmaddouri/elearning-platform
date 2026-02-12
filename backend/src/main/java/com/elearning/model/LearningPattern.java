package com.elearning.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "learning_patterns")
public class LearningPattern {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "peak_hour")
    private Integer peakHour; // 0-23

    @Column(name = "preferred_duration")
    private Integer preferredDuration; // minutes

    @Column(name = "learning_velocity")
    private Double learningVelocity; // courses per week

    @Column(name = "consistency_score")
    private Double consistencyScore = 0.0; // 0.00 to 1.00

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty_preference")
    private DifficultyPreference difficultyPreference;

    @Column(name = "last_analyzed", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime lastAnalyzed;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    public enum DifficultyPreference {
        BEGINNER,
        INTERMEDIATE,
        ADVANCED,
        MIXED
    }

    // Constructors
    public LearningPattern() {
        this.lastAnalyzed = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public LearningPattern(User user) {
        this();
        this.user = user;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Integer getPeakHour() { return peakHour; }
    public void setPeakHour(Integer peakHour) { this.peakHour = peakHour; }

    public Integer getPreferredDuration() { return preferredDuration; }
    public void setPreferredDuration(Integer preferredDuration) { this.preferredDuration = preferredDuration; }

    public Double getLearningVelocity() { return learningVelocity; }
    public void setLearningVelocity(Double learningVelocity) { this.learningVelocity = learningVelocity; }

    public Double getConsistencyScore() { return consistencyScore; }
    public void setConsistencyScore(Double consistencyScore) { this.consistencyScore = consistencyScore; }

    public DifficultyPreference getDifficultyPreference() { return difficultyPreference; }
    public void setDifficultyPreference(DifficultyPreference difficultyPreference) { this.difficultyPreference = difficultyPreference; }

    public LocalDateTime getLastAnalyzed() { return lastAnalyzed; }
    public void setLastAnalyzed(LocalDateTime lastAnalyzed) { this.lastAnalyzed = lastAnalyzed; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
