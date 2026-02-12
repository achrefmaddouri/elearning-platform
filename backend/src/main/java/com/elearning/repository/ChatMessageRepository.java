package com.elearning.repository;

import com.elearning.model.ChatMessage;
import com.elearning.model.ChatRoom;
import com.elearning.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatRoomOrderBySentAtAsc(ChatRoom chatRoom);
    
    List<ChatMessage> findBySender(User sender);
    
    @Query("SELECT m FROM ChatMessage m WHERE m.chatRoom = :chatRoom AND m.isRead = false")
    List<ChatMessage> findUnreadMessagesByChatRoom(@Param("chatRoom") ChatRoom chatRoom);
}
