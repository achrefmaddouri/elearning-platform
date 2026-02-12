package com.elearning.repository;

import com.elearning.model.Reward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RewardRepository extends JpaRepository<Reward, Long> {
    
    List<Reward> findByIsActiveTrueOrderByPointsCostAsc();
    
    List<Reward> findByRewardTypeAndIsActiveTrueOrderByPointsCostAsc(Reward.RewardType rewardType);
    
    @Query("SELECT r FROM Reward r WHERE r.isActive = true AND (r.stockQuantity = -1 OR r.stockQuantity > 0) ORDER BY r.pointsCost ASC")
    List<Reward> findAvailableRewards();
    
    @Query("SELECT r FROM Reward r WHERE r.pointsCost <= ?1 AND r.isActive = true AND (r.stockQuantity = -1 OR r.stockQuantity > 0) ORDER BY r.pointsCost ASC")
    List<Reward> findAffordableRewards(Integer userPoints);
    
    List<Reward> findByCourseIdAndIsActiveTrueOrderByPointsCostAsc(Long courseId);
}
