package com.elearning.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import com.elearning.dto.ChangePasswordRequest;
import com.elearning.dto.UpdateUserRequest;
import com.elearning.model.User;
import com.elearning.service.UserService;

public class UserControllerUnitTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController controller;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getUserProfile_returnsOk() {
        Authentication auth = mock(Authentication.class);
        User principal = mock(User.class);
        when(auth.getPrincipal()).thenReturn(principal);
        when(principal.getId()).thenReturn(42L);

        User profile = mock(User.class);
        when(userService.getUserById(42L)).thenReturn(profile);

        ResponseEntity<User> resp = controller.getUserProfile(auth);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(profile, resp.getBody());
    }

    @Test
    public void updateUserProfile_returnsOk() {
        Authentication auth = mock(Authentication.class);
        User principal = mock(User.class);
        when(auth.getPrincipal()).thenReturn(principal);
        when(principal.getId()).thenReturn(50L);

        UpdateUserRequest req = new UpdateUserRequest("First", "Last", "email@example.com");
        User updated = mock(User.class);
        when(userService.updateUserProfile(50L, req)).thenReturn(updated);

        ResponseEntity<User> resp = controller.updateUserProfile(req, auth);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(updated, resp.getBody());
    }

    @Test
    public void changePassword_returnsOk_onSuccess() {
        Authentication auth = mock(Authentication.class);
        User principal = mock(User.class);
        when(auth.getPrincipal()).thenReturn(principal);
        when(principal.getId()).thenReturn(60L);

        ChangePasswordRequest req = new ChangePasswordRequest("oldpwd", "newpwd");
        doNothing().when(userService).changePassword(60L, req);

        ResponseEntity<?> resp = controller.changePassword(req, auth);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }
}
