package com.elearning.controller;

import com.elearning.model.*;
import com.elearning.service.AdminService;
import com.elearning.dto.AdminUserDTO;
import com.elearning.dto.AdminCourseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // Dashboard Statistics
    @GetMapping("/dashboard/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStatistics() {
        Map<String, Object> stats = adminService.getDashboardStatistics();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = adminService.getDashboardStatistics();
        return ResponseEntity.ok(stats);
    }

    // User Management
    @GetMapping("/users")
    public ResponseEntity<List<AdminUserDTO>> getAllUsers() {
        List<AdminUserDTO> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/role/{role}")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable Role role) {
        List<User> users = adminService.getUsersByRole(role);
        return ResponseEntity.ok(users);
    }

    @PutMapping("/users/{userId}/status/{status}")
    public ResponseEntity<?> updateUserStatus(@PathVariable Long userId, @PathVariable UserStatus status) {
        try {
            User user = adminService.updateUserStatus(userId, status);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new AuthController.MessageResponse("Error: " + e.getMessage()));
        }
    }

    @PutMapping("/users/{userId}/role/{role}")
    public ResponseEntity<?> updateUserRole(@PathVariable Long userId, @PathVariable Role role) {
        try {
            User user = adminService.updateUserRole(userId, role);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new AuthController.MessageResponse("Error: " + e.getMessage()));
        }
    }

    @PutMapping("/users/{userId}/ban")
    public ResponseEntity<?> banUser(@PathVariable Long userId) {
        try {
            User user = adminService.banUser(userId);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new AuthController.MessageResponse("Error: " + e.getMessage()));
        }
    }

    @PutMapping("/users/{userId}/unban")
    public ResponseEntity<?> unbanUser(@PathVariable Long userId) {
        try {
            User user = adminService.unbanUser(userId);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new AuthController.MessageResponse("Error: " + e.getMessage()));
        }
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        try {
            adminService.deleteUser(userId);
            return ResponseEntity.ok(new AuthController.MessageResponse("User deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new AuthController.MessageResponse("Error: " + e.getMessage()));
        }
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest request) {
        try {
            User user = adminService.createUser(
                request.getFirstName(),
                request.getLastName(), 
                request.getEmail(),
                request.getPassword(),
                request.getRole()
            );
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new AuthController.MessageResponse("Error: " + e.getMessage()));
        }
    }

    // Inner class for request body
    public static class CreateUserRequest {
        private String firstName;
        private String lastName;
        private String email;
        private String password;
        private Role role;

        // Getters and setters
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        
        public Role getRole() { return role; }
        public void setRole(Role role) { this.role = role; }
    }

    // Course Management
    @GetMapping("/courses")
    public ResponseEntity<List<Course>> getAllCourses() {
        List<Course> courses = adminService.getAllCourses();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/courses/pending")
    public ResponseEntity<List<AdminCourseDTO>> getPendingCourses() {
        List<AdminCourseDTO> courses = adminService.getPendingCourses();
        return ResponseEntity.ok(courses);
    }

    @PutMapping("/courses/{courseId}/approve")
    public ResponseEntity<?> approveCourse(@PathVariable Long courseId) {
        try {
            Course course = adminService.approveCourse(courseId);
            return ResponseEntity.ok(course);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new AuthController.MessageResponse("Error: " + e.getMessage()));
        }
    }

    @PutMapping("/courses/{courseId}/decline")
    public ResponseEntity<?> declineCourse(@PathVariable Long courseId) {
        try {
            Course course = adminService.declineCourse(courseId);
            return ResponseEntity.ok(course);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new AuthController.MessageResponse("Error: " + e.getMessage()));
        }
    }

    @PutMapping("/courses/{courseId}/reject")
    public ResponseEntity<?> rejectCourse(@PathVariable Long courseId) {
        try {
            Course course = adminService.declineCourse(courseId);
            return ResponseEntity.ok(course);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new AuthController.MessageResponse("Error: " + e.getMessage()));
        }
    }

    @DeleteMapping("/courses/{courseId}")
    public ResponseEntity<?> deleteCourse(@PathVariable Long courseId) {
        try {
            adminService.deleteCourse(courseId);
            return ResponseEntity.ok(new AuthController.MessageResponse("Course deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new AuthController.MessageResponse("Error: " + e.getMessage()));
        }
    }
}
