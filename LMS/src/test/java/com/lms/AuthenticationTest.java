package com.lms;

import com.lms.persistence.LoginResponse;
import com.lms.persistence.LoginUserDto;
import com.lms.persistence.RegisterUserDto;
import com.lms.persistence.User;
import com.lms.presentation.AuthenticationController;
import com.lms.service.AuthenticationService;
import com.lms.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class AuthenticationTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthenticationController authenticationController;

    private RegisterUserDto signUpAdminData;
    private RegisterUserDto createStudentData;
    private User signedUpAdmin;
    private LoginUserDto loginUser;
    private User loggedInUser;
    private String jwtToken;
    private long expirationTime;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        signUpAdminData = MockDataFunctions.mockRegisteredUser("A01", "Ahmed", "Elbeltagy", "ahmed@gmail.com", "password123", "Admin");
        createStudentData = MockDataFunctions.mockRegisteredUser("S01", "Laila", "Khaled", "laila@gmail.com", "password123", "Student");
        signedUpAdmin = MockDataFunctions.mockAddedUser("A01", "ahmed@gmail.com", "Admin");
        loginUser = MockDataFunctions.mockLoggedInUser("laila@gmail.com", "password123");
        loggedInUser = MockDataFunctions.mockAddedUser("S01", "Laila", "Khaled", "laila@gmail.com", "encodedPassword", "Student");
        jwtToken = "abcdefg";
        expirationTime = 3600;
    }


    @Test
    void testRegisterAdmin() {
        when(authenticationService.signup(signUpAdminData)).thenReturn(signedUpAdmin);
        ResponseEntity<?> response = authenticationController.register(signUpAdminData);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(signedUpAdmin, response.getBody());
    }

    @Test
    void testRegisterStudent() {
        ResponseEntity<?> response = authenticationController.register(createStudentData);
        assertEquals(403, response.getStatusCodeValue());
        assertEquals("Access Denied: you are unauthorized", response.getBody());
        verifyNoInteractions(authenticationService);
    }

    @Test
    void testAuthenticationToken() {
        when(authenticationService.authenticate(loginUser)).thenReturn(loggedInUser);
        when(jwtService.generateToken(loggedInUser)).thenReturn(jwtToken);
        when(jwtService.getExpirationTime()).thenReturn(expirationTime);
        ResponseEntity<LoginResponse> response = authenticationController.authenticate(loginUser);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(jwtToken, response.getBody().getToken());
        assertEquals(expirationTime, response.getBody().getExpiresIn());
    }
}
