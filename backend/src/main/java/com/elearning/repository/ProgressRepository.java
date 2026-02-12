package com.elearning.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.elearning.model.Course;
import com.elearning.model.Progress;
import com.elearning.model.User;

@Repository
public interface ProgressRepository extends JpaRepository<Progress, Long> {
    List<Progress> findByUser(User user);
    
    List<Progress> findByCourse(Course course);
    
    Optional<Progress> findByUserAndCourse(User user, Course course);
    
    List<Progress> findByUserAndIsCompleted(User user, boolean isCompleted);
    
    // New methods for gamification
    Long countByUserAndIsCompleted(User user, boolean isCompleted);
    
    default Long countByUserAndIsCompletedTrue(User user) {
        return countByUserAndIsCompleted(user, true);
    }
    
    @Query("SELECT COUNT(p) FROM Progress p WHERE p.user = ?1 AND p.isCompleted = true")
    Long countCompletedCoursesByUser(User user);
    
    // Analytics-specific methods
    Long countByCourse(Course course);
    
    Long countByCourseAndIsCompleted(Course course, boolean isCompleted);
    
    default Long countByCourseAndIsCompletedTrue(Course course) {
        return countByCourseAndIsCompleted(course, true);
    }
    
    default List<Progress> findByUserAndIsCompletedTrue(User user) {
        return findByUserAndIsCompleted(user, true);
    }
}
