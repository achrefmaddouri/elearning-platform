package com.elearning.dto;

import jakarta.validation.constraints.NotBlank;

public class AIGenerationRequest {
    @NotBlank
    private String prompt;
    
    private String quizTitle;
    private String quizDescription;
    private int numberOfQuestions = 5;

    // Constructors
    public AIGenerationRequest() {}

    public AIGenerationRequest(String prompt) {
        this.prompt = prompt;
    }

    // Getters and Setters
    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { this.prompt = prompt; }

    public String getQuizTitle() { return quizTitle; }
    public void setQuizTitle(String quizTitle) { this.quizTitle = quizTitle; }

    public String getQuizDescription() { return quizDescription; }
    public void setQuizDescription(String quizDescription) { this.quizDescription = quizDescription; }

    public int getNumberOfQuestions() { return numberOfQuestions; }
    public void setNumberOfQuestions(int numberOfQuestions) { this.numberOfQuestions = numberOfQuestions; }
}
