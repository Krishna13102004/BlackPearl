package com.blackpearl.dto;

import com.blackpearl.model.Notification.Type;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private Long id;
    private Long userId;
    private String userEmail;
    private String title;
    private String message;
    private Type type;
    private boolean read;
    private LocalDateTime createdAt;
}
