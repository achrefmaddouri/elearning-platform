package com.elearning.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.elearning.model.VideoMaterial;

@Repository
public interface VideoMaterialRepository extends JpaRepository<VideoMaterial, Long> {
    
    List<VideoMaterial> findByCourseIdOrderByOrderIndexAsc(Long courseId);
    
    List<VideoMaterial> findByCourseId(Long courseId);
    
    @Query("SELECT vm FROM VideoMaterial vm WHERE vm.course.id = :courseId AND vm.course.instructor.id = :instructorId")
    List<VideoMaterial> findByCourseIdAndInstructorId(@Param("courseId") Long courseId, @Param("instructorId") Long instructorId);
    
    long countByCourseId(Long courseId);
    
    void deleteByCourseId(Long courseId);
}
