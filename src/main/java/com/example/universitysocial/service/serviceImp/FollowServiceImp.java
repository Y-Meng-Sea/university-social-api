package com.example.universitysocial.service.serviceImp;

import com.example.universitysocial.dto.UserResponse;
import com.example.universitysocial.entity.Follow;
import com.example.universitysocial.entity.User;
import com.example.universitysocial.exception.ResourceNotFoundException;
import com.example.universitysocial.repository.FollowRepository;
import com.example.universitysocial.repository.UserRepository;
import com.example.universitysocial.service.FollowService;
import com.example.universitysocial.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FollowServiceImp implements FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public void followUser(Long userId, String userEmail) {
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        User userToFollow = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (currentUser.getId().equals(userId)) {
            throw new RuntimeException("You cannot follow yourself");
        }

        if (followRepository.existsByFollowerAndFollowing(currentUser, userToFollow)) {
            throw new RuntimeException("Already following this user");
        }

        Follow follow = Follow.builder()
                .follower(currentUser)
                .following(userToFollow)
                .build();

        followRepository.save(follow);

        // Create notification
        notificationService.createNotification(
                currentUser.getId(),
                userToFollow.getId(),
                com.example.universitysocial.entity.Notification.NotificationType.FOLLOW,
                null,
                null,
                currentUser.getNickName() != null ? currentUser.getNickName() : currentUser.getUsername() + " started following you",
                currentUser.getNickName() != null ? currentUser.getNickName() : currentUser.getUsername() + " started following you"
        );
    }

    @Override
    @Transactional
    public void unfollowUser(Long userId, String userEmail) {
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        User userToUnfollow = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Follow follow = followRepository.findByFollowerAndFollowing(currentUser, userToUnfollow)
                .orElseThrow(() -> new ResourceNotFoundException("Follow relationship not found"));

        followRepository.delete(follow);
    }

    @Override
    public List<UserResponse> getFollowers(Long userId, String userEmail) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Follow> follows = followRepository.findByFollowing(user);
        return follows.stream()
                .map(follow -> mapToUserResponse(follow.getFollower(), currentUser))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponse> getFollowing(Long userId, String userEmail) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Follow> follows = followRepository.findByFollower(user);
        return follows.stream()
                .map(follow -> mapToUserResponse(follow.getFollowing(), currentUser))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isFollowing(Long userId, String userEmail) {
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        return followRepository.existsByFollowerAndFollowing(currentUser, user);
    }

    private UserResponse mapToUserResponse(User user, User currentUser) {
        long followersCount = followRepository.countByFollowing(user);
        long followingCount = followRepository.countByFollower(user);
        boolean isFollowing = followRepository.existsByFollowerAndFollowing(currentUser, user);

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickName(user.getNickName())
                .email(user.getEmail())
                .profileImage(user.getProfileImage())
                .followersCount(followersCount)
                .followingCount(followingCount)
                .isFollowing(isFollowing)
                .createdAt(user.getCreatedAt())
                .build();
    }
}
