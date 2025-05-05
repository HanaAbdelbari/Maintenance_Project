package com.lms.persistence;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    private String id = UUID.randomUUID().toString().replaceAll("-", "").substring(0,6);
    private String userId;
    private String message;
    private boolean isRead = false;
    private LocalDateTime timestamp = LocalDateTime.now();
}
