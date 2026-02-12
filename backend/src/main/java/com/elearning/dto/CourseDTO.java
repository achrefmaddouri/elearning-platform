package com.elearning.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.elearning.model.Course;
import com.elearning.model.CourseStatus;
import com.elearning.model.CourseVisibility;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

public class CourseDTO {
    private Long id;
    private String title;
    private String description;
    private Long instructorId;
    private String instructorName;
    private CourseStatus status;
    private CourseVisibility visibility;
    private BigDecimal price;
    @JsonProperty("isFree")
    private boolean isFree;
    private String privateUrl;
    private String thumbnailUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int enrollmentCount;
    private int filesCount;
    private int quizzesCount;

    // Constructors
    public CourseDTO() {}

    public CourseDTO(Course course) {
        this.id = course.getId();
        this.title = course.getTitle();
        this.description = course.getDescription();
        this.instructorId = course.getInstructor().getId();
        this.instructorName = course.getInstructor().getFirstName() + " " + course.getInstructor().getLastName();
        this.status = course.getStatus();
        this.visibility = course.getVisibility();
        this.price = course.getPrice();
        this.isFree = course.isFree();
        this.privateUrl = course.getPrivateUrl();
        this.thumbnailUrl = course.getThumbnailUrl();
        this.createdAt = course.getCreatedAt();
        this.updatedAt = course.getUpdatedAt();
        
        // Don't access lazy collections to avoid lazy loading issues
        this.enrollmentCount = 0;
        this.filesCount = 0;
        this.quizzesCount = 0;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getInstructorId() { return instructorId; }
    public void setInstructorId(Long instructorId) { this.instructorId = instructorId; }

    public String getInstructorName() { return instructorName; }
    public void setInstructorName(String instructorName) { this.instructorName = instructorName; }

    public CourseStatus getStatus() { return status; }
    public void setStatus(CourseStatus status) { this.status = status; }

    public CourseVisibility getVisibility() { return visibility; }
    public void setVisibility(CourseVisibility visibility) { this.visibility = visibility; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public boolean isFree() { return isFree; }
    @JsonProperty("isFree")
    public void setFree(boolean free) { isFree = free; }

    public String getPrivateUrl() { return privateUrl; }
    public void setPrivateUrl(String privateUrl) { this.privateUrl = privateUrl; }

    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public int getEnrollmentCount() { return enrollmentCount; }
    public void setEnrollmentCount(int enrollmentCount) { this.enrollmentCount = enrollmentCount; }

    public int getFilesCount() { return filesCount; }
    public void setFilesCount(int filesCount) { this.filesCount = filesCount; }

    public int getQuizzesCount() { return quizzesCount; }
    public void setQuizzesCount(int quizzesCount) { this.quizzesCount = quizzesCount; }
}
