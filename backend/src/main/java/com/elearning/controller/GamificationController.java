package com.elearning.controller;

import com.elearning.model.*;
import com.elearning.repository.RewardRepository;
import com.elearning.service.GamificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/gamification")
@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("hasRole('USER') or hasRole('INSTRUCTOR') or hasRole('ADMIN')")
public class GamificationController {

    @Autowired
    private GamificationService gamificationService;

    @Autowired
    private RewardRepository rewardRepository;

    // Get user's gamification profile
    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getUserGamificationProfile(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        
        UserGamification gamification = gamificationService.getUserGamification(user);
        List<UserBadge> badges = gamificationService.getUserBadges(user);
        
        Map<String, Object> profile = new HashMap<>();
        profile.put("totalPoints", gamification.getTotalPoints());
        profile.put("currentLoginStreak", gamification.getCurrentLoginStreak());
        profile.put("longestLoginStreak", gamification.getLongestLoginStreak());
        profile.put("currentCourseStreak", gamification.getCurrentCourseStreak());
        profile.put("currentQuizStreak", gamification.getCurrentQuizStreak());
        profile.put("streakFreezeTokens", gamification.getStreakFreezeTokens());
        profile.put("badges", badges);
        profile.put("badgeCount", badges.size());
        
        return ResponseEntity.ok(profile);
    }

    // Get user's badges
    @GetMapping("/badges")
    public ResponseEntity<List<UserBadge>> getUserBadges(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<UserBadge> badges = gamificationService.getUserBadges(user);
        return ResponseEntity.ok(badges);
    }

    // Get user's points history
    @GetMapping("/points/history")
    public ResponseEntity<List<PointsTransaction>> getUserPointsHistory(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<PointsTransaction> history = gamificationService.getUserPointsHistory(user);
        return ResponseEntity.ok(history);
    }

    // Get global leaderboard
    @GetMapping("/leaderboard/global")
    public ResponseEntity<List<Leaderboard>> getGlobalLeaderboard(@RequestParam(defaultValue = "10") int limit) {
        List<Leaderboard> leaderboard = gamificationService.getGlobalLeaderboard(limit);
        return ResponseEntity.ok(leaderboard);
    }

    // Get course leaderboard
    @GetMapping("/leaderboard/course/{courseId}")
    public ResponseEntity<List<Leaderboard>> getCourseLeaderboard(
            @PathVariable Long courseId, 
            @RequestParam(defaultValue = "10") int limit) {
        List<Leaderboard> leaderboard = gamificationService.getCourseLeaderboard(courseId, limit);
        return ResponseEntity.ok(leaderboard);
    }

    // Get available rewards
    @GetMapping("/rewards")
    public ResponseEntity<List<Reward>> getAvailableRewards(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        UserGamification gamification = gamificationService.getUserGamification(user);
        
        List<Reward> rewards = rewardRepository.findAffordableRewards(gamification.getTotalPoints());
        return ResponseEntity.ok(rewards);
    }

    // Get all rewards (for display)
    @GetMapping("/rewards/all")
    public ResponseEntity<List<Reward>> getAllRewards() {
        List<Reward> rewards = rewardRepository.findAvailableRewards();
        return ResponseEntity.ok(rewards);
    }

    // Purchase reward
    @PostMapping("/rewards/{rewardId}/purchase")
    public ResponseEntity<Map<String, Object>> purchaseReward(
            @PathVariable Long rewardId, 
            Authentication authentication) {
        
        User user = (User) authentication.getPrincipal();
        
        try {
            Reward reward = rewardRepository.findById(rewardId)
                    .orElseThrow(() -> new RuntimeException("Reward not found"));
            
            if (!reward.getIsActive()) {
                throw new RuntimeException("Reward is not available");
            }
            
            if (reward.getStockQuantity() != null && reward.getStockQuantity() == 0) {
                throw new RuntimeException("Reward is out of stock");
            }
            
            boolean success = gamificationService.spendPoints(user, reward.getPointsCost(), 
                    "Purchased reward: " + reward.getName());
            
            if (!success) {
                throw new RuntimeException("Insufficient points");
            }
            
            // Decrease stock if limited
            if (reward.getStockQuantity() != null && reward.getStockQuantity() > 0) {
                reward.setStockQuantity(reward.getStockQuantity() - 1);
                rewardRepository.save(reward);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Reward purchased successfully!");
            response.put("reward", reward);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Manually check for new badges (useful for testing)
    @PostMapping("/badges/check")
    public ResponseEntity<Map<String, Object>> checkBadges(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        
        int badgesBefore = gamificationService.getUserBadges(user).size();
        System.out.println("🔍 DEBUG: Manual badge check triggered for user: " + user.getEmail());
        gamificationService.checkBadgeEligibility(user);
        int badgesAfter = gamificationService.getUserBadges(user).size();
        
        Map<String, Object> response = new HashMap<>();
        response.put("badgesEarned", badgesAfter - badgesBefore);
        response.put("totalBadges", badgesAfter);
        
        return ResponseEntity.ok(response);
    }

    // Debug endpoint to check quiz attempts
    @GetMapping("/debug/quiz-stats")
    public ResponseEntity<Map<String, Object>> getDebugQuizStats(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        
        // Get quiz statistics for debugging
        Long passedQuizzes = gamificationService.getPassedQuizzesCount(user);
        Long perfectQuizzes = gamificationService.getPerfectQuizzesCount(user);
        UserGamification gamification = gamificationService.getUserGamification(user);
        
        Map<String, Object> response = new HashMap<>();
        response.put("userId", user.getId());
        response.put("userEmail", user.getEmail());
        response.put("passedQuizzes", passedQuizzes);
        response.put("perfectQuizzes", perfectQuizzes);
        response.put("currentQuizStreak", gamification.getCurrentQuizStreak());
        response.put("totalPoints", gamification.getTotalPoints());
        response.put("totalBadges", gamificationService.getUserBadges(user).size());
        
        return ResponseEntity.ok(response);
    }

    // Get user's rank in global leaderboard
    @GetMapping("/rank/global")
    public ResponseEntity<Map<String, Object>> getUserGlobalRank(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        
        // This is a simplified version - in production you might want to optimize this
        List<Leaderboard> globalLeaderboard = gamificationService.getGlobalLeaderboard(1000);
        
        for (int i = 0; i < globalLeaderboard.size(); i++) {
            if (globalLeaderboard.get(i).getUser().getId().equals(user.getId())) {
                Map<String, Object> response = new HashMap<>();
                response.put("rank", i + 1);
                response.put("totalUsers", globalLeaderboard.size());
                response.put("points", globalLeaderboard.get(i).getPoints());
                return ResponseEntity.ok(response);
            }
        }
        
        // User not found in leaderboard
        Map<String, Object> response = new HashMap<>();
        response.put("rank", -1);
        response.put("totalUsers", globalLeaderboard.size());
        response.put("points", 0);
        return ResponseEntity.ok(response);
    }

    // Record login for streak tracking
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> recordLogin(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        
        gamificationService.handleDailyLogin(user);
        UserGamification gamification = gamificationService.getUserGamification(user);
        
        Map<String, Object> response = new HashMap<>();
        response.put("currentStreak", gamification.getCurrentLoginStreak());
        response.put("longestStreak", gamification.getLongestLoginStreak());
        response.put("totalPoints", gamification.getTotalPoints());
        
        return ResponseEntity.ok(response);
    }
}
