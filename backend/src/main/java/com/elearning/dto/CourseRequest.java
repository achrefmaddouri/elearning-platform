package com.elearning.dto;

import com.elearning.model.CourseVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public class CourseRequest {
    @NotBlank
    @Size(max = 100)
    private String title;

    private String description;

    private CourseVisibility visibility = CourseVisibility.PUBLIC;

    private BigDecimal price = BigDecimal.ZERO;

    private boolean isFree = true;

    // Constructors
    public CourseRequest() {}

    public CourseRequest(String title, String description) {
        this.title = title;
        this.description = description;
    }

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public CourseVisibility getVisibility() { return visibility; }
    public void setVisibility(CourseVisibility visibility) { this.visibility = visibility; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public boolean isFree() { return isFree; }
    public void setFree(boolean free) { isFree = free; }
}
