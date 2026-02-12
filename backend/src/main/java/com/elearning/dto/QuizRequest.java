package com.elearning.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

public class QuizRequest {
    @NotBlank
    private String title;

    private String description;

    private Long courseId;

    private Integer timeLimit;
    private Integer passingScore;
    private Integer maxAttempts;
    private Boolean showCorrectAnswers;
    private Boolean randomizeQuestions;
    private List<QuestionRequest> questions;

    // Constructors
    public QuizRequest() {}

    public QuizRequest(String title, String description, Long courseId) {
        this.title = title;
        this.description = description;
        this.courseId = courseId;
    }

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public Integer getTimeLimit() { return timeLimit; }
    public void setTimeLimit(Integer timeLimit) { this.timeLimit = timeLimit; }

    public Integer getPassingScore() { return passingScore; }
    public void setPassingScore(Integer passingScore) { this.passingScore = passingScore; }

    public Integer getMaxAttempts() { return maxAttempts; }
    public void setMaxAttempts(Integer maxAttempts) { this.maxAttempts = maxAttempts; }

    public Boolean getShowCorrectAnswers() { return showCorrectAnswers; }
    public void setShowCorrectAnswers(Boolean showCorrectAnswers) { this.showCorrectAnswers = showCorrectAnswers; }

    public Boolean getRandomizeQuestions() { return randomizeQuestions; }
    public void setRandomizeQuestions(Boolean randomizeQuestions) { this.randomizeQuestions = randomizeQuestions; }

    public List<QuestionRequest> getQuestions() { return questions; }
    public void setQuestions(List<QuestionRequest> questions) { this.questions = questions; }
}
