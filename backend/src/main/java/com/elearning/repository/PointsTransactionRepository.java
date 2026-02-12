package com.elearning.repository;

import com.elearning.model.PointsTransaction;
import com.elearning.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PointsTransactionRepository extends JpaRepository<PointsTransaction, Long> {
    
    List<PointsTransaction> findByUserOrderByCreatedAtDesc(User user);
    
    List<PointsTransaction> findByUserAndTransactionTypeOrderByCreatedAtDesc(User user, PointsTransaction.TransactionType transactionType);
    
    @Query("SELECT SUM(pt.points) FROM PointsTransaction pt WHERE pt.user = ?1 AND pt.transactionType = 'EARNED'")
    Integer getTotalEarnedPointsByUser(User user);
    
    @Query("SELECT SUM(pt.points) FROM PointsTransaction pt WHERE pt.user = ?1 AND pt.transactionType = 'SPENT'")
    Integer getTotalSpentPointsByUser(User user);
    
    @Query("SELECT pt FROM PointsTransaction pt WHERE pt.user = ?1 AND pt.createdAt BETWEEN ?2 AND ?3 ORDER BY pt.createdAt DESC")
    List<PointsTransaction> findByUserAndDateRange(User user, LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT COUNT(pt) FROM PointsTransaction pt WHERE pt.user = ?1 AND pt.sourceType = ?2")
    Long countTransactionsByUserAndSourceType(User user, PointsTransaction.SourceType sourceType);
    
    @Query("SELECT pt FROM PointsTransaction pt WHERE pt.user = ?1 ORDER BY pt.createdAt DESC LIMIT ?2")
    List<PointsTransaction> findRecentTransactionsByUser(User user, int limit);
}
