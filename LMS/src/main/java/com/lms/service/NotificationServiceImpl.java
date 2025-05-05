package com.lms.service;

import java.util.List;

import com.lms.events.CourseNotificationEvent;
import com.lms.events.NotificationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.lms.manager.NotificationManager;
import com.lms.persistence.Notification;

@Service
public class NotificationServiceImpl {

    private final NotificationManager notificationManager;
    private final ApplicationEventPublisher eventPublisher;


    public NotificationServiceImpl(NotificationManager notificationManager, ApplicationEventPublisher eventPublisher) {
        this.notificationManager = notificationManager;
        this.eventPublisher = eventPublisher;
    }

    // Add a new notification
    public void addNotification(Notification notification) {
        eventPublisher.publishEvent(new NotificationEvent(this, notification.getUserId(), notification.getMessage(), "EMAIL"));
    }

    // Get all notifications
    public List<Notification> getAllNotifications() {
        return notificationManager.getAllNotifications();
    }

    // Get read notifications
    public List<Notification> getReadNotifications() {
        return notificationManager.getReadNotifications();
    }

    // Get unread notifications
    public List<Notification> getUnreadNotifications() {
        return notificationManager.getUnreadNotifications();
    }

    public void saveNotification(String userId, String message) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setMessage(message);
        notificationManager.addNotification(notification);
    }

    public List<Notification> getAllUserNotifications(String userId) {
        return notificationManager.findByUserId(userId);
    }

    public List<Notification> getUserUnreadNotifications(String userId) {
        return notificationManager.findUnreadByUserId(userId);
    }

    public List<Notification> getUserReadNotifications(String userId) {
        return notificationManager.findReadByUserId(userId);
    }

    public void markNotificationAsRead(String notificationId) {
        notificationManager.markAsRead(notificationId);
    }

    public boolean isUserNotification(String id, String notificationId) {
        return notificationManager.findByUserId(id).stream()
                .anyMatch(notification -> notification.getId().equals(notificationId));
    }

    public void notifyStudents(String courseId, String message) {
        CourseNotificationEvent event = new CourseNotificationEvent(this, courseId, message);
        eventPublisher.publishEvent(event);
    }
}
