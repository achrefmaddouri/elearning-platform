package com.elearning.controller;

import com.elearning.dto.ChatMessageRequest;
import com.elearning.dto.CreateChatRoomRequest;
import com.elearning.model.*;
import com.elearning.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("hasRole('USER') or hasRole('INSTRUCTOR') or hasRole('ADMIN')")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoom>> getUserChatRooms(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<ChatRoom> chatRooms = chatService.getUserChatRooms(user);
        return ResponseEntity.ok(chatRooms);
    }

    @PostMapping("/create-room")
    public ResponseEntity<?> createChatRoom(@RequestBody CreateChatRoomRequest request, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            ChatRoom chatRoom = chatService.createChatRoom(request.getCourseId(), user);
            return ResponseEntity.ok(chatRoom);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new AuthController.MessageResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/course/{courseId}/room")
    public ResponseEntity<?> getChatRoomByCourse(@PathVariable Long courseId, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            ChatRoom chatRoom = chatService.getChatRoomByCourse(courseId, user);
            return ResponseEntity.ok(chatRoom);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new AuthController.MessageResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/room/{chatRoomId}/messages")
    public ResponseEntity<?> getChatMessages(@PathVariable Long chatRoomId, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            List<ChatMessage> messages = chatService.getChatMessages(chatRoomId, user);
            return ResponseEntity.ok(messages);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new AuthController.MessageResponse("Error: " + e.getMessage()));
        }
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@Valid @RequestBody ChatMessageRequest messageRequest,
                                        Authentication authentication) {
        try {
            User sender = (User) authentication.getPrincipal();
            ChatMessage message = chatService.sendMessage(messageRequest, sender);
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new AuthController.MessageResponse("Error: " + e.getMessage()));
        }
    }

    @PutMapping("/room/{chatRoomId}/mark-read")
    public ResponseEntity<?> markMessagesAsRead(@PathVariable Long chatRoomId, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            chatService.markMessagesAsRead(chatRoomId, user);
            return ResponseEntity.ok(new AuthController.MessageResponse("Messages marked as read"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new AuthController.MessageResponse("Error: " + e.getMessage()));
        }
    }
}
