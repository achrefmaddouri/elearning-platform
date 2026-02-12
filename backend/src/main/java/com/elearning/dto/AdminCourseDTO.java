package com.elearning.dto;

import com.elearning.model.Course;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AdminCourseDTO {
    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private boolean isFree;
    private boolean isPrivate;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private AdminUserDTO instructor;

    // Constructors
    public AdminCourseDTO() {}

    public AdminCourseDTO(Course course) {
        this.id = course.getId();
        this.title = course.getTitle();
        this.description = course.getDescription();
        this.price = course.getPrice();
        this.isFree = course.isFree();
        this.isPrivate = course.getVisibility() == com.elearning.model.CourseVisibility.PRIVATE;
        this.status = course.getStatus().toString();
        this.createdAt = course.getCreatedAt();
        this.updatedAt = course.getUpdatedAt();
        
        // Safely create instructor DTO
        if (course.getInstructor() != null) {
            this.instructor = new AdminUserDTO(course.getInstructor());
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public boolean isFree() { return isFree; }
    public void setFree(boolean free) { isFree = free; }

    public boolean isPrivate() { return isPrivate; }
    public void setPrivate(boolean isPrivate) { this.isPrivate = isPrivate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public AdminUserDTO getInstructor() { return instructor; }
    public void setInstructor(AdminUserDTO instructor) { this.instructor = instructor; }
}
