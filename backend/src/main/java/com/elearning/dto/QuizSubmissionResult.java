package com.elearning.dto;

import java.time.LocalDateTime;
import java.util.List;

public class QuizSubmissionResult {
    private Long attemptId;
    private int score;
    private int totalQuestions;
    private int totalPoints;
    private double percentage;
    private boolean passed;
    private String certificateUrl;
    private LocalDateTime nextAttemptAllowed;
    private List<QuestionResult> questionResults;
    private String message;

    // Constructors
    public QuizSubmissionResult() {}

    public QuizSubmissionResult(Long attemptId, int score, int totalQuestions, int totalPoints, 
                               double percentage, boolean passed, String message) {
        this.attemptId = attemptId;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.totalPoints = totalPoints;
        this.percentage = percentage;
        this.passed = passed;
        this.message = message;
    }

    // Getters and Setters
    public Long getAttemptId() { return attemptId; }
    public void setAttemptId(Long attemptId) { this.attemptId = attemptId; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }

    public int getTotalPoints() { return totalPoints; }
    public void setTotalPoints(int totalPoints) { this.totalPoints = totalPoints; }

    public double getPercentage() { return percentage; }
    public void setPercentage(double percentage) { this.percentage = percentage; }

    public boolean isPassed() { return passed; }
    public void setPassed(boolean passed) { this.passed = passed; }

    public String getCertificateUrl() { return certificateUrl; }
    public void setCertificateUrl(String certificateUrl) { this.certificateUrl = certificateUrl; }

    public LocalDateTime getNextAttemptAllowed() { return nextAttemptAllowed; }
    public void setNextAttemptAllowed(LocalDateTime nextAttemptAllowed) { this.nextAttemptAllowed = nextAttemptAllowed; }

    public List<QuestionResult> getQuestionResults() { return questionResults; }
    public void setQuestionResults(List<QuestionResult> questionResults) { this.questionResults = questionResults; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    // Inner class for individual question results
    public static class QuestionResult {
        private int questionIndex;
        private boolean correct;
        private int userAnswer;
        private int correctAnswer;
        private int pointsEarned;
        private int maxPoints;

        public QuestionResult() {}

        public QuestionResult(int questionIndex, boolean correct, int userAnswer, 
                             int correctAnswer, int pointsEarned, int maxPoints) {
            this.questionIndex = questionIndex;
            this.correct = correct;
            this.userAnswer = userAnswer;
            this.correctAnswer = correctAnswer;
            this.pointsEarned = pointsEarned;
            this.maxPoints = maxPoints;
        }

        // Getters and Setters
        public int getQuestionIndex() { return questionIndex; }
        public void setQuestionIndex(int questionIndex) { this.questionIndex = questionIndex; }

        public boolean isCorrect() { return correct; }
        public void setCorrect(boolean correct) { this.correct = correct; }

        public int getUserAnswer() { return userAnswer; }
        public void setUserAnswer(int userAnswer) { this.userAnswer = userAnswer; }

        public int getCorrectAnswer() { return correctAnswer; }
        public void setCorrectAnswer(int correctAnswer) { this.correctAnswer = correctAnswer; }

        public int getPointsEarned() { return pointsEarned; }
        public void setPointsEarned(int pointsEarned) { this.pointsEarned = pointsEarned; }

        public int getMaxPoints() { return maxPoints; }
        public void setMaxPoints(int maxPoints) { this.maxPoints = maxPoints; }
    }
}
