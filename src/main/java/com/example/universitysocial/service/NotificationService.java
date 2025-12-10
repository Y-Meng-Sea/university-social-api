package com.example.universitysocial.service;

import com.example.universitysocial.dto.NotificationResponse;

import java.util.List;

public interface NotificationService {
    void createNotification(Long senderId, Long receiverId, 
                           com.example.universitysocial.entity.Notification.NotificationType type,
                           Long postId, Long scholarshipId, String title, String message);
    List<NotificationResponse> getNotifications(String userEmail);
    List<NotificationResponse> getUnreadNotifications(String userEmail);
    void markAsRead(Long notificationId, String userEmail);
    void markAllAsRead(String userEmail);
    long getUnreadCount(String userEmail);
}
