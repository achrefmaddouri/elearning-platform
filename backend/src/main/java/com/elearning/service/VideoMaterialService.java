package com.elearning.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.elearning.model.Course;
import com.elearning.model.User;
import com.elearning.model.VideoMaterial;
import com.elearning.repository.VideoMaterialRepository;

@Service
public class VideoMaterialService {

    @Autowired
    private VideoMaterialRepository videoMaterialRepository;

    @Autowired
    private CourseService courseService;

    @Value("${app.upload.video.path:uploads/videos/}")
    private String videoUploadPath;

    public VideoMaterial uploadVideo(MultipartFile file, String title, String description, 
                                   Long courseId, User user) {
        try {
            // Verify user has access to the course
            Course course = courseService.getCourseById(courseId);
            if (!course.getInstructor().getId().equals(user.getId()) && 
                !user.getRole().name().equals("ADMIN")) {
                throw new RuntimeException("You don't have permission to add videos to this course");
            }

            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("video/")) {
                throw new RuntimeException("File must be a video");
            }

            // Create upload directory if it doesn't exist
            Path uploadDir = Paths.get(videoUploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
            Path filePath = uploadDir.resolve(uniqueFilename);

            // Save file
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Get next order index
            long videoCount = videoMaterialRepository.countByCourseId(courseId);

            // Create VideoMaterial entity
            VideoMaterial videoMaterial = new VideoMaterial(
                title, 
                description, 
                originalFilename, 
                filePath.toString(),
                contentType,
                file.getSize(),
                course
            );
            videoMaterial.setOrderIndex((int) videoCount + 1);

            return videoMaterialRepository.save(videoMaterial);

        } catch (IOException e) {
            throw new RuntimeException("Failed to store video file: " + e.getMessage());
        }
    }

    public List<VideoMaterial> getCourseVideos(Long courseId) {
        return videoMaterialRepository.findByCourseIdOrderByOrderIndexAsc(courseId);
    }

    public VideoMaterial getVideoById(Long videoId) {
        return videoMaterialRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Video not found"));
    }

    public byte[] getVideoContent(Long videoId) {
        try {
            VideoMaterial video = getVideoById(videoId);
            Path filePath = Paths.get(video.getFilePath());
            
            if (!Files.exists(filePath)) {
                throw new RuntimeException("Video file not found on disk");
            }
            
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read video file: " + e.getMessage());
        }
    }

    public void deleteVideo(Long videoId, User user) {
        VideoMaterial video = getVideoById(videoId);
        Course course = video.getCourse();
        
        // Verify user has permission
        if (!course.getInstructor().getId().equals(user.getId()) && 
            !user.getRole().name().equals("ADMIN")) {
            throw new RuntimeException("You don't have permission to delete this video");
        }

        try {
            // Delete file from disk
            Path filePath = Paths.get(video.getFilePath());
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }

            // Delete from database
            videoMaterialRepository.delete(video);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete video file: " + e.getMessage());
        }
    }

    public VideoMaterial updateVideoInfo(Long videoId, String title, String description, 
                                       Integer orderIndex, User user) {
        VideoMaterial video = getVideoById(videoId);
        Course course = video.getCourse();
        
        // Verify user has permission
        if (!course.getInstructor().getId().equals(user.getId()) && 
            !user.getRole().name().equals("ADMIN")) {
            throw new RuntimeException("You don't have permission to update this video");
        }

        if (title != null) video.setTitle(title);
        if (description != null) video.setDescription(description);
        if (orderIndex != null) video.setOrderIndex(orderIndex);

        return videoMaterialRepository.save(video);
    }

    public List<VideoMaterial> reorderVideos(Long courseId, List<Long> videoIds, User user) {
        Course course = courseService.getCourseById(courseId);
        
        // Verify user has permission
        if (!course.getInstructor().getId().equals(user.getId()) && 
            !user.getRole().name().equals("ADMIN")) {
            throw new RuntimeException("You don't have permission to reorder videos in this course");
        }

        for (int i = 0; i < videoIds.size(); i++) {
            VideoMaterial video = getVideoById(videoIds.get(i));
            video.setOrderIndex(i + 1);
            videoMaterialRepository.save(video);
        }

        return getCourseVideos(courseId);
    }
}
