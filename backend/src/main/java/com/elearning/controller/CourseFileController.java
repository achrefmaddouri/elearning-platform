package com.elearning.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.elearning.model.CourseFile;
import com.elearning.model.User;
import com.elearning.service.CourseFileService;

@RestController
@RequestMapping("/api/course-files")
public class CourseFileController {

    @Autowired
    private CourseFileService courseFileService;

    @PostMapping("/upload/{courseId}")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<?> uploadCourseFile(
            @PathVariable Long courseId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "fileName", required = false) String fileName,
            Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Error: File cannot be empty"));
            }

            // Check file size (100MB limit)
            if (file.getSize() > 100 * 1024 * 1024) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Error: File size cannot exceed 100MB"));
            }

            CourseFile courseFile = courseFileService.uploadCourseFile(courseId, file, fileName, user);
            return ResponseEntity.ok(courseFile);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<CourseFile>> getCourseFiles(
            @PathVariable Long courseId,
            Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            List<CourseFile> files = courseFileService.getCourseFiles(courseId, user);
            return ResponseEntity.ok(files);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{fileId}/download")
    public ResponseEntity<ByteArrayResource> downloadFile(
            @PathVariable Long fileId,
            Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            
            // Get file content and metadata - this method handles permissions internally
            byte[] fileContent = courseFileService.getFileContent(fileId, user);
            
            // Get the course file for metadata (filename)
            CourseFile courseFile = courseFileService.getCourseFileById(fileId, user);
            ByteArrayResource resource = new ByteArrayResource(fileContent);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=\"" + courseFile.getFileName() + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(fileContent.length)
                    .body(resource);

        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{fileId}")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<?> updateFileName(
            @PathVariable Long fileId,
            @RequestParam("fileName") String fileName,
            Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            CourseFile updatedFile = courseFileService.updateCourseFile(fileId, fileName, user);
            return ResponseEntity.ok(updatedFile);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{fileId}")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteFile(
            @PathVariable Long fileId,
            Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            courseFileService.deleteCourseFile(fileId, user);
            return ResponseEntity.ok(new MessageResponse("File deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    // Message response class
    public static class MessageResponse {
        private String message;

        public MessageResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
