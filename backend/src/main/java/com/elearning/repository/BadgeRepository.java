package com.elearning.repository;

import com.elearning.model.Badge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, Long> {
    
    List<Badge> findByIsActiveTrue();
    
    List<Badge> findByBadgeType(Badge.BadgeType badgeType);
    
    List<Badge> findByBadgeTypeAndIsActiveTrue(Badge.BadgeType badgeType);
    
    List<Badge> findByConditionType(String conditionType);
    
    List<Badge> findByConditionTypeAndIsActiveTrue(String conditionType);
    
    @Query("SELECT b FROM Badge b WHERE b.conditionType = ?1 AND b.conditionValue <= ?2 AND b.isActive = true")
    List<Badge> findEligibleBadges(String conditionType, Integer value);
}
