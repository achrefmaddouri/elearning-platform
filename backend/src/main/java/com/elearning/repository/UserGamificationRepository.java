package com.elearning.repository;

import com.elearning.model.User;
import com.elearning.model.UserGamification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserGamificationRepository extends JpaRepository<UserGamification, Long> {
    
    Optional<UserGamification> findByUser(User user);
    
    Optional<UserGamification> findByUserId(Long userId);
    
    @Query("SELECT ug FROM UserGamification ug ORDER BY ug.totalPoints DESC")
    List<UserGamification> findTopUsersByPoints();
    
    @Query("SELECT ug FROM UserGamification ug ORDER BY ug.totalPoints DESC LIMIT ?1")
    List<UserGamification> findTopUsersByPointsLimit(int limit);
    
    @Query("SELECT ug FROM UserGamification ug ORDER BY ug.currentLoginStreak DESC")
    List<UserGamification> findTopUsersByLoginStreak();
    
    @Query("SELECT COUNT(ug) FROM UserGamification ug WHERE ug.totalPoints > ?1")
    Long countUsersWithPointsGreaterThan(Integer points);
}
