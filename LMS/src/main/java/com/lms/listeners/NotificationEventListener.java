package com.lms.listeners;

import com.lms.events.NotificationEvent;
import com.lms.manager.NotificationManager;
import com.lms.persistence.Notification;
import com.lms.persistence.OtpRequest;
import com.lms.persistence.User;
import com.lms.service.SmsService;
import com.lms.service.impl.EmailService;
import com.lms.service.impl.ServiceFacade;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import com.lms.service.NotificationServiceImpl;

import java.util.Optional;

import static org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties.UiService.LOGGER;


@Component
@AllArgsConstructor
public class NotificationEventListener {

    private final EmailService emailService;
    private final SmsService smsService;
    private final ServiceFacade service;
    private final NotificationManager notificationManager;
        //define a logger in this class
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationEventListener.class);

    @EventListener
    public void handleNotificationEvent(NotificationEvent event) {
        Notification notification = new Notification();
        notification.setUserId(event.getUserId());
        notification.setMessage(event.getMessage());
        notificationManager.addNotification(notification);
            // Replace System.out.println with logger statements
        LOGGER.info("Notification Event Received:");
        LOGGER.info("User ID: {}", event.getUserId());
        LOGGER.info("Message: {}", event.getMessage());
        LOGGER.info("Notification Type: {}", event.getNotificationType());

        // Simulate notification sending
        switch (event.getNotificationType()) {
            case "EMAIL":
                sendEmailNotification(event);
                break;
            case "SMS":
                sendSMSNotification(event);
                break;
            case "IN_APP":
                sendInAppNotification(event);
                break;
                //add default case to handle other notification types
            default:
                System.out.println("Invalid notification type: " + event.getNotificationType());
        }

    }

    private void sendEmailNotification(NotificationEvent event) {
        User user = service.findUserById(event.getUserId());
        String subject = "Notification for " + event.getUserId() + " \"" + user.getFirstName() + "\"";
        new Thread(() -> {

            try {
                emailService.sendEmail(user.getEmail(), subject, event.getMessage());
            }
            catch (Exception e) {
                System.err.println("Couldn't send the email: " + e);
            }
        }).start();
    }

    private void sendSMSNotification(NotificationEvent event) {
        User user = service.findUserById(event.getUserId());
        String lessonName = event.getMessage();
        OtpRequest otpRequest = new OtpRequest(
                "maya",
                "+201014367954",
                lessonName
        );
          //Convert the lambda to use expression notation by removing the curly braces and semicolon.
        new Thread(() -> smsService.sendSMS(otpRequest, Optional.of(user))).start();
        System.out.println("Sending SMS to student " + event.getUserId() + " for attendance otp of lesson : " + event.getMessage());
    }

    private void sendInAppNotification(NotificationEvent event) {
        System.out.println("Sending IN-APP notification to student " + event.getUserId() + ": " + event.getMessage());
    }
}
