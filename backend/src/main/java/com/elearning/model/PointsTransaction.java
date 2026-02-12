package com.elearning.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "points_transactions")
public class PointsTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "points", nullable = false)
    private Integer points;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false)
    private SourceType sourceType;

    @Column(name = "source_id")
    private Long sourceId;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "multiplier", precision = 3, scale = 2)
    private BigDecimal multiplier = BigDecimal.ONE;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Constructors
    public PointsTransaction() {}

    public PointsTransaction(User user, Integer points, TransactionType transactionType, 
                           SourceType sourceType, String description) {
        this.user = user;
        this.points = points;
        this.transactionType = transactionType;
        this.sourceType = sourceType;
        this.description = description;
    }

    public PointsTransaction(User user, Integer points, TransactionType transactionType, 
                           SourceType sourceType, Long sourceId, String description, BigDecimal multiplier) {
        this.user = user;
        this.points = points;
        this.transactionType = transactionType;
        this.sourceType = sourceType;
        this.sourceId = sourceId;
        this.description = description;
        this.multiplier = multiplier;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Integer getPoints() { return points; }
    public void setPoints(Integer points) { this.points = points; }

    public TransactionType getTransactionType() { return transactionType; }
    public void setTransactionType(TransactionType transactionType) { this.transactionType = transactionType; }

    public SourceType getSourceType() { return sourceType; }
    public void setSourceType(SourceType sourceType) { this.sourceType = sourceType; }

    public Long getSourceId() { return sourceId; }
    public void setSourceId(Long sourceId) { this.sourceId = sourceId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getMultiplier() { return multiplier; }
    public void setMultiplier(BigDecimal multiplier) { this.multiplier = multiplier; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // Enums
    public enum TransactionType {
        EARNED, SPENT, BONUS, PENALTY
    }

    public enum SourceType {
        COURSE_COMPLETE, QUIZ_PASS, LOGIN_STREAK, DAILY_LOGIN, BADGE_EARNED, PURCHASE, ADMIN_ADJUSTMENT
    }
}
