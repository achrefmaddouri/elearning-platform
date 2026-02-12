package com.elearning.service;

import com.elearning.model.*;
import com.elearning.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class AnalyticsService {

    @Autowired
    private LearningSessionRepository learningSessionRepository;
    
    @Autowired
    private UserRecommendationRepository userRecommendationRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private ProgressRepository progressRepository;
    
    @Autowired
    private QuizAttemptRepository quizAttemptRepository;

    // Learning Session Management
    public LearningSession startLearningSession(User user, Course course, LearningSession.ActivityType activityType) {
        // End any existing active sessions for this user
        List<LearningSession> activeSessions = learningSessionRepository.findActiveSessions(user);
        activeSessions.forEach(LearningSession::endSession);
        learningSessionRepository.saveAll(activeSessions);
        
        // Create new session
        LearningSession session = new LearningSession(user, course, activityType);
        return learningSessionRepository.save(session);
    }

    public void endLearningSession(Long sessionId, Double engagementScore) {
        LearningSession session = learningSessionRepository.findById(sessionId).orElse(null);
        if (session != null && session.getSessionEnd() == null) {
            session.endSession();
            if (engagementScore != null) {
                session.setEngagementScore(Math.min(1.0, Math.max(0.0, engagementScore)));
            }
            learningSessionRepository.save(session);
        }
    }

    // Learning Analytics
    public Map<String, Object> getUserLearningAnalytics(User user) {
        Map<String, Object> analytics = new HashMap<>();
        
        // Basic stats
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minus(30, ChronoUnit.DAYS);
        List<LearningSession> recentSessions = learningSessionRepository
                .findByUserAndSessionStartBetween(user, thirtyDaysAgo, LocalDateTime.now());
        
        int totalSessions = recentSessions.size();
        int totalMinutes = recentSessions.stream()
                .filter(s -> s.getDurationMinutes() != null)
                .mapToInt(LearningSession::getDurationMinutes)
                .sum();
        
        Double avgEngagement = recentSessions.stream()
                .filter(s -> s.getEngagementScore() != null)
                .mapToDouble(LearningSession::getEngagementScore)
                .average()
                .orElse(0.0);
        
        analytics.put("totalSessions", totalSessions);
        analytics.put("totalLearningTimeMinutes", totalMinutes);
        analytics.put("averageEngagementScore", Math.round(avgEngagement * 100.0) / 100.0);
        analytics.put("averageSessionDuration", totalSessions > 0 ? totalMinutes / totalSessions : 0);
        
        // Learning patterns
        analytics.put("learningPatterns", analyzeLearningPatterns(user));
        analytics.put("activityBreakdown", getActivityBreakdown(user));
        analytics.put("peakLearningHours", getPeakLearningHours(user));
        
        return analytics;
    }

    private Map<String, Object> analyzeLearningPatterns(User user) {
        List<Object[]> hourlyPatterns = learningSessionRepository.findLearningPatternsByHour(user);
        Map<String, Object> patterns = new HashMap<>();
        
        if (!hourlyPatterns.isEmpty()) {
            Object[] bestHour = hourlyPatterns.get(0);
            patterns.put("peakHour", bestHour[0]);
            patterns.put("peakHourSessions", bestHour[1]);
            patterns.put("peakHourEngagement", bestHour[2]);
        }
        
        // Calculate learning velocity (sessions per day over last 30 days)
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minus(30, ChronoUnit.DAYS);
        List<LearningSession> recentSessions = learningSessionRepository
                .findByUserAndSessionStartBetween(user, thirtyDaysAgo, LocalDateTime.now());
        
        double velocity = recentSessions.size() / 30.0;
        patterns.put("learningVelocity", Math.round(velocity * 100.0) / 100.0);
        
        // Calculate consistency score (how regularly user learns)
        Set<LocalDate> activeDays = recentSessions.stream()
                .map(s -> s.getSessionStart().toLocalDate())
                .collect(Collectors.toSet());
        
        double consistencyScore = activeDays.size() / 30.0;
        patterns.put("consistencyScore", Math.round(consistencyScore * 100.0) / 100.0);
        
        return patterns;
    }

    private Map<String, Integer> getActivityBreakdown(User user) {
        List<Object[]> activities = learningSessionRepository.findActivityPatterns(user);
        return activities.stream()
                .collect(Collectors.toMap(
                        arr -> arr[0].toString(),
                        arr -> ((Number) arr[1]).intValue()
                ));
    }

    private List<Map<String, Object>> getPeakLearningHours(User user) {
        List<Object[]> hourlyData = learningSessionRepository.findLearningPatternsByHour(user);
        return hourlyData.stream()
                .limit(5) // Top 5 hours
                .map(arr -> {
                    Map<String, Object> hour = new HashMap<>();
                    hour.put("hour", arr[0]);
                    hour.put("sessions", arr[1]);
                    hour.put("avgEngagement", arr[2]);
                    return hour;
                })
                .collect(Collectors.toList());
    }

    // Course Analytics
    public Map<String, Object> getCourseAnalytics(Long courseId) {
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null) return new HashMap<>();
        
        Map<String, Object> analytics = new HashMap<>();
        
        // Basic metrics
        Long enrollmentCount = progressRepository.countByCourse(course);
        Long completionCount = progressRepository.countByCourseAndIsCompletedTrue(course);
        Double completionRate = enrollmentCount > 0 ? 
                (completionCount.doubleValue() / enrollmentCount.doubleValue()) * 100 : 0.0;
        
        analytics.put("totalEnrollments", enrollmentCount);
        analytics.put("completions", completionCount);
        analytics.put("completionRate", Math.round(completionRate * 100.0) / 100.0);
        
        // Time analytics
        List<Object[]> dailyActivity = learningSessionRepository.getDailyActiveUsersByCourse(courseId);
        analytics.put("dailyActiveUsers", dailyActivity.stream()
                .limit(30) // Last 30 days
                .collect(Collectors.toList()));
        
        // Engagement analytics
        analytics.put("engagementMetrics", getCourseEngagementMetrics(courseId));
        analytics.put("difficultyMetrics", getCourseDifficultyMetrics(courseId));
        
        return analytics;
    }

    private Map<String, Object> getCourseEngagementMetrics(Long courseId) {
        Map<String, Object> engagement = new HashMap<>();
        
        // Average session duration for this course
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minus(30, ChronoUnit.DAYS);
        List<LearningSession> courseSessions = learningSessionRepository
                .findByUserAndSessionStartBetween(null, thirtyDaysAgo, LocalDateTime.now())
                .stream()
                .filter(s -> s.getCourse() != null && s.getCourse().getId().equals(courseId))
                .collect(Collectors.toList());
        
        OptionalDouble avgDuration = courseSessions.stream()
                .filter(s -> s.getDurationMinutes() != null)
                .mapToInt(LearningSession::getDurationMinutes)
                .average();
        
        OptionalDouble avgEngagement = courseSessions.stream()
                .filter(s -> s.getEngagementScore() != null)
                .mapToDouble(LearningSession::getEngagementScore)
                .average();
        
        engagement.put("averageSessionDuration", avgDuration.orElse(0.0));
        engagement.put("averageEngagementScore", avgEngagement.orElse(0.0));
        engagement.put("totalSessions", courseSessions.size());
        
        return engagement;
    }

    private Map<String, Object> getCourseDifficultyMetrics(Long courseId) {
        Map<String, Object> difficulty = new HashMap<>();
        
        // Get quiz performance for this course (assuming quizzes are linked to courses)
        Set<Quiz> courseQuizzes = courseRepository.findById(courseId)
                .map(Course::getQuizzes)
                .orElse(new HashSet<>());
        
        if (!courseQuizzes.isEmpty()) {
            Double avgQuizScore = courseQuizzes.stream()
                    .flatMap(quiz -> quizAttemptRepository.findByQuiz(quiz).stream())
                    .mapToDouble(attempt -> (attempt.getScore() / (double) attempt.getTotalQuestions()) * 100)
                    .average()
                    .orElse(0.0);
            
            Long totalAttempts = courseQuizzes.stream()
                    .mapToLong(quiz -> quizAttemptRepository.findByQuiz(quiz).size())
                    .sum();
            
            difficulty.put("averageQuizScore", Math.round(avgQuizScore * 100.0) / 100.0);
            difficulty.put("totalQuizAttempts", totalAttempts);
            
            // Calculate difficulty rating (inverse of average score)
            Double difficultyRating = avgQuizScore > 0 ? (100 - avgQuizScore) / 100 * 5 : 2.5; // Scale 1-5
            difficulty.put("difficultyRating", Math.round(difficultyRating * 100.0) / 100.0);
        }
        
        return difficulty;
    }

    // Instructor Analytics
    public Map<String, Object> getInstructorAnalytics(User instructor) {
        if (instructor.getRole() != Role.INSTRUCTOR) {
            throw new IllegalArgumentException("User is not an instructor");
        }
        
        Map<String, Object> analytics = new HashMap<>();
        
        // Get instructor's courses
        List<Course> instructorCourses = courseRepository.findByInstructor(instructor);
        analytics.put("totalCourses", instructorCourses.size());
        
        // Student metrics
        Long totalStudents = instructorCourses.stream()
                .mapToLong(course -> progressRepository.countByCourse(course))
                .sum();
        analytics.put("totalStudents", totalStudents);
        
        // Completion rates
        Double avgCompletionRate = instructorCourses.stream()
                .mapToDouble(course -> {
                    Long enrollments = progressRepository.countByCourse(course);
                    Long completions = progressRepository.countByCourseAndIsCompletedTrue(course);
                    return enrollments > 0 ? (completions.doubleValue() / enrollments.doubleValue()) * 100 : 0.0;
                })
                .average()
                .orElse(0.0);
        analytics.put("averageCompletionRate", Math.round(avgCompletionRate * 100.0) / 100.0);
        
        // Course performance breakdown
        List<Map<String, Object>> courseBreakdown = instructorCourses.stream()
                .map(course -> {
                    Map<String, Object> courseData = new HashMap<>();
                    courseData.put("courseId", course.getId());
                    courseData.put("title", course.getTitle());
                    courseData.put("enrollments", progressRepository.countByCourse(course));
                    courseData.put("completions", progressRepository.countByCourseAndIsCompletedTrue(course));
                    return courseData;
                })
                .collect(Collectors.toList());
        
        analytics.put("courseBreakdown", courseBreakdown);
        
        return analytics;
    }

    // Recommendation System
    public List<UserRecommendation> generateRecommendations(User user) {
        List<UserRecommendation> recommendations = new ArrayList<>();
        
        // Analyze user's learning patterns
        Map<String, Object> patterns = analyzeLearningPatterns(user);
        List<Course> completedCourses = progressRepository.findByUserAndIsCompletedTrue(user)
                .stream()
                .map(Progress::getCourse)
                .collect(Collectors.toList());
        
        // Course recommendations based on completed courses
        if (!completedCourses.isEmpty()) {
            Course lastCompleted = completedCourses.get(completedCourses.size() - 1);
            // Find similar courses by instructor or by category if available
            List<Course> similarCourses = courseRepository.findByInstructor(lastCompleted.getInstructor())
                    .stream()
                    .filter(c -> !completedCourses.contains(c))
                    .limit(3)
                    .collect(Collectors.toList());
            
            for (Course course : similarCourses) {
                UserRecommendation rec = new UserRecommendation(
                        user,
                        UserRecommendation.RecommendationType.COURSE,
                        course.getTitle(),
                        "Based on your completion of " + lastCompleted.getTitle()
                );
                rec.setTargetId(course.getId());
                rec.setReasoning("You showed strong engagement with " + lastCompleted.getTitle() + 
                               ". This course covers similar topics at the next level.");
                rec.setConfidenceScore(0.85);
                rec.setPriorityScore(0.8);
                recommendations.add(rec);
            }
        }
        
        // Study time optimization recommendations
        Double consistencyScore = (Double) patterns.get("consistencyScore");
        if (consistencyScore != null && consistencyScore < 0.7) {
            UserRecommendation rec = new UserRecommendation(
                    user,
                    UserRecommendation.RecommendationType.STUDY_TIME,
                    "Improve Learning Consistency",
                    "Try to study a little bit every day for better retention."
            );
            rec.setReasoning("Your consistency score is " + Math.round(consistencyScore * 100) + 
                           "%. Regular daily practice improves learning outcomes by 40%.");
            rec.setConfidenceScore(0.9);
            rec.setPriorityScore(0.9);
            recommendations.add(rec);
        }
        
        // Save recommendations
        return userRecommendationRepository.saveAll(recommendations);
    }

    public List<UserRecommendation> getUserRecommendations(User user) {
        return userRecommendationRepository.findActiveRecommendations(user, LocalDateTime.now());
    }

    public void markRecommendationAsViewed(Long recommendationId) {
        userRecommendationRepository.findById(recommendationId)
                .ifPresent(UserRecommendation::markAsViewed);
    }

    public void markRecommendationAsActedUpon(Long recommendationId) {
        userRecommendationRepository.findById(recommendationId)
                .ifPresent(UserRecommendation::markAsActedUpon);
    }
    
    // Create sample learning data for testing
    public void createSampleLearningData(User user) {
        LocalDateTime now = LocalDateTime.now();
        List<LearningSession> sessions = new ArrayList<>();
        
        // Get user's courses
        List<Course> userCourses = courseRepository.findByInstructor(user);
        if (userCourses.isEmpty()) {
            // If user is not an instructor, get courses they're enrolled in
            List<Progress> progressList = progressRepository.findByUser(user);
            userCourses = progressList.stream()
                    .map(Progress::getCourse)
                    .collect(Collectors.toList());
        }
        
        if (userCourses.isEmpty()) {
            // Create some mock data if no courses found
            return;
        }
        
        // Create sample sessions over the last 30 days
        for (int day = 0; day < 30; day++) {
            LocalDateTime sessionDate = now.minus(day, ChronoUnit.DAYS);
            
            // Random number of sessions per day (0-3)
            int sessionsPerDay = (int) (Math.random() * 4);
            
            for (int session = 0; session < sessionsPerDay; session++) {
                Course randomCourse = userCourses.get((int) (Math.random() * userCourses.size()));
                
                LearningSession learningSession = new LearningSession();
                learningSession.setUser(user);
                learningSession.setCourse(randomCourse);
                
                // Random activity type
                LearningSession.ActivityType[] activities = LearningSession.ActivityType.values();
                learningSession.setActivityType(activities[(int) (Math.random() * activities.length)]);
                
                // Random time during the day
                LocalDateTime startTime = sessionDate.plusHours((int) (Math.random() * 16) + 8); // 8 AM to midnight
                learningSession.setSessionStart(startTime);
                
                // Random duration (15-120 minutes)
                int duration = 15 + (int) (Math.random() * 105);
                learningSession.setDurationMinutes(duration);
                learningSession.setSessionEnd(startTime.plusMinutes(duration));
                
                // Random engagement score (0.3-1.0)
                learningSession.setEngagementScore(0.3 + Math.random() * 0.7);
                
                sessions.add(learningSession);
            }
        }
        
        learningSessionRepository.saveAll(sessions);
    }
}
