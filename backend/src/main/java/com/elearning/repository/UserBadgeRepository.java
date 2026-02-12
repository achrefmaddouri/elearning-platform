package com.elearning.repository;

import com.elearning.model.User;
import com.elearning.model.UserBadge;
import com.elearning.model.Badge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {
    
    List<UserBadge> findByUser(User user);
    
    List<UserBadge> findByUserAndIsDisplayedTrue(User user);
    
    Optional<UserBadge> findByUserAndBadge(User user, Badge badge);
    
    boolean existsByUserAndBadge(User user, Badge badge);
    
    @Query("SELECT COUNT(ub) FROM UserBadge ub WHERE ub.user = ?1")
    Long countBadgesByUser(User user);
    
    @Query("SELECT ub FROM UserBadge ub WHERE ub.user.id = ?1 AND ub.isDisplayed = true ORDER BY ub.earnedAt DESC")
    List<UserBadge> findDisplayedBadgesByUserId(Long userId);
    
    @Query("SELECT COUNT(ub) FROM UserBadge ub WHERE ub.badge.badgeType = ?1 AND ub.user = ?2")
    Long countBadgesByTypeAndUser(Badge.BadgeType badgeType, User user);
}
