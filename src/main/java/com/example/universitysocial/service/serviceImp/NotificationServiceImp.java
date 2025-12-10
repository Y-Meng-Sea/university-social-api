package com.example.universitysocial.service.serviceImp;

import com.example.universitysocial.dto.NotificationResponse;
import com.example.universitysocial.entity.Notification;
import com.example.universitysocial.entity.Post;
import com.example.universitysocial.entity.Scholarship;
import com.example.universitysocial.entity.User;
import com.example.universitysocial.exception.ResourceNotFoundException;
import com.example.universitysocial.repository.NotificationRepository;
import com.example.universitysocial.repository.PostRepository;
import com.example.universitysocial.repository.ScholarshipRepository;
import com.example.universitysocial.repository.UserRepository;
import com.example.universitysocial.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImp implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final ScholarshipRepository scholarshipRepository;

    @Override
    @Transactional
    public void createNotification(Long senderId, Long receiverId, 
                                  Notification.NotificationType type,
                                  Long postId, Long scholarshipId, 
                                  String title, String message) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found"));

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new ResourceNotFoundException("Receiver not found"));

        Notification.NotificationBuilder builder = Notification.builder()
                .sender(sender)
                .receiver(receiver)
                .type(type)
                .title(title)
                .message(message);

        if (postId != null) {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
            builder.post(post);
        }

        if (scholarshipId != null) {
            Scholarship scholarship = scholarshipRepository.findById(scholarshipId)
                    .orElseThrow(() -> new ResourceNotFoundException("Scholarship not found"));
            builder.scholarship(scholarship);
        }

        notificationRepository.save(builder.build());
    }

    @Override
    public List<NotificationResponse> getNotifications(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Notification> notifications = notificationRepository.findByReceiverOrderByCreatedAtDesc(user);
        return notifications.stream()
                .map(this::mapToNotificationResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<NotificationResponse> getUnreadNotifications(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Notification> notifications = notificationRepository.findByReceiverAndIsReadOrderByCreatedAtDesc(user, false);
        return notifications.stream()
                .map(this::mapToNotificationResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        if (!notification.getReceiver().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to mark this notification as read");
        }

        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void markAllAsRead(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Notification> notifications = notificationRepository.findByReceiverAndIsReadOrderByCreatedAtDesc(user, false);
        notifications.forEach(notification -> notification.setIsRead(true));
        notificationRepository.saveAll(notifications);
    }

    @Override
    public long getUnreadCount(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return notificationRepository.countByReceiverAndIsRead(user, false);
    }

    private NotificationResponse mapToNotificationResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .senderId(notification.getSender().getId())
                .senderUsername(notification.getSender().getUsername())
                .senderNickName(notification.getSender().getNickName())
                .senderProfileImage(notification.getSender().getProfileImage())
                .type(notification.getType())
                .postId(notification.getPost() != null ? notification.getPost().getId() : null)
                .scholarshipId(notification.getScholarship() != null ? notification.getScholarship().getId() : null)
                .title(notification.getTitle())
                .message(notification.getMessage())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
