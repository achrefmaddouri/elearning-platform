package com.elearning.dto;

import com.elearning.model.Quiz;

import java.time.LocalDateTime;

public class QuizDTO {
    private Long id;
    private String title;
    private String description;
    private Long courseId;
    private String courseTitle;
    private Long createdBy;
    private String creatorName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int questionsCount;
    private int attemptsCount;

    // Constructors
    public QuizDTO() {}

    public QuizDTO(Quiz quiz) {
        this.id = quiz.getId();
        this.title = quiz.getTitle();
        this.description = quiz.getDescription();
        this.courseId = quiz.getCourse().getId();
        this.courseTitle = quiz.getCourse().getTitle();
        this.createdBy = quiz.getCreatedBy().getId();
        this.creatorName = quiz.getCreatedBy().getFirstName() + " " + quiz.getCreatedBy().getLastName();
        this.createdAt = quiz.getCreatedAt();
        this.updatedAt = quiz.getUpdatedAt();
        
        // Don't access lazy collections to avoid lazy loading issues
        this.questionsCount = 0;
        this.attemptsCount = 0;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public String getCourseTitle() { return courseTitle; }
    public void setCourseTitle(String courseTitle) { this.courseTitle = courseTitle; }

    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }

    public String getCreatorName() { return creatorName; }
    public void setCreatorName(String creatorName) { this.creatorName = creatorName; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public int getQuestionsCount() { return questionsCount; }
    public void setQuestionsCount(int questionsCount) { this.questionsCount = questionsCount; }

    public int getAttemptsCount() { return attemptsCount; }
    public void setAttemptsCount(int attemptsCount) { this.attemptsCount = attemptsCount; }
}
