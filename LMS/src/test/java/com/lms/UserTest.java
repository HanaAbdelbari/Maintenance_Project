package com.lms;

import com.lms.persistence.UpdateUserDto;
import com.lms.persistence.User;
import com.lms.persistence.UserInfoDto;
import com.lms.presentation.UserController;
import com.lms.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserTest {

    @Mock
    private UserService userService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private UserController userController;

    private User currentUser;
    private UserInfoDto userInfoDto;
    private UpdateUserDto updateUserDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        currentUser = new User()
                .setId("S01")
                .setFirstName("Laila")
                .setLastName("Khaled")
                .setEmail("laila@gmail.com")
                .setPassword("password123")
                .setRole("Student");

        userInfoDto = new UserInfoDto(
                currentUser.getId(),
                currentUser.getFirstName(),
                currentUser.getLastName(),
                currentUser.getEmail(),
                currentUser.getPassword()
        );

        updateUserDto = new UpdateUserDto();
        updateUserDto.setFirstName("Leila");
        updateUserDto.setLastName("Hussein");
        updateUserDto.setPassword("newPassword123");
    }

    @Test
    void testViewAccountInfoWhenAuthenticated() {
        when(userDetails.getUsername()).thenReturn(currentUser.getEmail());
        when(userService.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
        ResponseEntity<?> response = userController.getAccountInfo();
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testViewAccountInfoWhenUnauthenticated() {
        when(authentication.getPrincipal()).thenReturn(null);
        ResponseEntity<?> response = userController.getAccountInfo();
        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Unauthorized: Please log in.", response.getBody());
    }

    @Test
    void testViewAccountInfoOfUnknownUser() {
        when(userDetails.getUsername()).thenReturn("unknown@gmail.com");
        when(userService.findByEmail("unknown@gmail.com")).thenReturn(Optional.empty());
        ResponseEntity<?> response = userController.getAccountInfo();
        assertEquals(404, response.getStatusCodeValue());
        assertEquals("User not found.", response.getBody());
    }

    @Test
    void testUpdateUserInfoWithValidData() {
        when(userDetails.getUsername()).thenReturn(currentUser.getEmail());
        when(userService.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
        ResponseEntity<?> response = userController.updateUser(updateUserDto);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Your info is updated", response.getBody());
        verify(userService).save(any(User.class));
    }

    @Test
    void testUpdateUserInfoWithUnauthinticatedUser() {
        when(authentication.getPrincipal()).thenReturn(null);
        ResponseEntity<?> response = userController.updateUser(updateUserDto);
        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Unauthorized: Please log in.", response.getBody());
    }

    @Test
    void testUpdateUserInfoWithUnknownUser() {
        when(userDetails.getUsername()).thenReturn("unknown@gmail.com");
        when(userService.findByEmail("unknown@gmail.com")).thenReturn(Optional.empty());
        ResponseEntity<?> response = userController.updateUser(updateUserDto);
        assertEquals(404, response.getStatusCodeValue());
        assertEquals("User not found.", response.getBody());
    }
}
