package com.elearning.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import com.elearning.dto.AIGenerationRequest;
import com.elearning.dto.QuizRequest;
import com.elearning.dto.QuizSubmissionRequest;
import com.elearning.dto.QuizSubmissionResult;
import com.elearning.model.Quiz;
import com.elearning.model.QuizAttempt;
import com.elearning.model.QuizQuestion;
import com.elearning.model.User;
import com.elearning.service.QuizService;

import java.util.List;

public class QuizControllerUnitTest {

    @Mock
    private QuizService quizService;

    @InjectMocks
    private QuizController controller;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void createQuiz_returnsOk() {
        User instructor = com.elearning.TestUtils.buildUser(22L, "inst@example.com", com.elearning.model.Role.INSTRUCTOR);
        Authentication auth = com.elearning.TestUtils.mockAuthenticationWith(instructor);

        com.elearning.dto.QuizRequest req = com.elearning.TestUtils.buildQuizRequest("Sample Quiz");
        when(quizService.createQuiz(req, instructor)).thenReturn(null);

        ResponseEntity<?> resp = controller.createQuiz(req, auth);
        // service returned null in this unit test mock; controller will still return 200 with null body
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    public void generateQuizFromCourse_returnsOk() {
        Authentication auth = mock(Authentication.class);
        User instructor = mock(User.class);
        when(auth.getPrincipal()).thenReturn(instructor);

        Quiz quiz = mock(Quiz.class);
        when(quizService.generateQuizFromCourse(1L, instructor)).thenReturn(quiz);

        ResponseEntity<?> resp = controller.generateQuizFromCourse(1L, auth);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    public void generateQuestionsWithAI_returnsOk() {
        AIGenerationRequest req = new AIGenerationRequest();
        when(quizService.generateQuestionsWithAI(req)).thenReturn(Collections.emptyList());

        Authentication auth = mock(Authentication.class);
        User instructor = mock(User.class);
        when(auth.getPrincipal()).thenReturn(instructor);

        ResponseEntity<?> resp = controller.generateQuestionsWithAI(req, auth);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    public void getQuizzesByCourse_returnsOk() {
        when(quizService.getQuizzesByCourseAsDTO(2L)).thenReturn(Collections.emptyList());
        ResponseEntity<?> resp = controller.getQuizzesByCourse(2L);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    public void getQuiz_returnsOk() {
        Authentication auth = mock(Authentication.class);
        User user = mock(User.class);
        when(auth.getPrincipal()).thenReturn(user);

        Quiz quiz = mock(Quiz.class);
        when(quizService.getQuizById(3L)).thenReturn(quiz);
        when(quizService.getQuizQuestions(3L, user)).thenReturn(Collections.emptyList());

        ResponseEntity<?> resp = controller.getQuiz(3L, auth);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    public void submitQuiz_returns401_whenAuthNull() {
        QuizSubmissionRequest submission = new QuizSubmissionRequest();
        ResponseEntity<?> resp = controller.submitQuiz(submission, null);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    @Test
    public void submitQuiz_returnsOk_onSuccess() {
        Authentication auth = mock(Authentication.class);
        User user = mock(User.class);
        when(auth.getPrincipal()).thenReturn(user);
        when(user.getId()).thenReturn(11L);

        com.elearning.dto.QuizSubmissionRequest submission = com.elearning.TestUtils.buildQuizSubmissionRequest(7L);
        com.elearning.dto.QuizSubmissionResult result = com.elearning.TestUtils.mockQuizSubmissionResult();
        when(quizService.submitQuiz(submission, user)).thenReturn(result);

        ResponseEntity<?> resp = controller.submitQuiz(submission, auth);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    public void getUserQuizAttempts_returnsOk() {
        Authentication auth = mock(Authentication.class);
        User user = mock(User.class);
        when(auth.getPrincipal()).thenReturn(user);

        when(quizService.getUserQuizAttempts(user)).thenReturn(Collections.emptyList());
        ResponseEntity<List<QuizAttempt>> resp = controller.getUserQuizAttempts(auth);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    public void deleteQuiz_returnsOk() {
        Authentication auth = mock(Authentication.class);
        User instructor = mock(User.class);
        when(auth.getPrincipal()).thenReturn(instructor);

        doNothing().when(quizService).deleteQuiz(4L, instructor);
        ResponseEntity<?> resp = controller.deleteQuiz(4L, auth);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }
}
