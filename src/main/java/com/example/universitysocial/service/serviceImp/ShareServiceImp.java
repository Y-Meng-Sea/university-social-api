package com.example.universitysocial.service.serviceImp;

import com.example.universitysocial.entity.Post;
import com.example.universitysocial.entity.Share;
import com.example.universitysocial.entity.User;
import com.example.universitysocial.exception.ResourceNotFoundException;
import com.example.universitysocial.repository.PostRepository;
import com.example.universitysocial.repository.ShareRepository;
import com.example.universitysocial.repository.UserRepository;
import com.example.universitysocial.service.NotificationService;
import com.example.universitysocial.service.ShareService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShareServiceImp implements ShareService {

    private final ShareRepository shareRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public void sharePost(Long postId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        if (shareRepository.existsByUserAndPost(user, post)) {
            throw new RuntimeException("Post already shared");
        }

        Share share = Share.builder()
                .user(user)
                .post(post)
                .build();

        shareRepository.save(share);

        // Create notification if user is not sharing their own post
        if (!post.getAuthor().getId().equals(user.getId())) {
            notificationService.createNotification(
                    user.getId(),
                    post.getAuthor().getId(),
                    com.example.universitysocial.entity.Notification.NotificationType.SHARE,
                    post.getId(),
                    null,
                    user.getNickName() != null ? user.getNickName() : user.getUsername() + " shared your post",
                    user.getNickName() != null ? user.getNickName() : user.getUsername() + " shared your post: " + post.getTitle()
            );
        }
    }

    @Override
    @Transactional
    public void unsharePost(Long postId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        Share share = shareRepository.findByUserAndPost(user, post)
                .orElseThrow(() -> new ResourceNotFoundException("Share not found"));

        shareRepository.delete(share);
    }

    @Override
    public boolean isPostShared(Long postId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        return shareRepository.existsByUserAndPost(user, post);
    }
}
