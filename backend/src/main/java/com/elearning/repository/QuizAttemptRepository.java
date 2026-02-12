package com.elearning.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.elearning.model.Quiz;
import com.elearning.model.QuizAttempt;
import com.elearning.model.User;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
    List<QuizAttempt> findByUser(User user);
    
    List<QuizAttempt> findByQuiz(Quiz quiz);
    
    Optional<QuizAttempt> findByQuizAndUser(Quiz quiz, User user);
    
    List<QuizAttempt> findByQuizAndUserOrderByAttemptedAtDesc(Quiz quiz, User user);
    
    // New methods for gamification
    @Query("SELECT COUNT(DISTINCT qa.quiz) FROM QuizAttempt qa WHERE qa.user.id = ?1 AND (qa.score * 100.0 / qa.totalQuestions) >= 75.0")
    Long countPassedQuizzesByUser(Long userId);
    
    @Query("SELECT COUNT(qa) FROM QuizAttempt qa WHERE qa.user.id = ?1 AND qa.score = qa.totalQuestions")
    Long countPerfectScoresByUser(Long userId);
    
    @Query("SELECT COUNT(qa) FROM QuizAttempt qa WHERE qa.user = ?1")
    Long countAttemptsByUser(User user);
    
    @Query("SELECT AVG(qa.score * 100.0 / qa.totalQuestions) FROM QuizAttempt qa WHERE qa.user = ?1")
    Double getAverageScoreByUser(User user);
}
