package com.example.universitysocial.dto;

import com.example.universitysocial.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private Long id;
    private Long senderId;
    private String senderUsername;
    private String senderNickName;
    private String senderProfileImage;
    private Notification.NotificationType type;
    private Long postId;
    private Long scholarshipId;
    private String title;
    private String message;
    private Boolean isRead;
    private LocalDateTime createdAt;
}
