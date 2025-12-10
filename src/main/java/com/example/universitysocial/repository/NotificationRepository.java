package com.example.universitysocial.repository;

import com.example.universitysocial.entity.Notification;
import com.example.universitysocial.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByReceiverOrderByCreatedAtDesc(User receiver);
    List<Notification> findByReceiverAndIsReadOrderByCreatedAtDesc(User receiver, Boolean isRead);
    long countByReceiverAndIsRead(User receiver, Boolean isRead);
}
