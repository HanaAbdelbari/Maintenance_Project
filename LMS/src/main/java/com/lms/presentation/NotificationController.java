package com.lms.presentation;

import java.util.List;
import java.util.Optional;


import com.lms.business.models.NotificationRequest;
import com.lms.persistence.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.lms.persistence.Notification;
import com.lms.service.NotificationServiceImpl;
import com.lms.service.impl.ServiceFacade;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationServiceImpl notificationService;
    private final ServiceFacade service;


    public NotificationController(NotificationServiceImpl notificationService, ServiceFacade service) {
        this.notificationService = notificationService;
        this.service = service;
    }

    // Add a new notification
    @PostMapping
    public ResponseEntity<String> addNotification(@RequestBody Notification notification) {
        Optional<User> currentUser = service.getCurrentUser();
        if (currentUser.isEmpty()) {
            return ResponseEntity.status(404).build();
        }
        if (!"Instructor".equals(currentUser.get().getRole()) && !"Admin".equals(currentUser.get().getRole())) {
            return ResponseEntity
                    .status(403)
                    .body("Access Denied: You are unauthorized");
        }

        if(service.findUserById(notification.getUserId()) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        notificationService.addNotification(notification);
        return ResponseEntity.ok("Notification added successfully!");
    }

//    // Get all notifications
//    @GetMapping
//    public ResponseEntity<List<Notification>> getAllNotifications() {
//        Optional<User> currentUser = service.getCurrentUser();
//        if (currentUser.isEmpty()) {
//            return ResponseEntity.status(404).build();
//        }
//        return ResponseEntity.ok(notificationService.getAllNotifications());
//    }
//
//    // Get read notifications
//    @GetMapping("/read")
//    public ResponseEntity<List<Notification>> getReadNotifications() {
//        Optional<User> currentUser = service.getCurrentUser();
//        if (currentUser.isEmpty()) {
//            return ResponseEntity.status(404).build();
//        }
//        return ResponseEntity.ok(notificationService.getReadNotifications());
//    }
//
//    // Get unread notifications
//    @GetMapping("/unread")
//    public ResponseEntity<List<Notification>> getUnreadNotifications() {
//        Optional<User> currentUser = service.getCurrentUser();
//        if (currentUser.isEmpty()) {
//            return ResponseEntity.status(404).build();
//        }
//        return ResponseEntity.ok(notificationService.getUnreadNotifications());
//    }

    @GetMapping
    public ResponseEntity<Object> getAllNotifications() {
        Optional<User> currentUser = service.getCurrentUser();
        if (currentUser.isEmpty()) {
            return ResponseEntity.status(404).build();
        }
        try {
            return ResponseEntity.ok(notificationService.getAllUserNotifications(currentUser.get().getId()));
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Notification not found");
        }
    }

    @GetMapping("/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications() {
        Optional<User> currentUser = service.getCurrentUser();
        if (currentUser.isEmpty()) {
            return ResponseEntity.status(404).build();
        }
        return ResponseEntity.ok(notificationService.getUserUnreadNotifications(currentUser.get().getId()));
    }

    @GetMapping("read")
    public ResponseEntity<List<Notification>> getReadNotifications() {
        Optional<User> currentUser = service.getCurrentUser();
        if (currentUser.isEmpty()) {
            return ResponseEntity.status(404).build();
        }
        return ResponseEntity.ok(notificationService.getUserReadNotifications(currentUser.get().getId()));
    }

    @PostMapping("/{notificationId}")
    public ResponseEntity<Object> markNotificationAsRead(@PathVariable String notificationId) {
        Optional<User> currentUser = service.getCurrentUser();
        if (currentUser.isEmpty()) {
            return ResponseEntity.status(404).build();
        }

        if (!notificationService.isUserNotification(currentUser.get().getId(), notificationId)) {
            return ResponseEntity.status(403).body("Access Denied: You are unauthorized to mark this notification as read");
        }

        try {
            notificationService.markNotificationAsRead(notificationId);
            return ResponseEntity.ok().body("Notification marked as read");
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Notification not found");
        }
    }

    @PostMapping("/course/{courseId}")
    public ResponseEntity<String> notifyStudentsInCourse(@PathVariable String courseId, @RequestBody NotificationRequest request) {
        if(service.findCourseById(courseId) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found");
        }
        notificationService.notifyStudents(courseId, request.getMessage());
        return ResponseEntity.ok("Course Notification added successfully!");
    }
}

