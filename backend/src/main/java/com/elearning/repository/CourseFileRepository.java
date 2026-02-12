package com.elearning.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.elearning.model.Course;
import com.elearning.model.CourseFile;
import com.elearning.model.FileType;

@Repository
public interface CourseFileRepository extends JpaRepository<CourseFile, Long> {
    List<CourseFile> findByCourse(Course course);
    
    List<CourseFile> findByCourseAndFileType(Course course, FileType fileType);
}
