package com.elearning.repository;

import com.elearning.model.ChatRoom;
import com.elearning.model.Course;
import com.elearning.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    List<ChatRoom> findByUser(User user);
    
    List<ChatRoom> findByInstructor(User instructor);
    
    List<ChatRoom> findByCourse(Course course);
    
    Optional<ChatRoom> findByCourseAndUser(Course course, User user);
}
