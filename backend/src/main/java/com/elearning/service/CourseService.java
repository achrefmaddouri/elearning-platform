package com.elearning.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.elearning.dto.CourseDTO;
import com.elearning.dto.CourseRequest;
import com.elearning.model.ChatRoom;
import com.elearning.model.Course;
import com.elearning.model.CourseFile;
import com.elearning.model.CourseStatus;
import com.elearning.model.CourseVisibility;
import com.elearning.model.Enrollment;
import com.elearning.model.FileType;
import com.elearning.model.Progress;
import com.elearning.model.Role;
import com.elearning.model.User;
import com.elearning.repository.ChatRoomRepository;
import com.elearning.repository.CourseFileRepository;
import com.elearning.repository.CourseRepository;
import com.elearning.repository.EnrollmentRepository;
import com.elearning.repository.ProgressRepository;

@Service
@Transactional
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseFileRepository courseFileRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private ProgressRepository progressRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private EmailService emailService;

    private final String UPLOAD_DIR = "uploads/";

    public Course createCourse(CourseRequest request, User instructor) {
        Course course = new Course(request.getTitle(), request.getDescription(), instructor);
        course.setVisibility(request.getVisibility());
        course.setPrice(request.getPrice());
        course.setFree(request.isFree());
        
        if (request.getVisibility() == CourseVisibility.PRIVATE) {
            course.setPrivateUrl(generatePrivateUrl());
        }

        Course savedCourse = courseRepository.save(course);
        
        // Fetch the course with instructor to ensure it's properly loaded
        return courseRepository.findByIdWithInstructor(savedCourse.getId())
                .orElseThrow(() -> new RuntimeException("Error creating course"));
    }

    public Course updateCourse(Long courseId, CourseRequest request, User instructor) {
        Course course = courseRepository.findByIdWithInstructor(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!course.getInstructor().getId().equals(instructor.getId())) {
            throw new RuntimeException("Access denied");
        }

        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setVisibility(request.getVisibility());
        course.setPrice(request.getPrice());
        course.setFree(request.isFree());

        if (request.getVisibility() == CourseVisibility.PRIVATE && course.getPrivateUrl() == null) {
            course.setPrivateUrl(generatePrivateUrl());
        }

        return courseRepository.save(course);
    }

    public void deleteCourse(Long courseId, User instructor) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!course.getInstructor().getId().equals(instructor.getId())) {
            throw new RuntimeException("Access denied");
        }

        courseRepository.delete(course);
    }

    public List<Course> getInstructorCourses(User instructor) {
        return courseRepository.findByInstructorWithInstructor(instructor);
    }

    public List<CourseDTO> getPublicCourses() {
        List<Course> courses = courseRepository.findPublicCoursesWithInstructor();
        return courses.stream()
                .map(CourseDTO::new)
                .collect(Collectors.toList());
    }

    public Course getCourseByPrivateUrl(String privateUrl) {
        return courseRepository.findByPrivateUrl(privateUrl)
                .orElseThrow(() -> new RuntimeException("Course not found"));
    }

    public Course getCourseById(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
    }

    public void enrollUserInCourse(User user, Course course) {
        if (enrollmentRepository.existsByUserAndCourse(user, course)) {
            throw new RuntimeException("User already enrolled in this course");
        }

        // Create enrollment
        Enrollment enrollment = new Enrollment(user, course);
        enrollmentRepository.save(enrollment);

        // Create progress tracking
        Progress progress = new Progress(user, course);
        progressRepository.save(progress);

        // Create chat room with instructor
        ChatRoom chatRoom = new ChatRoom(course, user, course.getInstructor());
        chatRoomRepository.save(chatRoom);
    }

    public String uploadFile(Long courseId, MultipartFile file, User instructor) throws IOException {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!course.getInstructor().getId().equals(instructor.getId())) {
            throw new RuntimeException("Access denied");
        }

        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uniqueFilename = UUID.randomUUID().toString() + extension;
        
        // Save file
        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Determine file type
        FileType fileType = determineFileType(extension);

        // Save file info to database
        CourseFile courseFile = new CourseFile(
                originalFilename,
                filePath.toString(),
                fileType,
                file.getSize(),
                course
        );
        courseFileRepository.save(courseFile);

        return filePath.toString();
    }

    public List<CourseFile> getCourseFiles(Long courseId, User user) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // Check if user is enrolled or is the instructor or is an admin
        boolean isEnrolled = enrollmentRepository.existsByUserAndCourse(user, course);
        boolean isInstructor = course.getInstructor().getId().equals(user.getId());
        boolean isAdmin = user.getRole() == Role.ADMIN;

        if (!isEnrolled && !isInstructor && !isAdmin) {
            throw new RuntimeException("Access denied");
        }

        return courseFileRepository.findByCourse(course);
    }

    // Admin methods
    public void approveCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        
        course.setStatus(CourseStatus.ACCEPTED);
        courseRepository.save(course);

        // Send email notification
        emailService.sendCourseStatusEmail(
                course.getInstructor().getEmail(),
                course.getInstructor().getFirstName(),
                course.getTitle(),
                "accepted"
        );
    }

    public void declineCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        
        course.setStatus(CourseStatus.DECLINED);
        courseRepository.save(course);

        // Send email notification
        emailService.sendCourseStatusEmail(
                course.getInstructor().getEmail(),
                course.getInstructor().getFirstName(),
                course.getTitle(),
                "declined"
        );
    }

    public List<Course> getPendingCourses() {
        return courseRepository.findByStatus(CourseStatus.PENDING);
    }

    public String uploadCourseThumbnail(Long courseId, MultipartFile thumbnail, User instructor) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!course.getInstructor().getId().equals(instructor.getId())) {
            throw new RuntimeException("Access denied");
        }

        try {
            // Create thumbnails directory if it doesn't exist
            Path thumbnailPath = Paths.get("uploads/thumbnails/");
            if (!Files.exists(thumbnailPath)) {
                Files.createDirectories(thumbnailPath);
            }

            // Generate unique filename
            String originalFilename = thumbnail.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String uniqueFilename = "course_" + courseId + "_" + UUID.randomUUID().toString() + extension;

            // Save file
            Path filePath = thumbnailPath.resolve(uniqueFilename);
            Files.copy(thumbnail.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Update course with thumbnail URL
            String thumbnailUrl = "/uploads/thumbnails/" + uniqueFilename;
            course.setThumbnailUrl(thumbnailUrl);
            courseRepository.save(course);

            return thumbnailUrl;

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload thumbnail: " + e.getMessage());
        }
    }

    private String generatePrivateUrl() {
        return UUID.randomUUID().toString();
    }

    private FileType determineFileType(String extension) {
        extension = extension.toLowerCase();
        switch (extension) {
            case ".mp4":
            case ".avi":
            case ".mov":
            case ".wmv":
                return FileType.VIDEO;
            case ".jpg":
            case ".jpeg":
            case ".png":
            case ".gif":
                return FileType.IMAGE;
            case ".pdf":
                return FileType.PDF;
            default:
                return FileType.DOCUMENT;
        }
    }
}
