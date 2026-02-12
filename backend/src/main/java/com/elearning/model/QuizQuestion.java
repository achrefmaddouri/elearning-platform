package com.elearning.model;

import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
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
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "quiz_questions")
public class QuizQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(columnDefinition = "TEXT")
    private String question;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_type")
    private QuestionType questionType = QuestionType.MULTIPLE_CHOICE;

    @ElementCollection
    @CollectionTable(name = "quiz_options", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "option_text")
    private List<String> options;

    @ElementCollection
    @CollectionTable(name = "quiz_correct_answers", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "answer_index")
    private List<Integer> correctAnswers;

    private int correctAnswer; // For backward compatibility

    private int points = 10;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    // Enum for question types
    public enum QuestionType {
        MULTIPLE_CHOICE,
        TRUE_FALSE,
        SHORT_ANSWER
    }

    // Constructors
    public QuizQuestion() {}

    public QuizQuestion(String question, List<String> options, int correctAnswer, Quiz quiz) {
        this.question = question;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.quiz = quiz;
        this.questionType = QuestionType.MULTIPLE_CHOICE;
    }

    public QuizQuestion(String question, QuestionType questionType, List<String> options, 
                       List<Integer> correctAnswers, int points, Quiz quiz) {
        this.question = question;
        this.questionType = questionType;
        this.options = options;
        this.correctAnswers = correctAnswers;
        this.points = points;
        this.quiz = quiz;
        
        // Set backward compatibility field
        if (correctAnswers != null && !correctAnswers.isEmpty()) {
            this.correctAnswer = correctAnswers.get(0);
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public QuestionType getQuestionType() { return questionType; }
    public void setQuestionType(QuestionType questionType) { this.questionType = questionType; }

    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }

    public List<Integer> getCorrectAnswers() { return correctAnswers; }
    public void setCorrectAnswers(List<Integer> correctAnswers) { 
        this.correctAnswers = correctAnswers; 
        // Update backward compatibility field
        if (correctAnswers != null && !correctAnswers.isEmpty()) {
            this.correctAnswer = correctAnswers.get(0);
        }
    }

    public int getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(int correctAnswer) { this.correctAnswer = correctAnswer; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }

    public Quiz getQuiz() { return quiz; }
    public void setQuiz(Quiz quiz) { this.quiz = quiz; }
}
