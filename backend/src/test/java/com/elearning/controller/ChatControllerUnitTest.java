package com.elearning.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import com.elearning.dto.ChatMessageRequest;
import com.elearning.dto.CreateChatRoomRequest;
import com.elearning.model.ChatMessage;
import com.elearning.model.ChatRoom;
import com.elearning.model.User;
import com.elearning.service.ChatService;

public class ChatControllerUnitTest {

    @Mock
    private ChatService chatService;

    @InjectMocks
    private ChatController controller;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getUserChatRooms_returnsOk() {
        User user = com.elearning.TestUtils.buildUser(33L, "u@example.com", com.elearning.model.Role.USER);
        Authentication auth = com.elearning.TestUtils.mockAuthenticationWith(user);

        when(chatService.getUserChatRooms(user)).thenReturn(Collections.emptyList());

        ResponseEntity<List<ChatRoom>> resp = controller.getUserChatRooms(auth);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    public void createChatRoom_returnsOk() {
        Authentication auth = mock(Authentication.class);
        User user = mock(User.class);
        when(auth.getPrincipal()).thenReturn(user);

        CreateChatRoomRequest req = new CreateChatRoomRequest();
        req.setCourseId(10L);

        ChatRoom room = mock(ChatRoom.class);
        when(chatService.createChatRoom(10L, user)).thenReturn(room);

        ResponseEntity<?> resp = controller.createChatRoom(req, auth);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    public void getChatRoomByCourse_returnsOk() {
        Authentication auth = mock(Authentication.class);
        User user = mock(User.class);
        when(auth.getPrincipal()).thenReturn(user);

        ChatRoom room = mock(ChatRoom.class);
        when(chatService.getChatRoomByCourse(5L, user)).thenReturn(room);

        ResponseEntity<?> resp = controller.getChatRoomByCourse(5L, auth);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    public void getChatMessages_returnsOk() {
        Authentication auth = mock(Authentication.class);
        User user = mock(User.class);
        when(auth.getPrincipal()).thenReturn(user);

        when(chatService.getChatMessages(7L, user)).thenReturn(Collections.emptyList());

        ResponseEntity<?> resp = controller.getChatMessages(7L, auth);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    public void sendMessage_returnsOk() {
        Authentication auth = mock(Authentication.class);
        User user = mock(User.class);
        when(auth.getPrincipal()).thenReturn(user);

        com.elearning.dto.ChatMessageRequest req = com.elearning.TestUtils.buildChatMessageRequest(2L, "Hello");

        com.elearning.model.ChatMessage msg = com.elearning.TestUtils.mockChatMessage();
        when(chatService.sendMessage(req, user)).thenReturn(msg);

        ResponseEntity<?> resp = controller.sendMessage(req, auth);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    public void markMessagesAsRead_returnsOk() {
        Authentication auth = mock(Authentication.class);
        User user = mock(User.class);
        when(auth.getPrincipal()).thenReturn(user);

        doNothing().when(chatService).markMessagesAsRead(3L, user);

        ResponseEntity<?> resp = controller.markMessagesAsRead(3L, auth);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }
}
