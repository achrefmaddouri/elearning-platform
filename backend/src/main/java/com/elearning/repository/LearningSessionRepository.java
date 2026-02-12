package com.elearning.repository;

import com.elearning.model.LearningSession;
import com.elearning.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LearningSessionRepository extends JpaRepository<LearningSession, Long> {
    
    List<LearningSession> findByUserOrderBySessionStartDesc(User user);
    
    List<LearningSession> findByUserAndSessionStartBetween(User user, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT ls FROM LearningSession ls WHERE ls.user = :user AND ls.sessionEnd IS NULL")
    List<LearningSession> findActiveSessions(@Param("user") User user);
    
    @Query("SELECT HOUR(ls.sessionStart) as hour, COUNT(ls) as count, AVG(ls.engagementScore) as avgEngagement " +
           "FROM LearningSession ls WHERE ls.user = :user " +
           "GROUP BY HOUR(ls.sessionStart) ORDER BY count DESC")
    List<Object[]> findLearningPatternsByHour(@Param("user") User user);
    
    @Query("SELECT ls.activityType, COUNT(ls) as count, AVG(ls.durationMinutes) as avgDuration " +
           "FROM LearningSession ls WHERE ls.user = :user " +
           "GROUP BY ls.activityType")
    List<Object[]> findActivityPatterns(@Param("user") User user);
    
    @Query("SELECT SUM(ls.durationMinutes) FROM LearningSession ls WHERE ls.user = :user AND ls.course.id = :courseId")
    Long getTotalTimeSpentOnCourse(@Param("user") User user, @Param("courseId") Long courseId);
    
    @Query("SELECT AVG(ls.engagementScore) FROM LearningSession ls WHERE ls.user = :user AND ls.sessionStart >= :since")
    Double getAverageEngagementSince(@Param("user") User user, @Param("since") LocalDateTime since);
    
    @Query("SELECT DATE(ls.sessionStart) as date, COUNT(DISTINCT ls.user) as activeUsers " +
           "FROM LearningSession ls WHERE ls.course.id = :courseId " +
           "GROUP BY DATE(ls.sessionStart) ORDER BY date DESC")
    List<Object[]> getDailyActiveUsersByCourse(@Param("courseId") Long courseId);
}
