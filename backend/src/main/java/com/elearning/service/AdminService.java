package com.elearning.service;

import com.elearning.model.*;
import com.elearning.repository.*;
import com.elearning.dto.AdminUserDTO;
import com.elearning.dto.AdminCourseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // User Management
    public List<AdminUserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(AdminUserDTO::new)
                .collect(Collectors.toList());
    }

    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }

    public List<User> getUsersByStatus(UserStatus status) {
        return userRepository.findByStatus(status);
    }

    public User updateUserStatus(Long userId, UserStatus status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setStatus(status);
        return userRepository.save(user);
    }

    public User updateUserRole(Long userId, Role role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setRole(role);
        return userRepository.save(user);
    }

    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getRole() == Role.ADMIN) {
            throw new RuntimeException("Cannot delete admin user");
        }
        
        userRepository.delete(user);
    }

    public User banUser(Long userId) {
        return updateUserStatus(userId, UserStatus.BANNED);
    }

    public User unbanUser(Long userId) {
        return updateUserStatus(userId, UserStatus.ACTIVE);
    }

    public User createUser(String firstName, String lastName, String email, String password, Role role) {
        // Check if user already exists
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("User with this email already exists");
        }

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password)); // Encode password
        user.setRole(role);
        user.setStatus(UserStatus.ACTIVE);
        user.setEmailVerified(true);

        return userRepository.save(user);
    }

    // Course Management
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public List<AdminCourseDTO> getPendingCourses() {
        List<Course> courses = courseRepository.findByStatusWithInstructor(CourseStatus.PENDING);
        return courses.stream()
                .map(AdminCourseDTO::new)
                .collect(Collectors.toList());
    }

    public List<Course> getCoursesByStatus(CourseStatus status) {
        return courseRepository.findByStatus(status);
    }

    public Course approveCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        
        course.setStatus(CourseStatus.ACCEPTED);
        Course savedCourse = courseRepository.save(course);

        // Send email notification
        emailService.sendCourseStatusEmail(
                course.getInstructor().getEmail(),
                course.getInstructor().getFirstName(),
                course.getTitle(),
                "accepted"
        );

        return savedCourse;
    }

    public Course declineCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        
        course.setStatus(CourseStatus.DECLINED);
        Course savedCourse = courseRepository.save(course);

        // Send email notification
        emailService.sendCourseStatusEmail(
                course.getInstructor().getEmail(),
                course.getInstructor().getFirstName(),
                course.getTitle(),
                "declined"
        );

        return savedCourse;
    }

    public Course archiveCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        
        course.setStatus(CourseStatus.ARCHIVED);
        return courseRepository.save(course);
    }

    public void deleteCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        
        courseRepository.delete(course);
    }

    // Statistics
    public Map<String, Object> getDashboardStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // User statistics
        stats.put("totalUsers", userRepository.count());
        stats.put("activeUsers", userRepository.countByStatus(UserStatus.ACTIVE));
        stats.put("bannedUsers", userRepository.countByStatus(UserStatus.BANNED));
        stats.put("totalInstructors", userRepository.countByRole(Role.INSTRUCTOR));
        stats.put("totalStudents", userRepository.countByRole(Role.USER));
        
        // Course statistics
        stats.put("totalCourses", courseRepository.count());
        stats.put("acceptedCourses", courseRepository.countByStatus(CourseStatus.ACCEPTED));
        stats.put("pendingCourses", courseRepository.countByStatus(CourseStatus.PENDING));
        stats.put("declinedCourses", courseRepository.countByStatus(CourseStatus.DECLINED));
        
        // Enrollment statistics
        stats.put("totalEnrollments", enrollmentRepository.count());
        
        return stats;
    }

    public Map<String, Long> getUserRegistrationStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("instructors", userRepository.countByRole(Role.INSTRUCTOR));
        stats.put("students", userRepository.countByRole(Role.USER));
        stats.put("activeUsers", userRepository.countByStatus(UserStatus.ACTIVE));
        stats.put("pendingUsers", userRepository.countByStatus(UserStatus.PENDING));
        return stats;
    }

    public Map<String, Long> getCourseStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalCourses", courseRepository.count());
        stats.put("acceptedCourses", courseRepository.countByStatus(CourseStatus.ACCEPTED));
        stats.put("pendingCourses", courseRepository.countByStatus(CourseStatus.PENDING));
        stats.put("declinedCourses", courseRepository.countByStatus(CourseStatus.DECLINED));
        stats.put("archivedCourses", courseRepository.countByStatus(CourseStatus.ARCHIVED));
        return stats;
    }
}
