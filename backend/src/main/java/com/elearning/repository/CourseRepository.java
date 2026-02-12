package com.elearning.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.elearning.model.Course;
import com.elearning.model.CourseStatus;
import com.elearning.model.CourseVisibility;
import com.elearning.model.User;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByInstructor(User instructor);
    
    List<Course> findByStatus(CourseStatus status);
    
    List<Course> findByStatusAndVisibility(CourseStatus status, CourseVisibility visibility);
    
    Optional<Course> findByPrivateUrl(String privateUrl);
    
    @Query("SELECT c FROM Course c JOIN FETCH c.instructor WHERE c.id = :id")
    Optional<Course> findByIdWithInstructor(@Param("id") Long id);
    
    @Query("SELECT c FROM Course c JOIN FETCH c.instructor WHERE c.instructor = :instructor")
    List<Course> findByInstructorWithInstructor(@Param("instructor") User instructor);
    
    @Query("SELECT c FROM Course c WHERE c.status = :status AND c.visibility = 'PUBLIC'")
    List<Course> findPublicCoursesByStatus(@Param("status") CourseStatus status);
    
    @Query("SELECT c FROM Course c JOIN FETCH c.instructor WHERE c.status = 'ACCEPTED' AND c.visibility = 'PUBLIC'")
    List<Course> findPublicCoursesWithInstructor();
    
    @Query("SELECT COUNT(c) FROM Course c WHERE c.status = :status")
    long countByStatus(@Param("status") CourseStatus status);
    
    @Query("SELECT c FROM Course c WHERE c.instructor = :instructor AND c.status = :status")
    List<Course> findByInstructorAndStatus(@Param("instructor") User instructor, @Param("status") CourseStatus status);
    
    @Query("SELECT c FROM Course c JOIN FETCH c.instructor WHERE c.status = :status")
    List<Course> findByStatusWithInstructor(@Param("status") CourseStatus status);
}
