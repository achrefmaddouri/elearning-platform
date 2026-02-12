package com.elearning.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.elearning.model.Course;
import com.elearning.model.Quiz;
import com.elearning.model.User;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByCourse(Course course);
    
    @Query("SELECT q FROM Quiz q JOIN FETCH q.course JOIN FETCH q.createdBy WHERE q.course = :course")
    List<Quiz> findByCourseWithCreator(@Param("course") Course course);
    
    List<Quiz> findByCreatedBy(User createdBy);
    
    List<Quiz> findByCourseAndCreatedBy(Course course, User createdBy);
    
    @Query("SELECT q FROM Quiz q JOIN FETCH q.course JOIN FETCH q.createdBy WHERE q.id = :id")
    Optional<Quiz> findByIdWithCourseAndCreator(@Param("id") Long id);
    
    @Query("SELECT q FROM Quiz q JOIN FETCH q.course JOIN FETCH q.createdBy WHERE q.createdBy = :user")
    List<Quiz> findByCreatedByWithCourseAndCreator(@Param("user") User user);
}
