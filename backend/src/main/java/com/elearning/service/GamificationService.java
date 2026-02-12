package com.elearning.service;

import com.elearning.model.*;
import com.elearning.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GamificationService {

    @Autowired
    private UserGamificationRepository userGamificationRepository;
    
    @Autowired
    private BadgeRepository badgeRepository;
    
    @Autowired
    private UserBadgeRepository userBadgeRepository;
    
    @Autowired
    private PointsTransactionRepository pointsTransactionRepository;
    
    @Autowired
    private LeaderboardRepository leaderboardRepository;
    
    @Autowired
    private EnrollmentRepository enrollmentRepository;
    
    @Autowired
    private ProgressRepository progressRepository;
    
    @Autowired
    private QuizAttemptRepository quizAttemptRepository;
    
    // Initialize user gamification profile
    public UserGamification initializeUserGamification(User user) {
        Optional<UserGamification> existing = userGamificationRepository.findByUser(user);
        if (existing.isPresent()) {
            return existing.get();
        }
        
        UserGamification gamification = new UserGamification(user);
        return userGamificationRepository.save(gamification);
    }
    
    // Award points for various actions
    public void awardPoints(User user, Integer basePoints, PointsTransaction.SourceType sourceType, 
                           Long sourceId, String description, BigDecimal multiplier) {
        // Calculate final points with multiplier
        Integer finalPoints = (int) Math.round(basePoints * multiplier.doubleValue());
        
        // Create transaction record
        PointsTransaction transaction = new PointsTransaction(
            user, finalPoints, PointsTransaction.TransactionType.EARNED, 
            sourceType, sourceId, description, multiplier
        );
        pointsTransactionRepository.save(transaction);
        
        // Update user's total points
        UserGamification gamification = getUserGamificationOrCreate(user);
        gamification.setTotalPoints(gamification.getTotalPoints() + finalPoints);
        userGamificationRepository.save(gamification);
        
        // Check for badge eligibility
        checkBadgeEligibility(user);
        
        // Update leaderboards
        updateLeaderboards(user);
    }
    
    // Overloaded method without multiplier
    public void awardPoints(User user, Integer points, PointsTransaction.SourceType sourceType, 
                           Long sourceId, String description) {
        awardPoints(user, points, sourceType, sourceId, description, BigDecimal.ONE);
    }
    
    // Handle quiz completion with score-based multiplier
    public void handleQuizCompletion(User user, Long quizId, double scorePercentage, boolean passed) {
        System.out.println("🎯 DEBUG: handleQuizCompletion called for user: " + user.getEmail() + ", score: " + scorePercentage + "%, passed: " + passed);
        
        if (!passed) {
            // Reset quiz streak if failed
            UserGamification gamification = getUserGamificationOrCreate(user);
            gamification.setCurrentQuizStreak(0);
            userGamificationRepository.save(gamification);
            System.out.println("❌ DEBUG: Quiz failed, reset quiz streak to 0");
            return;
        }
        
        // Calculate points with score multiplier
        Integer basePoints = 100; // Base points for passing a quiz
        BigDecimal multiplier = BigDecimal.ONE;
        
        if (scorePercentage >= 100.0) {
            multiplier = new BigDecimal("2.0"); // 2x for perfect score
        } else if (scorePercentage >= 90.0) {
            multiplier = new BigDecimal("1.5"); // 1.5x for excellent score
        } else if (scorePercentage >= 80.0) {
            multiplier = new BigDecimal("1.25"); // 1.25x for good score
        }
        
        String description = String.format("Quiz passed with %.1f%% score", scorePercentage);
        awardPoints(user, basePoints, PointsTransaction.SourceType.QUIZ_PASS, quizId, description, multiplier);
        
        // Update quiz streak
        UserGamification gamification = getUserGamificationOrCreate(user);
        gamification.setCurrentQuizStreak(gamification.getCurrentQuizStreak() + 1);
        userGamificationRepository.save(gamification);
        
        // Award streak bonus if applicable
        if (gamification.getCurrentQuizStreak() >= 5) {
            Integer bonusPoints = 50 * (gamification.getCurrentQuizStreak() / 5);
            awardPoints(user, bonusPoints, PointsTransaction.SourceType.QUIZ_PASS, quizId, 
                       "Quiz streak bonus (" + gamification.getCurrentQuizStreak() + " in a row)");
        }
    }
    
    // Handle course completion
    public void handleCourseCompletion(User user, Long courseId, String courseName) {
        Integer basePoints = 500; // Base points for completing a course
        String description = "Course completed: " + courseName;
        awardPoints(user, basePoints, PointsTransaction.SourceType.COURSE_COMPLETE, courseId, description);
        
        // Update course streak
        UserGamification gamification = getUserGamificationOrCreate(user);
        gamification.setCurrentCourseStreak(gamification.getCurrentCourseStreak() + 1);
        userGamificationRepository.save(gamification);
    }
    
    // Handle daily login
    public void handleDailyLogin(User user) {
        UserGamification gamification = getUserGamificationOrCreate(user);
        LocalDate today = LocalDate.now();
        LocalDate lastLogin = gamification.getLastLoginDate();
        
        if (lastLogin == null || !lastLogin.equals(today)) {
            // Award daily login points
            Integer basePoints = 10;
            awardPoints(user, basePoints, PointsTransaction.SourceType.DAILY_LOGIN, null, "Daily login bonus");
            
            // Update login streak
            if (lastLogin != null && ChronoUnit.DAYS.between(lastLogin, today) == 1) {
                // Consecutive day
                gamification.setCurrentLoginStreak(gamification.getCurrentLoginStreak() + 1);
            } else if (lastLogin == null || ChronoUnit.DAYS.between(lastLogin, today) > 1) {
                // Check if user has streak freeze tokens
                if (lastLogin != null && ChronoUnit.DAYS.between(lastLogin, today) == 2 && 
                    gamification.getStreakFreezeTokens() > 0) {
                    // Use streak freeze token
                    gamification.setStreakFreezeTokens(gamification.getStreakFreezeTokens() - 1);
                    gamification.setCurrentLoginStreak(gamification.getCurrentLoginStreak() + 1);
                } else {
                    // Reset streak
                    gamification.setCurrentLoginStreak(1);
                }
            }
            
            gamification.setLastLoginDate(today);
            userGamificationRepository.save(gamification);
            
            // Award streak bonuses
            Integer streak = gamification.getCurrentLoginStreak();
            if (streak >= 7 && streak % 7 == 0) {
                Integer bonusPoints = 50 * (streak / 7);
                awardPoints(user, bonusPoints, PointsTransaction.SourceType.LOGIN_STREAK, null, 
                           "Login streak bonus (" + streak + " days)");
            }
        }
    }
    
    // Check and award badges
    public void checkBadgeEligibility(User user) {
        System.out.println("🔍 DEBUG: Checking badge eligibility for user: " + user.getEmail());
        UserGamification gamification = getUserGamificationOrCreate(user);
        List<Badge> allBadges = badgeRepository.findByIsActiveTrue();
        System.out.println("🔍 DEBUG: Found " + allBadges.size() + " active badges to check");
        
        for (Badge badge : allBadges) {
            // Skip if user already has this badge
            if (userBadgeRepository.existsByUserAndBadge(user, badge)) {
                System.out.println("🔍 DEBUG: User already has badge: " + badge.getName());
                continue;
            }
            
            boolean eligible = false;
            
            switch (badge.getConditionType()) {
                case "COURSE_COMPLETE":
                    long completedCourses = progressRepository.countByUserAndIsCompletedTrue(user);
                    eligible = completedCourses >= badge.getConditionValue();
                    System.out.println("🔍 DEBUG: Badge '" + badge.getName() + "' - Completed courses: " + completedCourses + "/" + badge.getConditionValue() + " = " + eligible);
                    break;
                    
                case "QUIZ_PASS":
                    long passedQuizzes = quizAttemptRepository.countPassedQuizzesByUser(user.getId());
                    eligible = passedQuizzes >= badge.getConditionValue();
                    System.out.println("🔍 DEBUG: Badge '" + badge.getName() + "' - Passed quizzes: " + passedQuizzes + "/" + badge.getConditionValue() + " = " + eligible);
                    break;
                    
                case "QUIZ_PERFECT":
                    long perfectQuizzes = quizAttemptRepository.countPerfectScoresByUser(user.getId());
                    eligible = perfectQuizzes >= badge.getConditionValue();
                    System.out.println("🔍 DEBUG: Badge '" + badge.getName() + "' - Perfect quizzes: " + perfectQuizzes + "/" + badge.getConditionValue() + " = " + eligible);
                    break;
                    
                case "LOGIN_STREAK":
                    eligible = gamification.getCurrentLoginStreak() >= badge.getConditionValue();
                    System.out.println("🔍 DEBUG: Badge '" + badge.getName() + "' - Login streak: " + gamification.getCurrentLoginStreak() + "/" + badge.getConditionValue() + " = " + eligible);
                    break;
                    
                case "QUIZ_STREAK":
                    eligible = gamification.getCurrentQuizStreak() >= badge.getConditionValue();
                    System.out.println("🔍 DEBUG: Badge '" + badge.getName() + "' - Quiz streak: " + gamification.getCurrentQuizStreak() + "/" + badge.getConditionValue() + " = " + eligible);
                    break;
                    
                case "POINTS_EARNED":
                    eligible = gamification.getTotalPoints() >= badge.getConditionValue();
                    System.out.println("🔍 DEBUG: Badge '" + badge.getName() + "' - Points: " + gamification.getTotalPoints() + "/" + badge.getConditionValue() + " = " + eligible);
                    break;
                    
                default:
                    System.out.println("🔍 DEBUG: Unknown badge condition type: " + badge.getConditionType());
                    // Handle other badge types as needed
                    break;
            }
            
            if (eligible) {
                System.out.println("🎉 DEBUG: Awarding badge: " + badge.getName());
                awardBadge(user, badge);
            }
        }
    }
    
    // Award a badge to user
    public void awardBadge(User user, Badge badge) {
        System.out.println("🏆 DEBUG: Attempting to award badge '" + badge.getName() + "' to user: " + user.getEmail());
        if (!userBadgeRepository.existsByUserAndBadge(user, badge)) {
            UserBadge userBadge = new UserBadge(user, badge);
            userBadgeRepository.save(userBadge);
            System.out.println("✅ DEBUG: Badge '" + badge.getName() + "' successfully awarded and saved to database!");
            
            // Award bonus points for earning badge
            Integer bonusPoints = 50;
            awardPoints(user, bonusPoints, PointsTransaction.SourceType.BADGE_EARNED, 
                       badge.getId(), "Badge earned: " + badge.getName());
        } else {
            System.out.println("⚠️ DEBUG: User already has badge '" + badge.getName() + "', skipping award.");
        }
    }
    
    // Get user gamification data
    public UserGamification getUserGamification(User user) {
        return getUserGamificationOrCreate(user);
    }
    
    public UserGamification getUserGamificationOrCreate(User user) {
        return userGamificationRepository.findByUser(user)
                .orElseGet(() -> initializeUserGamification(user));
    }
    
    // Get user badges
    public List<UserBadge> getUserBadges(User user) {
        return userBadgeRepository.findByUser(user);
    }

    // Helper methods for debugging
    public Long getPassedQuizzesCount(User user) {
        return quizAttemptRepository.countPassedQuizzesByUser(user.getId());
    }

    public Long getPerfectQuizzesCount(User user) {
        return quizAttemptRepository.countPerfectScoresByUser(user.getId());
    }

    // Get user points history
    public List<PointsTransaction> getUserPointsHistory(User user) {
        return pointsTransactionRepository.findByUserOrderByCreatedAtDesc(user);
    }
    
    // Get leaderboard
    public List<Leaderboard> getGlobalLeaderboard(int limit) {
        return leaderboardRepository.findTopUsersByType(Leaderboard.LeaderboardType.GLOBAL, limit);
    }
    
    public List<Leaderboard> getCourseLeaderboard(Long courseId, int limit) {
        return leaderboardRepository.findByLeaderboardTypeAndReferenceIdOrderByRankPositionAsc(
                Leaderboard.LeaderboardType.COURSE, courseId).stream().limit(limit).toList();
    }
    
    // Update leaderboards (called after points changes)
    public void updateLeaderboards(User user) {
        UserGamification gamification = getUserGamificationOrCreate(user);
        
        // Update global leaderboard
        updateGlobalLeaderboard(user, gamification.getTotalPoints());
        
        // Update course-specific leaderboards for user's enrolled courses
        List<Enrollment> enrollments = enrollmentRepository.findByUser(user);
        for (Enrollment enrollment : enrollments) {
            updateCourseLeaderboard(user, enrollment.getCourse().getId(), gamification.getTotalPoints());
        }
    }
    
    private void updateGlobalLeaderboard(User user, Integer points) {
        Optional<Leaderboard> existing = leaderboardRepository.findByUserAndLeaderboardType(
                user, Leaderboard.LeaderboardType.GLOBAL);
        
        if (existing.isPresent()) {
            Leaderboard leaderboard = existing.get();
            leaderboard.setPoints(points);
            leaderboardRepository.save(leaderboard);
        } else {
            Leaderboard leaderboard = new Leaderboard(user, Leaderboard.LeaderboardType.GLOBAL, points, 0);
            leaderboardRepository.save(leaderboard);
        }
        
        // Recalculate ranks (this could be optimized with a scheduled task)
        recalculateGlobalRanks();
    }
    
    private void updateCourseLeaderboard(User user, Long courseId, Integer points) {
        Optional<Leaderboard> existing = leaderboardRepository.findByUserAndLeaderboardTypeAndReferenceId(
                user, Leaderboard.LeaderboardType.COURSE, courseId);
        
        if (existing.isPresent()) {
            Leaderboard leaderboard = existing.get();
            leaderboard.setPoints(points);
            leaderboardRepository.save(leaderboard);
        } else {
            Leaderboard leaderboard = new Leaderboard(user, Leaderboard.LeaderboardType.COURSE, points, 0);
            leaderboard.setReferenceId(courseId);
            leaderboardRepository.save(leaderboard);
        }
    }
    
    // Recalculate ranks (should be called periodically)
    public void recalculateGlobalRanks() {
        List<Leaderboard> globalLeaderboard = leaderboardRepository
                .findByLeaderboardTypeOrderByRankPositionAsc(Leaderboard.LeaderboardType.GLOBAL);
        
        // Sort by points descending
        globalLeaderboard.sort((a, b) -> b.getPoints().compareTo(a.getPoints()));
        
        for (int i = 0; i < globalLeaderboard.size(); i++) {
            globalLeaderboard.get(i).setRankPosition(i + 1);
        }
        
        leaderboardRepository.saveAll(globalLeaderboard);
    }
    
    // Spend points (for rewards store)
    public boolean spendPoints(User user, Integer points, String description) {
        UserGamification gamification = getUserGamificationOrCreate(user);
        
        if (gamification.getTotalPoints() < points) {
            return false; // Not enough points
        }
        
        // Create transaction record
        PointsTransaction transaction = new PointsTransaction(
            user, points, PointsTransaction.TransactionType.SPENT, 
            PointsTransaction.SourceType.PURCHASE, null, description, BigDecimal.ONE
        );
        pointsTransactionRepository.save(transaction);
        
        // Deduct points
        gamification.setTotalPoints(gamification.getTotalPoints() - points);
        userGamificationRepository.save(gamification);
        
        return true;
    }
}
