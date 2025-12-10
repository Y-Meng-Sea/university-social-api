package com.example.universitysocial.controller;

import com.example.universitysocial.dto.NotificationResponse;
import com.example.universitysocial.dto.response.ApiResponse;
import com.example.universitysocial.service.NotificationService;
import com.example.universitysocial.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification", description = "Notification management API")
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @Operation(summary = "Get all notifications")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getNotifications() {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        List<NotificationResponse> responses = notificationService.getNotifications(userEmail);
        return ResponseEntity.ok(ApiResponse.<List<NotificationResponse>>builder()
                .message("Notifications retrieved successfully")
                .payload(responses)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @GetMapping("/unread")
    @Operation(summary = "Get unread notifications")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getUnreadNotifications() {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        List<NotificationResponse> responses = notificationService.getUnreadNotifications(userEmail);
        return ResponseEntity.ok(ApiResponse.<List<NotificationResponse>>builder()
                .message("Unread notifications retrieved successfully")
                .payload(responses)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @GetMapping("/unread/count")
    @Operation(summary = "Get unread notifications count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount() {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        long count = notificationService.getUnreadCount(userEmail);
        return ResponseEntity.ok(ApiResponse.<Long>builder()
                .message("Unread count retrieved successfully")
                .payload(count)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Mark notification as read")
    public ResponseEntity<ApiResponse<String>> markAsRead(@PathVariable Long id) {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        notificationService.markAsRead(id, userEmail);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .message("Notification marked as read")
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @PutMapping("/read-all")
    @Operation(summary = "Mark all notifications as read")
    public ResponseEntity<ApiResponse<String>> markAllAsRead() {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        notificationService.markAllAsRead(userEmail);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .message("All notifications marked as read")
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build());
    }
}
