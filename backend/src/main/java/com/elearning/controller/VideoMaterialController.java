package com.elearning.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.elearning.controller.AuthController.MessageResponse;
import com.elearning.model.User;
import com.elearning.model.VideoMaterial;
import com.elearning.service.VideoMaterialService;

@RestController
@RequestMapping("/api/videos")
@CrossOrigin(origins = "*", maxAge = 3600)
public class VideoMaterialController {

    @Autowired
    private VideoMaterialService videoMaterialService;

    @PostMapping("/upload/{courseId}")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<?> uploadVideo(
            @PathVariable Long courseId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            Authentication authentication) {
        System.out.println("VideoMaterialController.uploadVideo called with courseId: " + courseId);
        System.out.println("File: " + file.getOriginalFilename() + ", Size: " + file.getSize());
        try {
            User user = (User) authentication.getPrincipal();
            VideoMaterial video = videoMaterialService.uploadVideo(file, title, description, courseId, user);
            return ResponseEntity.ok(video);
        } catch (RuntimeException e) {
            System.out.println("Error in uploadVideo: " + e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<VideoMaterial>> getCourseVideos(@PathVariable Long courseId) {
        List<VideoMaterial> videos = videoMaterialService.getCourseVideos(courseId);
        return ResponseEntity.ok(videos);
    }

    @GetMapping("/{videoId}")
    public ResponseEntity<VideoMaterial> getVideoInfo(@PathVariable Long videoId) {
        try {
            VideoMaterial video = videoMaterialService.getVideoById(videoId);
            return ResponseEntity.ok(video);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{videoId}/stream")
    public ResponseEntity<ByteArrayResource> streamVideo(@PathVariable Long videoId) {
        try {
            VideoMaterial video = videoMaterialService.getVideoById(videoId);
            byte[] videoContent = videoMaterialService.getVideoContent(videoId);
            
            ByteArrayResource resource = new ByteArrayResource(videoContent);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + video.getFileName() + "\"")
                    .contentType(MediaType.parseMediaType(video.getMimeType()))
                    .contentLength(videoContent.length)
                    .body(resource);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{videoId}")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<?> updateVideoInfo(
            @PathVariable Long videoId,
            @RequestBody VideoUpdateRequest request,
            Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            VideoMaterial video = videoMaterialService.updateVideoInfo(
                    videoId, 
                    request.getTitle(), 
                    request.getDescription(), 
                    request.getOrderIndex(), 
                    user
            );
            return ResponseEntity.ok(video);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{videoId}")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteVideo(@PathVariable Long videoId, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            videoMaterialService.deleteVideo(videoId, user);
            return ResponseEntity.ok(new MessageResponse("Video deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @PostMapping("/reorder/{courseId}")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<?> reorderVideos(
            @PathVariable Long courseId,
            @RequestBody List<Long> videoIds,
            Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            List<VideoMaterial> videos = videoMaterialService.reorderVideos(courseId, videoIds, user);
            return ResponseEntity.ok(videos);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    // Inner classes for request DTOs
    public static class VideoUpdateRequest {
        private String title;
        private String description;
        private Integer orderIndex;

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public Integer getOrderIndex() { return orderIndex; }
        public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }
    }
}
