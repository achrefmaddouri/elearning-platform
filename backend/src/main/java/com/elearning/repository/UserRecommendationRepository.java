package com.elearning.repository;

import com.elearning.model.UserRecommendation;
import com.elearning.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserRecommendationRepository extends JpaRepository<UserRecommendation, Long> {
    
    List<UserRecommendation> findByUserOrderByPriorityScoreDescCreatedAtDesc(User user);
    
    List<UserRecommendation> findByUserAndIsViewedFalseOrderByPriorityScoreDesc(User user);
    
    @Query("SELECT ur FROM UserRecommendation ur WHERE ur.user = :user AND ur.recommendationType = :type")
    List<UserRecommendation> findByUserAndType(@Param("user") User user, 
                                             @Param("type") UserRecommendation.RecommendationType type);
    
    @Query("SELECT ur FROM UserRecommendation ur WHERE ur.user = :user AND " +
           "(ur.expiresAt IS NULL OR ur.expiresAt > :now) " +
           "ORDER BY ur.priorityScore DESC, ur.createdAt DESC")
    List<UserRecommendation> findActiveRecommendations(@Param("user") User user, @Param("now") LocalDateTime now);
    
    @Query("SELECT ur FROM UserRecommendation ur WHERE ur.expiresAt < :now")
    List<UserRecommendation> findExpiredRecommendations(@Param("now") LocalDateTime now);
    
    @Query("SELECT COUNT(ur) FROM UserRecommendation ur WHERE ur.user = :user AND ur.isActedUpon = true")
    Long countActedUponRecommendations(@Param("user") User user);
    
    @Query("SELECT ur.recommendationType, COUNT(ur) FROM UserRecommendation ur " +
           "WHERE ur.user = :user GROUP BY ur.recommendationType")
    List<Object[]> getRecommendationTypeStats(@Param("user") User user);
}
