package com.elearning.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.elearning.model.Course;
import com.elearning.model.Enrollment;
import com.elearning.model.User;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByUser(User user);
    
    List<Enrollment> findByCourse(Course course);
    
    Optional<Enrollment> findByUserAndCourse(User user, Course course);
    
    boolean existsByUserAndCourse(User user, Course course);
    
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course = :course")
    long countByCourse(@Param("course") Course course);
}
