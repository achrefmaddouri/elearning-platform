package com.elearning.dto;

import jakarta.validation.constraints.NotNull;

public class CreateChatRoomRequest {
    @NotNull
    private Long courseId;

    public CreateChatRoomRequest() {}

    public CreateChatRoomRequest(Long courseId) {
        this.courseId = courseId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }
}
