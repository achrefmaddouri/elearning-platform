package com.elearning;

import org.springframework.security.core.Authentication;

import com.elearning.model.Role;
import com.elearning.model.User;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestUtils {
    public static User buildUser(Long id, String email, Role role) {
        User u = new User();
        u.setId(id);
        u.setEmail(email != null ? email : "user@example.com");
        u.setFirstName("First");
        u.setLastName("Last");
        u.setPassword("password");
        u.setRole(role != null ? role : Role.USER);
        u.setEmailVerified(true);
        return u;
    }

    public static Authentication mockAuthenticationWith(User user) {
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(user);
        return auth;
    }

    // Builder for Course (minimal fields used in tests)
    public static com.elearning.model.Course buildCourse(Long id, String title, User instructor) {
        com.elearning.model.Course c = new com.elearning.model.Course();
        c.setId(id);
        c.setTitle(title != null ? title : "Course Title");
        c.setDescription("Description");
        if (instructor != null) c.setInstructor(instructor);
        return c;
    }

    // Builder for QuizSubmissionRequest (minimal)
    public static com.elearning.dto.QuizSubmissionRequest buildQuizSubmissionRequest(Long quizId) {
        com.elearning.dto.QuizSubmissionRequest req = new com.elearning.dto.QuizSubmissionRequest();
        req.setQuizId(quizId != null ? quizId : 1L);
        return req;
    }

    // Builder for ChatMessageRequest
    public static com.elearning.dto.ChatMessageRequest buildChatMessageRequest(Long chatRoomId, String message) {
        com.elearning.dto.ChatMessageRequest r = new com.elearning.dto.ChatMessageRequest();
        r.setChatRoomId(chatRoomId != null ? chatRoomId : 1L);
        r.setMessage(message != null ? message : "hi");
        return r;
    }

    // Builder for QuizRequest (minimal)
    public static com.elearning.dto.QuizRequest buildQuizRequest(String title) {
        com.elearning.dto.QuizRequest r = new com.elearning.dto.QuizRequest();
        r.setTitle(title != null ? title : "Quiz");
        return r;
    }

    // Return a mocked ChatMessage instance for tests
    public static com.elearning.model.ChatMessage mockChatMessage() {
        return mock(com.elearning.model.ChatMessage.class);
    }

    // Return a mocked QuizSubmissionResult instance for tests
    public static com.elearning.dto.QuizSubmissionResult mockQuizSubmissionResult() {
        return mock(com.elearning.dto.QuizSubmissionResult.class);
    }

    // Builder for CourseFile
    public static com.elearning.model.CourseFile buildCourseFile(Long id, String name) {
        com.elearning.model.CourseFile f = new com.elearning.model.CourseFile();
        f.setId(id != null ? id : 1L);
        f.setFileName(name != null ? name : "file.pdf");
        f.setFilePath("/uploads/file.pdf");
        return f;
    }

    // Builder for QuizQuestion
    public static com.elearning.model.QuizQuestion buildQuizQuestion(Long id, String text) {
        com.elearning.model.QuizQuestion q = new com.elearning.model.QuizQuestion();
        q.setId(id != null ? id : 1L);
        q.setQuestion(text != null ? text : "What is 2+2?");
        return q;
    }

    // Builder for QuizDTO
    public static com.elearning.dto.QuizDTO buildQuizDTO(Long id, String title) {
        com.elearning.dto.QuizDTO d = new com.elearning.dto.QuizDTO();
        d.setId(id != null ? id : 1L);
        d.setTitle(title != null ? title : "Sample Quiz");
        return d;
    }

    // Builder for QuizAttempt
    public static com.elearning.model.QuizAttempt buildQuizAttempt(Long id) {
        com.elearning.model.QuizAttempt a = new com.elearning.model.QuizAttempt();
        a.setId(id != null ? id : 1L);
        return a;
    }

    // Builder for CourseRecommendationDTO
    public static com.elearning.dto.CourseRecommendationDTO buildCourseRecommendationDTO(Long courseId, String title) {
        return new com.elearning.dto.CourseRecommendationDTO(courseId != null ? courseId : 1L, title != null ? title : "T", "General", 4.0, "Because");
    }
}
