package com.elearning.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.elearning.dto.ChangePasswordRequest;
import com.elearning.dto.UpdateUserRequest;
import com.elearning.model.User;
import com.elearning.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER') or hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<User> getUserProfile(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        User userProfile = userService.getUserById(user.getId());
        return ResponseEntity.ok(userProfile);
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('USER') or hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<User> updateUserProfile(
            @Valid @RequestBody UpdateUserRequest updateRequest,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        User updatedUser = userService.updateUserProfile(user.getId(), updateRequest);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/change-password")
    @PreAuthorize("hasRole('USER') or hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<?> changePassword(
            @Valid @RequestBody ChangePasswordRequest changePasswordRequest,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        userService.changePassword(user.getId(), changePasswordRequest);
        return ResponseEntity.ok().body(new MessageResponse("Password changed successfully"));
    }

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
