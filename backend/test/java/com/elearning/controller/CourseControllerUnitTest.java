package com.elearning.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import com.elearning.model.Course;
import com.elearning.model.CourseFile;
import com.elearning.model.CourseStatus;
import com.elearning.model.CourseVisibility;
import com.elearning.model.User;
import com.elearning.service.CourseService;

public class CourseControllerUnitTest {

    @Mock
    private CourseService courseService;

    @InjectMocks
    private CourseController controller;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getPublicCourses_returnsOk() {
        when(courseService.getPublicCourses()).thenReturn(Collections.emptyList());
        ResponseEntity<List<?>> resp = controller.getPublicCourses();
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(0, resp.getBody().size());
    }

    @Test
    public void getCourseById_returnsOk_whenPublicAndAccepted() {
        Course course = mock(Course.class);
        when(course.getVisibility()).thenReturn(CourseVisibility.PUBLIC);
        when(course.getStatus()).thenReturn(CourseStatus.ACCEPTED);
        when(courseService.getCourseById(1L)).thenReturn(course);

        ResponseEntity<?> resp = controller.getCourseById(1L);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    public void getCourseById_returnsNotFound_whenNotPublicOrNotAccepted() {
        Course course = mock(Course.class);
        when(course.getVisibility()).thenReturn(CourseVisibility.PRIVATE);
        when(course.getStatus()).thenReturn(CourseStatus.DRAFT);
        when(courseService.getCourseById(2L)).thenReturn(course);

        ResponseEntity<?> resp = controller.getCourseById(2L);
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    @Test
    public void enrollInCourse_returnsOk_onSuccess() {
        Authentication auth = mock(Authentication.class);
        User user = mock(User.class);
        when(auth.getPrincipal()).thenReturn(user);

        Course course = mock(Course.class);
        when(courseService.getCourseById(3L)).thenReturn(course);
        doNothing().when(courseService).enrollUserInCourse(user, course);

        ResponseEntity<?> resp = controller.enrollInCourse(3L, auth);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    public void getCourseFiles_returnsOk() {
        Authentication auth = mock(Authentication.class);
        User user = mock(User.class);
        when(auth.getPrincipal()).thenReturn(user);

        when(courseService.getCourseFiles(4L, user)).thenReturn(Collections.emptyList());

        ResponseEntity<List<CourseFile>> resp = controller.getCourseFiles(4L, auth);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(0, resp.getBody().size());
    }

    // Note: uploadFile interacts with MultipartFile and may throw IOException. We test success path.
    @Test
    public void uploadFile_returnsOk_onSuccess() throws IOException {
        Authentication auth = mock(Authentication.class);
        User instructor = mock(User.class);
        when(auth.getPrincipal()).thenReturn(instructor);

        MultipartFile file = mock(MultipartFile.class);
        when(courseService.uploadFile(5L, file, instructor)).thenReturn("/uploads/file.pdf");

        ResponseEntity<?> resp = controller.uploadFile(5L, file, auth);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }
}
