package com.elearning.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.elearning.dto.ChatMessageRequest;
import com.elearning.model.ChatMessage;
import com.elearning.model.ChatRoom;
import com.elearning.model.Course;
import com.elearning.model.Role;
import com.elearning.model.User;
import com.elearning.repository.ChatMessageRepository;
import com.elearning.repository.ChatRoomRepository;
import com.elearning.repository.CourseRepository;
import com.elearning.repository.EnrollmentRepository;

@Service
@Transactional
public class ChatService {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private ChatMessageRepository messageRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    public ChatRoom createChatRoom(Long courseId, User user) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // Check if user is enrolled in the course
        boolean isEnrolled = enrollmentRepository.existsByUserAndCourse(user, course);
        if (!isEnrolled) {
            throw new RuntimeException("You must be enrolled in this course to create a chat room");
        }

        // Check if chat room already exists for this user and course
        if (chatRoomRepository.findByCourseAndUser(course, user).isPresent()) {
            throw new RuntimeException("Chat room already exists for this course");
        }

        // Create new chat room
        ChatRoom chatRoom = new ChatRoom(course, user, course.getInstructor());
        return chatRoomRepository.save(chatRoom);
    }

    public ChatRoom getChatRoomByCourse(Long courseId, User user) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // Check if user is enrolled or is the instructor
        boolean isEnrolled = enrollmentRepository.existsByUserAndCourse(user, course);
        boolean isInstructor = course.getInstructor().getId().equals(user.getId());

        if (!isEnrolled && !isInstructor) {
            throw new RuntimeException("Access denied");
        }

        // If user is the instructor, find the first chat room for this course
        // or create one with the instructor as both user and instructor
        if (isInstructor) {
            List<ChatRoom> courseRooms = chatRoomRepository.findByCourse(course);
            if (!courseRooms.isEmpty()) {
                return courseRooms.get(0); // Return first chat room for the course
            } else {
                // Create a chat room for the instructor
                ChatRoom newChatRoom = new ChatRoom(course, user, user);
                return chatRoomRepository.save(newChatRoom);
            }
        }

        // For regular enrolled users, try to find their specific chat room
        return chatRoomRepository.findByCourseAndUser(course, user)
                .orElseGet(() -> {
                    // Create new chat room for enrolled user
                    ChatRoom newChatRoom = new ChatRoom(course, user, course.getInstructor());
                    return chatRoomRepository.save(newChatRoom);
                });
    }

    public List<ChatRoom> getUserChatRooms(User user) {
        if (user.getRole() == Role.INSTRUCTOR) {
            return chatRoomRepository.findByInstructor(user);
        } else {
            return chatRoomRepository.findByUser(user);
        }
    }

    public ChatMessage sendMessage(ChatMessageRequest request, User sender) {
        ChatRoom chatRoom = chatRoomRepository.findById(request.getChatRoomId())
                .orElseThrow(() -> new RuntimeException("Chat room not found"));

        // Verify sender has access to this chat room
        if (!chatRoom.getUser().getId().equals(sender.getId()) && 
            !chatRoom.getInstructor().getId().equals(sender.getId())) {
            throw new RuntimeException("Access denied");
        }

        ChatMessage message = new ChatMessage(chatRoom, sender, request.getMessage());
        return messageRepository.save(message);
    }

    public List<ChatMessage> getChatMessages(Long chatRoomId, User user) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new RuntimeException("Chat room not found"));

        // Verify user has access to this chat room
        if (!chatRoom.getUser().getId().equals(user.getId()) && 
            !chatRoom.getInstructor().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        return messageRepository.findByChatRoomOrderBySentAtAsc(chatRoom);
    }

    public void markMessagesAsRead(Long chatRoomId, User user) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new RuntimeException("Chat room not found"));

        List<ChatMessage> unreadMessages = messageRepository.findUnreadMessagesByChatRoom(chatRoom);
        for (ChatMessage message : unreadMessages) {
            if (!message.getSender().getId().equals(user.getId())) {
                message.setRead(true);
                messageRepository.save(message);
            }
        }
    }
}
