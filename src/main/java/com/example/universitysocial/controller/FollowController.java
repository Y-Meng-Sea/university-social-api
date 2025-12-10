package com.example.universitysocial.controller;

import com.example.universitysocial.dto.UserResponse;
import com.example.universitysocial.dto.response.ApiResponse;
import com.example.universitysocial.service.FollowService;
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
@RequestMapping("/api/v1/users/{userId}/follow")
@RequiredArgsConstructor
@Tag(name = "Follow", description = "User follow management API")
@SecurityRequirement(name = "bearerAuth")
public class FollowController {

    private final FollowService followService;

    @PostMapping
    @Operation(summary = "Follow a user")
    public ResponseEntity<ApiResponse<String>> followUser(@PathVariable Long userId) {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        followService.followUser(userId, userEmail);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .message("User followed successfully")
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @DeleteMapping
    @Operation(summary = "Unfollow a user")
    public ResponseEntity<ApiResponse<String>> unfollowUser(@PathVariable Long userId) {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        followService.unfollowUser(userId, userEmail);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .message("User unfollowed successfully")
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @GetMapping("/followers")
    @Operation(summary = "Get followers of a user")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getFollowers(@PathVariable Long userId) {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        List<UserResponse> responses = followService.getFollowers(userId, userEmail);
        return ResponseEntity.ok(ApiResponse.<List<UserResponse>>builder()
                .message("Followers retrieved successfully")
                .payload(responses)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @GetMapping("/following")
    @Operation(summary = "Get users that a user is following")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getFollowing(@PathVariable Long userId) {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        List<UserResponse> responses = followService.getFollowing(userId, userEmail);
        return ResponseEntity.ok(ApiResponse.<List<UserResponse>>builder()
                .message("Following list retrieved successfully")
                .payload(responses)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @GetMapping("/check")
    @Operation(summary = "Check if following a user")
    public ResponseEntity<ApiResponse<Boolean>> isFollowing(@PathVariable Long userId) {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        boolean isFollowing = followService.isFollowing(userId, userEmail);
        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .message("Follow status retrieved successfully")
                .payload(isFollowing)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build());
    }
}
