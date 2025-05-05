package com.lms;

import com.lms.persistence.OtpRequest;
import com.lms.persistence.OtpResponseDto;
import com.lms.persistence.OtpStatus;
import com.lms.presentation.OtpController;
import com.lms.service.SmsService;
import com.lms.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
class OtpTest {

    @Autowired
    private OtpController otpController;

    @Autowired
    private SmsService smsService;

    @Mock
    private UserService userService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("stu@gmail.com");
        when(userService.findByEmail("stu@gmail.com")).thenReturn(MockDataFunctions.mockOptionalUser("S01", "stu@gmail.com"));
    }

    @Test
    void testSendOtpSuccess() {
        OtpRequest otpRequest = MockDataFunctions.mockOtpRequest("maya", "+201014367954", "Lesson2");
        OtpResponseDto mockResponse = new OtpResponseDto(OtpStatus.DELIVERED, "Mocked OTP sent successfully.");
        when(smsService.sendSMS(any(OtpRequest.class), any(Optional.class))).thenReturn(mockResponse);
        OtpResponseDto response = otpController.sendOtp(otpRequest);
        assertEquals(OtpStatus.DELIVERED, response.getStatus());
        assertEquals("Mocked OTP sent successfully.", response.getMessage());
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public SmsService smsService() {
            return mock(SmsService.class);
        }
    }
}
