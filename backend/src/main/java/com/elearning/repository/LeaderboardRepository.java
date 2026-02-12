package com.elearning.repository;

import com.elearning.model.Leaderboard;
import com.elearning.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LeaderboardRepository extends JpaRepository<Leaderboard, Long> {
    
    List<Leaderboard> findByLeaderboardTypeOrderByRankPositionAsc(Leaderboard.LeaderboardType leaderboardType);
    
    List<Leaderboard> findByLeaderboardTypeAndReferenceIdOrderByRankPositionAsc(Leaderboard.LeaderboardType leaderboardType, Long referenceId);
    
    Optional<Leaderboard> findByUserAndLeaderboardTypeAndReferenceId(User user, Leaderboard.LeaderboardType leaderboardType, Long referenceId);
    
    Optional<Leaderboard> findByUserAndLeaderboardType(User user, Leaderboard.LeaderboardType leaderboardType);
    
    @Query("SELECT l FROM Leaderboard l WHERE l.leaderboardType = ?1 AND l.periodStart = ?2 AND l.periodEnd = ?3 ORDER BY l.rankPosition ASC")
    List<Leaderboard> findByTypeAndPeriod(Leaderboard.LeaderboardType leaderboardType, LocalDate periodStart, LocalDate periodEnd);
    
    @Query("SELECT l FROM Leaderboard l WHERE l.leaderboardType = ?1 ORDER BY l.rankPosition ASC LIMIT ?2")
    List<Leaderboard> findTopUsersByType(Leaderboard.LeaderboardType leaderboardType, int limit);
    
    void deleteByLeaderboardTypeAndPeriodStartAndPeriodEnd(Leaderboard.LeaderboardType leaderboardType, LocalDate periodStart, LocalDate periodEnd);
    
    @Query("SELECT l FROM Leaderboard l WHERE l.user = ?1 ORDER BY l.rankPosition ASC")
    List<Leaderboard> findAllUserRankings(User user);
}
