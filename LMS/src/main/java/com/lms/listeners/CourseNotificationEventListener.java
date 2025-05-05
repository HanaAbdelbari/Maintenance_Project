package com.lms.listeners;

import com.lms.events.CourseNotificationEvent;
import com.lms.manager.NotificationManager;
import com.lms.persistence.Notification;
import com.lms.service.EnrollmentService;
import com.lms.service.EnrollmentServiceImpl;
import com.lms.service.UserService;
import com.lms.service.impl.EmailService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class CourseNotificationEventListener {


    private final EmailService emailService;     // Service to send emails
    private final EnrollmentService enrollmentService;     // Service to send emails
    private final UserService userService;
    private final NotificationManager notificationManager;

    public CourseNotificationEventListener(EnrollmentService enrollmentService, EmailService emailService, EnrollmentServiceImpl enrollmentServiceImpl, UserService userService, NotificationManager notificationManager) {
        this.enrollmentService = enrollmentService;
        this.emailService = emailService;
        this.userService = userService;
        this.notificationManager = notificationManager;
    }

    @EventListener
    public void handleCourseNotificationEvent(CourseNotificationEvent event) {
        String courseId = event.getCourseId();
        String message = event.getMessage();

        Notification notification = new Notification();
        notification.setMessage(event.getMessage());

        // Fetch students registered in the course
        enrollmentService.getEnrollmentsByCourse(courseId).forEach(enrollment -> {
            notification.setUserId(enrollment.getsId());
            notificationManager.addNotification(notification);
            String email = userService.findById(enrollment.getsId()).getEmail();
            String subject = "Notification for Course: " + courseId;
            new Thread(() -> {
                try {
                    emailService.sendEmail(email, subject, message);
                } catch (Exception e) {
                    System.err.println("Couldn't send the email: " + e);
                }
            }).start();
        });

        System.out.println("Notification sent to all students in course: " + courseId);
    }
}
