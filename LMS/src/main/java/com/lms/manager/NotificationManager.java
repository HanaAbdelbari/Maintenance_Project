package com.lms.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.lms.persistence.Notification;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Service
@Scope("singleton")
      //Replace all instances of .collect(Collectors.toList()) with .toList() in NotificationManager class
       //Since Java 16, Stream.toList() is preferred because it returns an unmodifiable List
public class NotificationManager {
    private final List<Notification> notifications = new ArrayList<>();

    // Add a notification
    public void addNotification(Notification notification) {
        notifications.add(notification);
//        sendEmail(notification.getUserId(), "New Notification", notification.getMessage());
    }

    // Get all notifications
    public List<Notification> getAllNotifications() {
        return notifications;
    }

    // Get read notifications
    public List<Notification> getReadNotifications() {
        return notifications.stream()
                .filter(Notification::isRead)
                .toList();
    }

    // Get unread notifications
    public List<Notification> getUnreadNotifications() {
        return notifications.stream()
                .filter(notification -> !notification.isRead())
                .toList();
    }

    public List<Notification> findByUserId(String userId) {
        return notifications.stream()
                .filter(notification -> notification.getUserId() != null && notification.getUserId().equals(userId))
                .toList();
    }

    public List<Notification> findUnreadByUserId(String userId) {
        return notifications.stream()
                .filter(notification -> notification.getUserId() != null && notification.getUserId().equals(userId) && !notification.isRead())
                .toList();
    }

    public List<Notification> findReadByUserId(String userId) {
        return notifications.stream()
                .filter(notification -> notification.getUserId() != null && notification.getUserId().equals(userId) && notification.isRead())
                .toList();
    }

    public void markAsRead(String notificationId) {
        notifications.stream()
                .filter(notification -> notification.getId().equals(notificationId))
                .findFirst()
                .ifPresent(notification -> notification.setRead(true));
    }

    /*
     Joseph
        private void sendEmail(String userId, String subject, String body) {
            // Replace this with actual email sending logic
            System.out.println("Sending email to user " + userId);
            System.out.println("Subject: " + subject);
            System.out.println("Body: " + body);
        }
    */
}