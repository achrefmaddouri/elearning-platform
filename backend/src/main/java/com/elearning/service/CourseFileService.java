package com.elearning.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.elearning.model.Course;
import com.elearning.model.CourseFile;
import com.elearning.model.FileType;
import com.elearning.model.User;
import com.elearning.repository.CourseFileRepository;
import com.elearning.repository.CourseRepository;

@Service
public class CourseFileService {

    @Autowired
    private CourseFileRepository courseFileRepository;

    @Autowired
    private CourseRepository courseRepository;

    private final String uploadDir = "uploads/materials/";

    public CourseFile uploadCourseFile(Long courseId, MultipartFile file, String fileName, User uploader) {
        // Verify user can upload to this course
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!course.getInstructor().getId().equals(uploader.getId()) && 
            !uploader.getRole().name().equals("ADMIN")) {
            throw new RuntimeException("Unauthorized to upload files to this course");
        }

        try {
            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
            
            // Use provided fileName or original filename without extension
            String displayName = fileName != null && !fileName.trim().isEmpty() 
                ? fileName.trim() 
                : (originalFilename != null ? originalFilename.replaceFirst("[.][^.]+$", "") : "Untitled");

            // Determine file type
            FileType fileType = determineFileType(file.getContentType(), fileExtension);

            // Save file to disk
            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Create database entry
            CourseFile courseFile = new CourseFile(
                displayName,
                filePath.toString(),
                fileType,
                file.getSize(),
                course
            );

            return courseFileRepository.save(courseFile);

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file: " + e.getMessage());
        }
    }

    public List<CourseFile> getCourseFiles(Long courseId, User user) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // Check if user can access course files
        // For now, allow enrolled users and instructors/admins
        return courseFileRepository.findByCourse(course);
    }

    public byte[] getFileContent(Long fileId, User user) {
        CourseFile courseFile = courseFileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        try {
            Path filePath = Paths.get(courseFile.getFilePath());
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + e.getMessage());
        }
    }

    public CourseFile getCourseFileById(Long fileId, User user) {
        CourseFile courseFile = courseFileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));
        
        // Check if user can access this file (same logic as getCourseFiles)
        Course course = courseFile.getCourse();
        // For now, allow enrolled users and instructors/admins
        // You might want to add more specific permission checks here
        
        return courseFile;
    }

    public void deleteCourseFile(Long fileId, User user) {
        CourseFile courseFile = courseFileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        // Check permissions
        if (!courseFile.getCourse().getInstructor().getId().equals(user.getId()) && 
            !user.getRole().name().equals("ADMIN")) {
            throw new RuntimeException("Unauthorized to delete this file");
        }

        try {
            // Delete physical file
            Path filePath = Paths.get(courseFile.getFilePath());
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }

            // Delete database entry
            courseFileRepository.delete(courseFile);

        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file: " + e.getMessage());
        }
    }

    public CourseFile updateCourseFile(Long fileId, String newFileName, User user) {
        CourseFile courseFile = courseFileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        // Check permissions
        if (!courseFile.getCourse().getInstructor().getId().equals(user.getId()) && 
            !user.getRole().name().equals("ADMIN")) {
            throw new RuntimeException("Unauthorized to update this file");
        }

        courseFile.setFileName(newFileName);
        return courseFileRepository.save(courseFile);
    }

    private FileType determineFileType(String mimeType, String fileExtension) {
        if (mimeType != null) {
            if (mimeType.startsWith("video/")) {
                return FileType.VIDEO;
            } else if (mimeType.startsWith("image/")) {
                return FileType.IMAGE;
            } else if (mimeType.equals("application/pdf")) {
                return FileType.PDF;
            }
        }

        // Fallback to extension-based detection
        if (fileExtension != null) {
            String ext = fileExtension.toLowerCase();
            if (ext.matches("\\.(mp4|avi|mov|wmv|flv|webm|mkv)")) {
                return FileType.VIDEO;
            } else if (ext.matches("\\.(jpg|jpeg|png|gif|bmp|svg|webp)")) {
                return FileType.IMAGE;
            } else if (ext.equals(".pdf")) {
                return FileType.PDF;
            }
        }

        return FileType.DOCUMENT;
    }
}
