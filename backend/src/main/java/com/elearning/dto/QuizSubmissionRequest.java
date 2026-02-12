package com.elearning.dto;

import java.util.List;

public class QuizSubmissionRequest {
    private Long quizId;
    private List<Integer> answers;

    // Constructors
    public QuizSubmissionRequest() {}

    public QuizSubmissionRequest(Long quizId, List<Integer> answers) {
        this.quizId = quizId;
        this.answers = answers;
    }

    // Getters and Setters
    public Long getQuizId() { return quizId; }
    public void setQuizId(Long quizId) { this.quizId = quizId; }

    public List<Integer> getAnswers() { return answers; }
    public void setAnswers(List<Integer> answers) { this.answers = answers; }
}
