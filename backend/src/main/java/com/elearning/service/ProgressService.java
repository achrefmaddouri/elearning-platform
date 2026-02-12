package com.elearning.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.elearning.model.Course;
import com.elearning.model.Progress;
import com.elearning.model.Quiz;
import com.elearning.model.QuizAttempt;
import com.elearning.model.User;
import com.elearning.repository.CourseRepository;
import com.elearning.repository.EnrollmentRepository;
import com.elearning.repository.ProgressRepository;
import com.elearning.repository.QuizAttemptRepository;
import com.elearning.repository.QuizRepository;

@Service
@Transactional
public class ProgressService {

    @Autowired
    private ProgressRepository progressRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizAttemptRepository quizAttemptRepository;
    
    @Autowired
    private GamificationService gamificationService;

    public Progress updateProgress(Long courseId, int progressPercentage, User user) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // Check if user is enrolled
        if (!enrollmentRepository.existsByUserAndCourse(user, course)) {
            throw new RuntimeException("User not enrolled in this course");
        }

        Progress progress = progressRepository.findByUserAndCourse(user, course)
                .orElseThrow(() -> new RuntimeException("Progress record not found"));

        progress.setProgressPercentage(Math.min(100, Math.max(0, progressPercentage)));
        progress.setLastAccessed(LocalDateTime.now());

        // Mark as completed if 100%
        if (progressPercentage >= 100) {
            progress.setCompleted(true);
            // Generate certificate
            generateCertificate(progress);
        }

        return progressRepository.save(progress);
    }

    public Progress calculateAndUpdateProgress(Long courseId, User user) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // Check if user is enrolled
        if (!enrollmentRepository.existsByUserAndCourse(user, course)) {
            throw new RuntimeException("User not enrolled in this course");
        }

        Progress progress = progressRepository.findByUserAndCourse(user, course)
                .orElseThrow(() -> new RuntimeException("Progress record not found"));

        // Calculate progress based on completed quizzes
        List<Quiz> courseQuizzes = quizRepository.findByCourse(course);
        
        if (courseQuizzes.isEmpty()) {
            // No quizzes in course, progress remains manual
            progress.setLastAccessed(LocalDateTime.now());
            return progressRepository.save(progress);
        }

        // Count passed quizzes (score >= 75%)
        int passedQuizzes = 0;
        for (Quiz quiz : courseQuizzes) {
            List<QuizAttempt> attempts = quizAttemptRepository.findByQuizAndUserOrderByAttemptedAtDesc(quiz, user);
            if (!attempts.isEmpty()) {
                QuizAttempt bestAttempt = attempts.get(0); // Get most recent attempt
                double scorePercentage = (double) bestAttempt.getScore() / bestAttempt.getTotalQuestions() * 100;
                if (scorePercentage >= 75.0) {
                    passedQuizzes++;
                }
            }
        }

        // Calculate progress percentage
        int progressPercentage = (int) Math.round((double) passedQuizzes / courseQuizzes.size() * 100);
        progress.setProgressPercentage(progressPercentage);
        progress.setLastAccessed(LocalDateTime.now());

        // Mark as completed if 100%
        if (progressPercentage >= 100 && !progress.isCompleted()) {
            progress.setCompleted(true);
            
            // Award points for course completion
            gamificationService.handleCourseCompletion(user, courseId, course.getTitle());
            
            // Generate certificate
            generateCertificate(progress);
        }

        return progressRepository.save(progress);
    }

    public List<Progress> getUserProgress(User user) {
        return progressRepository.findByUser(user);
    }

    public List<Progress> getUserCompletedCourses(User user) {
        return progressRepository.findByUserAndIsCompleted(user, true);
    }

    public Progress getCourseProgress(Long courseId, User user) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        return progressRepository.findByUserAndCourse(user, course)
                .orElseThrow(() -> new RuntimeException("Progress record not found"));
    }

    private void generateCertificate(Progress progress) {
        // In a real implementation, you would generate a PDF certificate
        // For now, we'll just set a placeholder URL
        String certificateUrl = "certificates/" + progress.getUser().getId() + "_" + 
                                progress.getCourse().getId() + "_certificate.pdf";
        progress.setCertificateUrl(certificateUrl);
    }
}
