package com.elearning.dto;

import java.util.List;

public class QuestionRequest {
    private String questionText;
    private String questionType;
    private int points;
    private List<String> options;
    private List<Integer> correctAnswers;

    // Constructors
    public QuestionRequest() {}

    public QuestionRequest(String questionText, String questionType, int points, 
                          List<String> options, List<Integer> correctAnswers) {
        this.questionText = questionText;
        this.questionType = questionType;
        this.points = points;
        this.options = options;
        this.correctAnswers = correctAnswers;
    }

    // Getters and Setters
    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public String getQuestionType() { return questionType; }
    public void setQuestionType(String questionType) { this.questionType = questionType; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }

    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }

    public List<Integer> getCorrectAnswers() { return correctAnswers; }
    public void setCorrectAnswers(List<Integer> correctAnswers) { this.correctAnswers = correctAnswers; }
}
