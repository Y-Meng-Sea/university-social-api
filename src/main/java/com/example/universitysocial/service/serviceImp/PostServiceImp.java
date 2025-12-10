package com.example.universitysocial.service.serviceImp;

import com.example.universitysocial.dto.PostRequest;
import com.example.universitysocial.dto.PostResponse;
import com.example.universitysocial.entity.Post;
import com.example.universitysocial.entity.User;
import com.example.universitysocial.exception.ResourceNotFoundException;
import com.example.universitysocial.repository.LikeRepository;
import com.example.universitysocial.repository.PostRepository;
import com.example.universitysocial.repository.SaveRepository;
import com.example.universitysocial.repository.ShareRepository;
import com.example.universitysocial.repository.UserRepository;
import com.example.universitysocial.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImp implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final ShareRepository shareRepository;
    private final SaveRepository saveRepository;

    @Override
    @Transactional
    public PostResponse createPost(PostRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Post post = Post.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .author(user)
                .build();

        post = postRepository.save(post);
        return mapToPostResponse(post, user);
    }

    @Override
    public PostResponse getPostById(Long postId, String userEmail) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return mapToPostResponse(post, currentUser);
    }

    @Override
    public List<PostResponse> getAllPosts(String userEmail) {
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Post> posts = postRepository.findAll();
        return posts.stream()
                .map(post -> mapToPostResponse(post, currentUser))
                .collect(Collectors.toList());
    }

    @Override
    public List<PostResponse> getPostsByUser(Long userId, String userEmail) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Post> posts = postRepository.findByAuthor(user);

        return posts.stream()
                .map(post -> mapToPostResponse(post, currentUser))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PostResponse updatePost(Long postId, PostRequest request, String userEmail) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if user is the author
        if (!post.getAuthor().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not authorized to update this post");
        }

        post.setTitle(request.getTitle());
        post.setDescription(request.getDescription());
        if (request.getImageUrl() != null) {
            post.setImageUrl(request.getImageUrl());
        }

        post = postRepository.save(post);
        return mapToPostResponse(post, currentUser);
    }

    @Override
    @Transactional
    public void deletePost(Long postId, String userEmail) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if user is the author
        if (!post.getAuthor().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not authorized to delete this post");
        }

        postRepository.delete(post);
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
