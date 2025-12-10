package com.example.universitysocial.service.serviceImp;

import com.example.universitysocial.entity.Like;
import com.example.universitysocial.entity.Post;
import com.example.universitysocial.entity.User;
import com.example.universitysocial.exception.ResourceNotFoundException;
import com.example.universitysocial.repository.LikeRepository;
import com.example.universitysocial.repository.PostRepository;
import com.example.universitysocial.repository.UserRepository;
import com.example.universitysocial.service.LikeService;
import com.example.universitysocial.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeServiceImp implements LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public void likePost(Long postId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        if (likeRepository.existsByUserAndPost(user, post)) {
            throw new RuntimeException("Post already liked");
        }

        Like like = Like.builder()
                .user(user)
                .post(post)
                .build();

        likeRepository.save(like);

        // Create notification if user is not liking their own post
        if (!post.getAuthor().getId().equals(user.getId())) {
            notificationService.createNotification(
                    user.getId(),
                    post.getAuthor().getId(),
                    com.example.universitysocial.entity.Notification.NotificationType.LIKE,
                    post.getId(),
                    null,
                    user.getNickName() != null ? user.getNickName() : user.getUsername() + " liked your post",
                    user.getNickName() != null ? user.getNickName() : user.getUsername() + " liked your post: " + post.getTitle()
            );
        }
    }

    @Override
    @Transactional
    public void unlikePost(Long postId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        Like like = likeRepository.findByUserAndPost(user, post)
                .orElseThrow(() -> new ResourceNotFoundException("Like not found"));

        likeRepository.delete(like);
    }

    @Override
    public boolean isPostLiked(Long postId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        return likeRepository.existsByUserAndPost(user, post);
    }
}
