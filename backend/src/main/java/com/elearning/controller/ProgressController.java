package com.elearning.controller;

import com.elearning.model.*;
import com.elearning.service.ProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/progress")
@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("hasRole('USER') or hasRole('INSTRUCTOR') or hasRole('ADMIN')")
public class ProgressController {

    @Autowired
    private ProgressService progressService;

    @GetMapping
    public ResponseEntity<List<Progress>> getUserProgress(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<Progress> progress = progressService.getUserProgress(user);
        return ResponseEntity.ok(progress);
    }

    @GetMapping("/completed")
    public ResponseEntity<List<Progress>> getUserCompletedCourses(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<Progress> completedCourses = progressService.getUserCompletedCourses(user);
        return ResponseEntity.ok(completedCourses);
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<?> getCourseProgress(@PathVariable Long courseId, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Progress progress = progressService.getCourseProgress(courseId, user);
            return ResponseEntity.ok(progress);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new AuthController.MessageResponse("Error: " + e.getMessage()));
        }
    }

    @PutMapping("/course/{courseId}")
    public ResponseEntity<?> updateProgress(@PathVariable Long courseId,
                                           @RequestParam int progressPercentage,
                                           Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Progress progress = progressService.updateProgress(courseId, progressPercentage, user);
            return ResponseEntity.ok(progress);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new AuthController.MessageResponse("Error: " + e.getMessage()));
        }
    }
}
