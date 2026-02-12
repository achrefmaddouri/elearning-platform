package com.elearning.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_gamification")
public class UserGamification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @Column(name = "total_points")
    private Integer totalPoints = 0;

    @Column(name = "current_login_streak")
    private Integer currentLoginStreak = 0;

    @Column(name = "longest_login_streak")
    private Integer longestLoginStreak = 0;

    @Column(name = "last_login_date")
    private LocalDate lastLoginDate;

    @Column(name = "current_course_streak")
    private Integer currentCourseStreak = 0;

    @Column(name = "current_quiz_streak")
    private Integer currentQuizStreak = 0;

    @Column(name = "streak_freeze_tokens")
    private Integer streakFreezeTokens = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Constructors
    public UserGamification() {}

    public UserGamification(User user) {
        this.user = user;
        this.totalPoints = 0;
        this.currentLoginStreak = 0;
        this.longestLoginStreak = 0;
        this.currentCourseStreak = 0;
        this.currentQuizStreak = 0;
        this.streakFreezeTokens = 0;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Integer getTotalPoints() { return totalPoints; }
    public void setTotalPoints(Integer totalPoints) { 
        this.totalPoints = totalPoints;
        this.updatedAt = LocalDateTime.now();
    }

    public Integer getCurrentLoginStreak() { return currentLoginStreak; }
    public void setCurrentLoginStreak(Integer currentLoginStreak) { 
        this.currentLoginStreak = currentLoginStreak;
        if (currentLoginStreak > longestLoginStreak) {
            this.longestLoginStreak = currentLoginStreak;
        }
        this.updatedAt = LocalDateTime.now();
    }

    public Integer getLongestLoginStreak() { return longestLoginStreak; }
    public void setLongestLoginStreak(Integer longestLoginStreak) { 
        this.longestLoginStreak = longestLoginStreak;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDate getLastLoginDate() { return lastLoginDate; }
    public void setLastLoginDate(LocalDate lastLoginDate) { 
        this.lastLoginDate = lastLoginDate;
        this.updatedAt = LocalDateTime.now();
    }

    public Integer getCurrentCourseStreak() { return currentCourseStreak; }
    public void setCurrentCourseStreak(Integer currentCourseStreak) { 
        this.currentCourseStreak = currentCourseStreak;
        this.updatedAt = LocalDateTime.now();
    }

    public Integer getCurrentQuizStreak() { return currentQuizStreak; }
    public void setCurrentQuizStreak(Integer currentQuizStreak) { 
        this.currentQuizStreak = currentQuizStreak;
        this.updatedAt = LocalDateTime.now();
    }

    public Integer getStreakFreezeTokens() { return streakFreezeTokens; }
    public void setStreakFreezeTokens(Integer streakFreezeTokens) { 
        this.streakFreezeTokens = streakFreezeTokens;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
