package com.lms;

import com.lms.persistence.RegisterUserDto;
import com.lms.persistence.User;
import com.lms.presentation.AdminController;
import com.lms.service.AuthenticationService;
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
import static org.mockito.Mockito.when;

class AdminTest {

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private AdminController adminController;

    private RegisterUserDto adminRegisterUserDto;
    private RegisterUserDto studentRegisterUserDto;
    private User registeredAdmin;
    private User registeredStudent;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        adminRegisterUserDto = MockDataFunctions.mockRegisteredUser("A01", "Ahmed", "Elbeltagy", "ahmed@gmail.com", "password123", "Admin");
        studentRegisterUserDto = MockDataFunctions.mockRegisteredUser("S01", "Laila", "Khaled", "laila@gmail.com", "password123", "Student");
        registeredAdmin = MockDataFunctions.mockAddedUser("A01", "ahmed@gmail.com", "Admin");
        registeredStudent = MockDataFunctions.mockAddedUser("S01", "laila@gmail.com", "Student");
    }

    @Test
    void testUserCreationAfterAuthorization() {
        when(userDetails.getUsername()).thenReturn("ahmed@gmail.com");
        when(userService.findByEmail("ahmed@gmail.com")).thenReturn(Optional.of(registeredAdmin));
        when(authenticationService.signup(studentRegisterUserDto)).thenReturn(registeredStudent);
        ResponseEntity<?> response = adminController.createUser(studentRegisterUserDto);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(registeredStudent, response.getBody());
    }

    @Test
    void testUserCreationWithoutAuthorization() {
        when(userDetails.getUsername()).thenReturn("laila@gmail.com");
        when(userService.findByEmail("laila@gmail.com")).thenReturn(Optional.of(registeredStudent));
        ResponseEntity<?> response = adminController.createUser(studentRegisterUserDto);
        assertEquals(403, response.getStatusCodeValue());
        assertEquals("Access Denied: Access Denied: you are unauthorized", response.getBody());
    }
}
