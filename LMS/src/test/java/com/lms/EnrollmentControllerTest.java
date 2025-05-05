package com.lms;

import java.lang.reflect.Field;

import com.lms.events.NotificationEvent;
import com.lms.persistence.Course;
import com.lms.persistence.Enrollment;
import com.lms.persistence.User;
import com.lms.presentation.EnrollmentController;
import com.lms.service.CourseService;
import com.lms.service.EnrollmentService;
import com.lms.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class EnrollmentControllerTest {

    @Mock
    private EnrollmentService enrollmentService;

    @Mock
    private UserService userService;

    @Mock
    private CourseService courseService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private EnrollmentController enrollmentController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        enrollmentController = new EnrollmentController(
                enrollmentService,
                userService,
                mock(ApplicationEventPublisher.class),
                courseService
        );
    }

    @Test
    void testEnrollStudent_Success() {

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("student@example.com");

        User user = new User();
        user.setRole("Student");
        user.setId("S01");

        Course course = new Course("101", "JAVA", "Programming language", 3, "P01");
        when(courseService.findCourseById("101")).thenReturn(course);

        when(userService.findByEmail("student@example.com")).thenReturn(Optional.of(user));
        doNothing().when(enrollmentService).enrollStudent("S01", "101");

        ApplicationEventPublisher mockEventPublisher = enrollmentController.eventPublisher;


        ResponseEntity<String> response = enrollmentController.enrollStudent("S01", "101");

        assertEquals(200, response.getStatusCodeValue(), "Expected status code should be 200.");
        assertEquals("Student enrolled successfully", response.getBody(), "Expected response body does not match.");
        verify(enrollmentService, times(1)).enrollStudent("S01", "101");
        verify(userService, times(1)).findByEmail("student@example.com");
        verify(courseService, times(1)).findCourseById("101");
        verify(mockEventPublisher, times(2)).publishEvent(any(NotificationEvent.class));
    }




    @Test
    void testEnrollStudent_UserNotFound() {

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("unknown@example.com");
        when(userService.findByEmail("unknown@example.com")).thenReturn(Optional.empty());


        ResponseEntity<String> response = enrollmentController.enrollStudent("1", "101");


        assertEquals(404, response.getStatusCodeValue());
        verify(enrollmentService, never()).enrollStudent(anyString(), anyString());
    }


@Test
void testGetEnrollmentsByCourse() {

    when(authentication.getPrincipal()).thenReturn(userDetails);
    when(userDetails.getUsername()).thenReturn("instructor@example.com");
    User user = new User();
    user.setRole("Instructor");
    user.setId("I01");
    when(userService.findByEmail("instructor@example.com")).thenReturn(Optional.of(user));
    Enrollment enrollment = new Enrollment("E1", "S01", "101");
    List<Enrollment> enrollments = List.of(enrollment);
    when(enrollmentService.getEnrollmentsByCourse("101")).thenReturn(enrollments);

    ResponseEntity<?> response = enrollmentController.getEnrollmentsByCourse("101");

    assertEquals(200, response.getStatusCodeValue());
    assertEquals(enrollments.toString(), response.getBody()); 
    verify(enrollmentService, times(1)).getEnrollmentsByCourse("101");
}

}
