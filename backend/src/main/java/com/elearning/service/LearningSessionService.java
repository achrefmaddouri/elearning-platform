package com.elearning.service;

import com.elearning.model.*;
import com.elearning.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LearningSessionService {

    @Autowired
    private LearningSessionRepository learningSessionRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CourseRepository courseRepository;

    public LearningSession startSession(User user, Course course, LearningSession.ActivityType activityType) {
        // End any existing active sessions for this user
        endActiveSessions(user);
        
        // Create new session
        LearningSession session = new LearningSession();
        session.setUser(user);
        session.setCourse(course);
        session.setActivityType(activityType);
        session.setSessionStart(LocalDateTime.now());
        session.setEngagementScore(0.5); // Default engagement
        
        return learningSessionRepository.save(session);
    }

    public void endSession(Long sessionId, Double engagementScore) {
        Optional<LearningSession> sessionOpt = learningSessionRepository.findById(sessionId);
        if (sessionOpt.isPresent()) {
            LearningSession session = sessionOpt.get();
            if (session.getSessionEnd() == null) {
                session.endSession();
                if (engagementScore != null) {
                    session.setEngagementScore(Math.min(1.0, Math.max(0.0, engagementScore)));
                }
                learningSessionRepository.save(session);
            }
        }
    }

    public void endActiveSessions(User user) {
        List<LearningSession> activeSessions = learningSessionRepository.findActiveSessions(user);
        for (LearningSession session : activeSessions) {
            session.endSession();
        }
        if (!activeSessions.isEmpty()) {
            learningSessionRepository.saveAll(activeSessions);
        }
    }

    // Auto-track when user starts course
    public LearningSession trackCourseView(User user, Course course) {
        return startSession(user, course, LearningSession.ActivityType.COURSE_VIEW);
    }

    // Auto-track when user takes quiz
    public LearningSession trackQuizAttempt(User user, Course course) {
        return startSession(user, course, LearningSession.ActivityType.QUIZ_ATTEMPT);
    }

    // Auto-track when user watches video
    public LearningSession trackVideoWatch(User user, Course course) {
        return startSession(user, course, LearningSession.ActivityType.VIDEO_WATCH);
    }

    // Auto-track when user downloads material
    public LearningSession trackMaterialDownload(User user, Course course) {
        return startSession(user, course, LearningSession.ActivityType.MATERIAL_DOWNLOAD);
    }

    // Create sample data for existing user (for testing)
    @Transactional
    public void createSampleDataForUser(User user) {
        List<Course> availableCourses = courseRepository.findAll();
        
        if (availableCourses.isEmpty()) {
            return;
        }

        // Get first few courses for sample data
        int coursesToUse = Math.min(3, availableCourses.size());
        
        // Create sessions for the last 30 days
        LocalDateTime now = LocalDateTime.now();
        
        for (int day = 0; day < 30; day++) {
            LocalDateTime sessionDate = now.minus(day, ChronoUnit.DAYS);
            
            // Skip some days for realistic pattern
            if (day % 3 == 0) continue;
            
            for (int courseIndex = 0; courseIndex < coursesToUse; courseIndex++) {
                Course course = availableCourses.get(courseIndex);
                
                // Create course view session
                LearningSession courseSession = new LearningSession();
                courseSession.setUser(user);
                courseSession.setCourse(course);
                courseSession.setActivityType(LearningSession.ActivityType.COURSE_VIEW);
                courseSession.setSessionStart(sessionDate.withHour(9 + (courseIndex * 2)).withMinute(0));
                courseSession.setSessionEnd(sessionDate.withHour(9 + (courseIndex * 2)).withMinute(45));
                courseSession.setDurationMinutes(45);
                courseSession.setEngagementScore(0.7 + (Math.random() * 0.3)); // 0.7-1.0
                courseSession.setDeviceType("Desktop");
                courseSession.setBrowser("Chrome");
                learningSessionRepository.save(courseSession);
                
                // Sometimes add quiz attempt
                if (day % 7 == 0) {
                    LearningSession quizSession = new LearningSession();
                    quizSession.setUser(user);
                    quizSession.setCourse(course);
                    quizSession.setActivityType(LearningSession.ActivityType.QUIZ_ATTEMPT);
                    quizSession.setSessionStart(sessionDate.withHour(10 + (courseIndex * 2)).withMinute(0));
                    quizSession.setSessionEnd(sessionDate.withHour(10 + (courseIndex * 2)).withMinute(20));
                    quizSession.setDurationMinutes(20);
                    quizSession.setEngagementScore(0.8 + (Math.random() * 0.2)); // 0.8-1.0
                    quizSession.setDeviceType("Desktop");
                    quizSession.setBrowser("Chrome");
                    learningSessionRepository.save(quizSession);
                }
                
                // Sometimes add video watch
                if (day % 5 == 0) {
                    LearningSession videoSession = new LearningSession();
                    videoSession.setUser(user);
                    videoSession.setCourse(course);
                    videoSession.setActivityType(LearningSession.ActivityType.VIDEO_WATCH);
                    videoSession.setSessionStart(sessionDate.withHour(14).withMinute(0));
                    videoSession.setSessionEnd(sessionDate.withHour(14).withMinute(30));
                    videoSession.setDurationMinutes(30);
                    videoSession.setEngagementScore(0.6 + (Math.random() * 0.4)); // 0.6-1.0
                    videoSession.setDeviceType("Mobile");
                    videoSession.setBrowser("Safari");
                    learningSessionRepository.save(videoSession);
                }
            }
        }
        
        System.out.println("Created sample learning session data for user: " + user.getEmail());
    }

    // Get current active session for user
    public Optional<LearningSession> getCurrentActiveSession(User user) {
        List<LearningSession> activeSessions = learningSessionRepository.findActiveSessions(user);
        return activeSessions.isEmpty() ? Optional.empty() : Optional.of(activeSessions.get(0));
    }
}
