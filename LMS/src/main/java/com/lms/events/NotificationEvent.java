package com.lms.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class NotificationEvent extends ApplicationEvent {
    private final String userId;
    private final String message;
    private final String notificationType;

    public NotificationEvent(Object source, String userId, String message, String notificationType) {
        super(source);
        this.userId = userId;
        this.message = message;
        this.notificationType = notificationType;
    }
}
