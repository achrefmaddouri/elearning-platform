package com.elearning.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.elearning.dto.AIGenerationRequest;
import com.elearning.dto.QuestionRequest;
import com.elearning.dto.QuizDTO;
import com.elearning.dto.QuizRequest;
import com.elearning.dto.QuizSubmissionRequest;
import com.elearning.dto.QuizSubmissionResult;
import com.elearning.model.Quiz;
import com.elearning.model.QuizAttempt;
import com.elearning.model.QuizQuestion;
import com.elearning.model.User;
import com.elearning.service.QuizService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/quizzes")
@CrossOrigin(origins = "*", maxAge = 3600)
public class QuizController {

    @Autowired
    private QuizService quizService;

    @PostMapping
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<?> createQuiz(@Valid @RequestBody QuizRequest quizRequest,
                                       Authentication authentication) {
        try {
            User instructor = (User) authentication.getPrincipal();
            QuizDTO quiz = quizService.createQuiz(quizRequest, instructor);
            return ResponseEntity.ok(quiz);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new AuthController.MessageResponse("Error: " + e.getMessage()));
        }
    }

    @PostMapping("/generate/{courseId}")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<?> generateQuizFromCourse(@PathVariable Long courseId,
                                                   Authentication authentication) {
        try {
            User instructor = (User) authentication.getPrincipal();
            Quiz quiz = quizService.generateQuizFromCourse(courseId, instructor);
            return ResponseEntity.ok(quiz);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new AuthController.MessageResponse("Error: " + e.getMessage()));
        }
    }

    @PostMapping("/ai-generate")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<?> generateQuestionsWithAI(@Valid @RequestBody AIGenerationRequest request,
                                                     Authentication authentication) {
        try {
            List<QuestionRequest> questions = quizService.generateQuestionsWithAI(request);
            return ResponseEntity.ok(questions);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new AuthController.MessageResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<QuizDTO>> getQuizzesByCourse(@PathVariable Long courseId) {
        List<QuizDTO> quizzes = quizService.getQuizzesByCourseAsDTO(courseId);
        return ResponseEntity.ok(quizzes);
    }

    @GetMapping("/instructor")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<List<QuizDTO>> getInstructorQuizzes(Authentication authentication) {
        User instructor = (User) authentication.getPrincipal();
        List<QuizDTO> quizzes = quizService.getInstructorQuizzes(instructor);
        return ResponseEntity.ok(quizzes);
    }

    @GetMapping("/{quizId}")
    @PreAuthorize("hasRole('USER') or hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<?> getQuiz(@PathVariable Long quizId, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Quiz quiz = quizService.getQuizById(quizId);
            List<QuizQuestion> questions = quizService.getQuizQuestions(quizId, user);
            
            // Return quiz with questions
            QuizWithQuestionsResponse response = new QuizWithQuestionsResponse(quiz, questions);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new AuthController.MessageResponse("Error: " + e.getMessage()));
        }
    }

    @PostMapping("/submit")
    @PreAuthorize("hasRole('USER') or hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<?> submitQuiz(@Valid @RequestBody QuizSubmissionRequest submissionRequest,
                                       Authentication authentication) {
        try {
            System.out.println("Quiz submission request received");
            System.out.println("Authentication: " + authentication);
            System.out.println("Authentication name: " + (authentication != null ? authentication.getName() : "null"));
            System.out.println("Submission request: " + submissionRequest);
            
            if (authentication == null || authentication.getPrincipal() == null) {
                System.out.println("Authentication or principal is null");
                return ResponseEntity.status(401)
                        .body(new AuthController.MessageResponse("Error: Authentication required"));
            }
            
            User user = (User) authentication.getPrincipal();
            System.out.println("User: " + user.getEmail() + " (ID: " + user.getId() + ") Role: " + user.getRole());
            System.out.println("User authorities: " + authentication.getAuthorities());
            
            QuizSubmissionResult result = quizService.submitQuiz(submissionRequest, user);
            System.out.println("Quiz submission result: " + result);
            
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            System.out.println("Error in quiz submission: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(new AuthController.MessageResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/attempts")
    @PreAuthorize("hasRole('USER') or hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<List<QuizAttempt>> getUserQuizAttempts(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<QuizAttempt> attempts = quizService.getUserQuizAttempts(user);
        return ResponseEntity.ok(attempts);
    }

    @DeleteMapping("/{quizId}")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteQuiz(@PathVariable Long quizId, Authentication authentication) {
        try {
            User instructor = (User) authentication.getPrincipal();
            quizService.deleteQuiz(quizId, instructor);
            return ResponseEntity.ok(new AuthController.MessageResponse("Quiz deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new AuthController.MessageResponse("Error: " + e.getMessage()));
        }
    }

    // Inner class for quiz with questions response
    public static class QuizWithQuestionsResponse {
        private Quiz quiz;
        private List<QuizQuestion> questions;

        public QuizWithQuestionsResponse(Quiz quiz, List<QuizQuestion> questions) {
            this.quiz = quiz;
            this.questions = questions;
        }

        public Quiz getQuiz() { return quiz; }
        public void setQuiz(Quiz quiz) { this.quiz = quiz; }

        public List<QuizQuestion> getQuestions() { return questions; }
        public void setQuestions(List<QuizQuestion> questions) { this.questions = questions; }
    }
}
