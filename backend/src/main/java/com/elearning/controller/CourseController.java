package com.elearning.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.elearning.dto.CourseDTO;
import com.elearning.model.Course;
import com.elearning.model.CourseFile;
import com.elearning.model.CourseStatus;
import com.elearning.model.CourseVisibility;
import com.elearning.model.User;
import com.elearning.service.CourseService;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping("/public")
    public ResponseEntity<List<CourseDTO>> getPublicCourses() {
        List<CourseDTO> courses = courseService.getPublicCourses();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable Long courseId) {
        Course course = courseService.getCourseById(courseId);
        if (course.getVisibility() != CourseVisibility.PUBLIC || course.getStatus() != CourseStatus.ACCEPTED) {
            return ResponseEntity.notFound().build();
        }
        CourseDTO courseDTO = new CourseDTO(course);
        return ResponseEntity.ok(courseDTO);
    }

    @GetMapping("/private/{privateUrl}")
    public ResponseEntity<Course> getCourseByPrivateUrl(@PathVariable String privateUrl) {
        Course course = courseService.getCourseByPrivateUrl(privateUrl);
        return ResponseEntity.ok(course);
    }

    @PostMapping("/{courseId}/enroll")
    @PreAuthorize("hasRole('USER') or hasRole('INSTRUCTOR')")
    public ResponseEntity<?> enrollInCourse(@PathVariable Long courseId, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Course course = courseService.getCourseById(courseId);
            courseService.enrollUserInCourse(user, course);
            return ResponseEntity.ok(new AuthController.MessageResponse("Successfully enrolled in course"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new AuthController.MessageResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/{courseId}/files")
    @PreAuthorize("hasRole('USER') or hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<List<CourseFile>> getCourseFiles(@PathVariable Long courseId, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<CourseFile> files = courseService.getCourseFiles(courseId, user);
        return ResponseEntity.ok(files);
    }

    @PostMapping("/{courseId}/upload")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<?> uploadFile(@PathVariable Long courseId, 
                                       @RequestParam("file") MultipartFile file,
                                       Authentication authentication) {
        try {
            User instructor = (User) authentication.getPrincipal();
            String filePath = courseService.uploadFile(courseId, file, instructor);
            return ResponseEntity.ok(new AuthController.MessageResponse("File uploaded successfully: " + filePath));
        } catch (IOException e) {
            return ResponseEntity.badRequest()
                    .body(new AuthController.MessageResponse("Error uploading file: " + e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new AuthController.MessageResponse("Error: " + e.getMessage()));
        }
    }
}
