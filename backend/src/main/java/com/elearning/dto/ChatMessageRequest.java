package com.elearning.dto;

import jakarta.validation.constraints.NotBlank;

public class ChatMessageRequest {
    private Long chatRoomId;

    @NotBlank
    private String message;

    // Constructors
    public ChatMessageRequest() {}

    public ChatMessageRequest(Long chatRoomId, String message) {
        this.chatRoomId = chatRoomId;
        this.message = message;
    }

    // Getters and Setters
    public Long getChatRoomId() { return chatRoomId; }
    public void setChatRoomId(Long chatRoomId) { this.chatRoomId = chatRoomId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
