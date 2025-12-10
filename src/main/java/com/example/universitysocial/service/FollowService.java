package com.example.universitysocial.service;

import com.example.universitysocial.dto.UserResponse;

import java.util.List;

public interface FollowService {
    void followUser(Long userId, String userEmail);
    void unfollowUser(Long userId, String userEmail);
    List<UserResponse> getFollowers(Long userId, String userEmail);
    List<UserResponse> getFollowing(Long userId, String userEmail);
    boolean isFollowing(Long userId, String userEmail);
}
