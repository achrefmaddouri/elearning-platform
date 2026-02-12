package com.elearning.controller;

import com.elearning.dto.ChatMessageRequest;
import com.elearning.dto.TypingRequest;
import com.elearning.model.ChatMessage;
import com.elearning.model.User;
import com.elearning.service.ChatService;
import com.elearning.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class WebSocketChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessageRequest chatMessage, 
                           SimpMessageHeaderAccessor headerAccessor,
                           Principal principal) {
        try {
            User sender = userRepository.findByEmail(principal.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            ChatMessage savedMessage = chatService.sendMessage(chatMessage, sender);

            // Send message to chat room participants
            messagingTemplate.convertAndSend(
                "/topic/chatroom/" + chatMessage.getChatRoomId(), 
                savedMessage
            );

        } catch (Exception e) {
            // Handle error - could send error message back to sender
            messagingTemplate.convertAndSendToUser(
                principal.getName(),
                "/queue/errors",
                "Failed to send message: " + e.getMessage()
            );
        }
    }

    @MessageMapping("/chat.addUser")
    public void addUser(@Payload String chatRoomId,
                       SimpMessageHeaderAccessor headerAccessor,
                       Principal principal) {
        // Add user to chat room session
        headerAccessor.getSessionAttributes().put("chatRoomId", chatRoomId);
        headerAccessor.getSessionAttributes().put("username", principal.getName());

        // Notify chat room about new user
        messagingTemplate.convertAndSend(
            "/topic/chatroom/" + chatRoomId + "/users",
            principal.getName() + " joined the chat"
        );
    }

    @MessageMapping("/chat.typing")
    public void typing(@Payload TypingRequest typingRequest, Principal principal) {
        try {
            // forward typing notification to room subscribers
            messagingTemplate.convertAndSend(
                "/topic/chatroom/" + typingRequest.getChatRoomId() + "/typing",
                typingRequest
            );
        } catch (Exception e) {
            // ignore
        }
    }

    @MessageMapping("/chat.stopTyping")
    public void stopTyping(@Payload TypingRequest typingRequest, Principal principal) {
        try {
            messagingTemplate.convertAndSend(
                "/topic/chatroom/" + typingRequest.getChatRoomId() + "/stop-typing",
                typingRequest
            );
        } catch (Exception e) {
            // ignore
        }
    }
}
