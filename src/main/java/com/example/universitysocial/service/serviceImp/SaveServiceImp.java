package com.example.universitysocial.service.serviceImp;

import com.example.universitysocial.dto.PostResponse;
import com.example.universitysocial.entity.Post;
import com.example.universitysocial.entity.Save;
import com.example.universitysocial.entity.User;
import com.example.universitysocial.exception.ResourceNotFoundException;
import com.example.universitysocial.repository.LikeRepository;
import com.example.universitysocial.repository.PostRepository;
import com.example.universitysocial.repository.SaveRepository;
import com.example.universitysocial.repository.ShareRepository;
import com.example.universitysocial.repository.UserRepository;
import com.example.universitysocial.service.NotificationService;
import com.example.universitysocial.service.SaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SaveServiceImp implements SaveService {

    private final SaveRepository saveRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final ShareRepository shareRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public void savePost(Long postId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        if (saveRepository.existsByUserAndPost(user, post)) {
            throw new RuntimeException("Post already saved");
        }

        Save save = Save.builder()
                .user(user)
                .post(post)
                .build();

        saveRepository.save(save);

        // Create notification if user is not saving their own post
        if (!post.getAuthor().getId().equals(user.getId())) {
            notificationService.createNotification(
                    user.getId(),
                    post.getAuthor().getId(),
                    com.example.universitysocial.entity.Notification.NotificationType.POST_SAVE,
                    post.getId(),
                    null,
                    user.getNickName() != null ? user.getNickName() : user.getUsername() + " saved your post",
                    user.getNickName() != null ? user.getNickName() : user.getUsername() + " saved your post: " + post.getTitle()
            );
        }
    }

    @Override
    @Transactional
    public void unsavePost(Long postId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        Save save = saveRepository.findByUserAndPost(user, post)
                .orElseThrow(() -> new ResourceNotFoundException("Save not found"));

        saveRepository.delete(save);
    }

    @Override
    public List<PostResponse> getSavedPosts(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Save> saves = saveRepository.findByUser(user);
        return saves.stream()
                .map(save -> mapToPostResponse(save.getPost(), user))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isPostSaved(Long postId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        return saveRepository.existsByUserAndPost(user, post);
    }

    private PostResponse mapToPostResponse(Post post, User currentUser) {
        boolean isLiked = likeRepository.existsByUserAndPost(currentUser, post);
        boolean isShared = shareRepository.existsByUserAndPost(currentUser, post);
        boolean isSaved = saveRepository.existsByUserAndPost(currentUser, post);

        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .description(post.getDescription())
                .imageUrl(post.getImageUrl())
                .authorId(post.getAuthor().getId())
                .authorUsername(post.getAuthor().getUsername())
                .authorNickName(post.getAuthor().getNickName())
                .authorProfileImage(post.getAuthor().getProfileImage())
                .likesCount(likeRepository.countByPost(post))
                .sharesCount(shareRepository.countByPost(post))
                .savesCount((long) post.getSaves().size())
                .isLiked(isLiked)
                .isShared(isShared)
                .isSaved(isSaved)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
