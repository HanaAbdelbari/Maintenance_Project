package com.lms.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CourseNotificationEvent extends ApplicationEvent {

    private final String courseId;
    private final String message;

    public CourseNotificationEvent(Object source, String courseId, String message) {
        super(source);
        this.courseId = courseId;
        this.message = message;
    }

}
