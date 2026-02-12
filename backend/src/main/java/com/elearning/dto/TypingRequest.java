package com.elearning.dto;

public class TypingRequest {
    private Long chatRoomId;
    private Long userId;
    private String name;

    public TypingRequest() {}

    public TypingRequest(Long chatRoomId, Long userId, String name) {
        this.chatRoomId = chatRoomId;
        this.userId = userId;
        this.name = name;
    }

    public Long getChatRoomId() { return chatRoomId; }
    public void setChatRoomId(Long chatRoomId) { this.chatRoomId = chatRoomId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
