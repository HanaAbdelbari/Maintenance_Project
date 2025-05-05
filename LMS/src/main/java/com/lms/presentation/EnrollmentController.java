package com.lms.presentation;

import com.lms.events.CourseNotificationEvent;
import com.lms.events.NotificationEvent;
import com.lms.persistence.Course;
import com.lms.persistence.Enrollment;
import com.lms.persistence.User;
import com.lms.service.CourseService;
import com.lms.service.EnrollmentService;
import com.lms.service.UserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    private final UserService userService;
    public ApplicationEventPublisher eventPublisher;
    private CourseService courseService;

    public EnrollmentController(EnrollmentService enrollmentService, UserService userService, ApplicationEventPublisher eventPublisher, CourseService courseService) {
        this.enrollmentService = enrollmentService;
        this.userService = userService;
        this.eventPublisher = eventPublisher;
        this.courseService = courseService;
    }

    @PostMapping("/enroll")
    public ResponseEntity<String> enrollStudent(@RequestParam String studentId, @RequestParam String courseId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails currentUserDetails = (UserDetails) authentication.getPrincipal();
        Optional<User> currentUser = userService.findByEmail(currentUserDetails.getUsername());

        if (currentUser.isEmpty()) {
            return ResponseEntity.status(404).build();
        }
        if (!"Student".equals(currentUser.get().getRole())) {
            return ResponseEntity.status(403).body("Access Denied: Access Denied: you are unauthorized");
        }

        Course course = courseService.findCourseById(courseId);
        if (course == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found");
        }

        enrollmentService.enrollStudent(studentId, courseId);

        String studentMessage = "you Enrolled in course " + courseId + " successfully";
        eventPublisher.publishEvent(new NotificationEvent(this, currentUser.get().getId(), studentMessage, "EMAIL"));
        String instructorMessage = "New Student "+ studentId +" Enrolled in course  " + courseId + " \"" + course.getTitle() + "\"";
        eventPublisher.publishEvent(new NotificationEvent(this, course.getProfid(), instructorMessage, "EMAIL"));

        return ResponseEntity.ok("Student enrolled successfully");
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<String> getEnrollmentsByCourse(@PathVariable String courseId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails currentUserDetails = (UserDetails) authentication.getPrincipal();
        Optional<User> currentUser = userService.findByEmail(currentUserDetails.getUsername());
        if (currentUser.isEmpty()) {
            return ResponseEntity.status(404).build();
        }


        List<Enrollment> enrollments = enrollmentService.getEnrollmentsByCourse(courseId);
        return ResponseEntity.ok(enrollments.toString());
    }

}
