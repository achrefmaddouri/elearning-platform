package com.elearning.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.elearning.dto.CourseDTO;
import com.elearning.dto.CourseRequest;
import com.elearning.model.Course;
import com.elearning.model.User;
import com.elearning.service.CourseService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/instructor")
@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
public class InstructorController {

    @Autowired
    private CourseService courseService;

    @PostMapping("/courses")
    public ResponseEntity<?> createCourse(@Valid @RequestBody CourseRequest courseRequest, 
                                         Authentication authentication) {
        try {
            User instructor = (User) authentication.getPrincipal();
            Course course = courseService.createCourse(courseRequest, instructor);
            CourseDTO courseDTO = new CourseDTO(course);
            return ResponseEntity.ok(courseDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new AuthController.MessageResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/courses")
    public ResponseEntity<List<CourseDTO>> getInstructorCourses(Authentication authentication) {
        User instructor = (User) authentication.getPrincipal();
        List<Course> courses = courseService.getInstructorCourses(instructor);
        List<CourseDTO> courseDTOs = courses.stream()
                .map(CourseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(courseDTOs);
    }

    @PutMapping("/courses/{courseId}")
    public ResponseEntity<?> updateCourse(@PathVariable Long courseId,
                                         @Valid @RequestBody CourseRequest courseRequest,
                                         Authentication authentication) {
        try {
            User instructor = (User) authentication.getPrincipal();
            Course course = courseService.updateCourse(courseId, courseRequest, instructor);
            CourseDTO courseDTO = new CourseDTO(course);
            return ResponseEntity.ok(courseDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new AuthController.MessageResponse("Error: " + e.getMessage()));
        }
    }

    @DeleteMapping("/courses/{courseId}")
    public ResponseEntity<?> deleteCourse(@PathVariable Long courseId, Authentication authentication) {
        try {
            User instructor = (User) authentication.getPrincipal();
            courseService.deleteCourse(courseId, instructor);
            return ResponseEntity.ok(new AuthController.MessageResponse("Course deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new AuthController.MessageResponse("Error: " + e.getMessage()));
        }
    }

    @PostMapping("/courses/{courseId}/thumbnail")
    public ResponseEntity<?> uploadCourseThumbnail(@PathVariable Long courseId,
                                                   @RequestParam("thumbnail") MultipartFile thumbnail,
                                                   Authentication authentication) {
        try {
            User instructor = (User) authentication.getPrincipal();
            
            // Validate file
            if (thumbnail.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new AuthController.MessageResponse("Error: Thumbnail file cannot be empty"));
            }

            // Check if it's an image
            String contentType = thumbnail.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest()
                        .body(new AuthController.MessageResponse("Error: Only image files are allowed for thumbnails"));
            }

            // Check file size (5MB limit)
            if (thumbnail.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest()
                        .body(new AuthController.MessageResponse("Error: Thumbnail size cannot exceed 5MB"));
            }

            String thumbnailUrl = courseService.uploadCourseThumbnail(courseId, thumbnail, instructor);
            return ResponseEntity.ok(new ThumbnailResponse(thumbnailUrl));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new AuthController.MessageResponse("Error: " + e.getMessage()));
        }
    }

    // Response class for thumbnail upload
    public static class ThumbnailResponse {
        private String thumbnailUrl;

        public ThumbnailResponse(String thumbnailUrl) {
            this.thumbnailUrl = thumbnailUrl;
        }

        public String getThumbnailUrl() {
            return thumbnailUrl;
        }

        public void setThumbnailUrl(String thumbnailUrl) {
            this.thumbnailUrl = thumbnailUrl;
        }
    }
}
