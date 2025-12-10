package com.example.universitysocial.controller;

import com.example.universitysocial.dto.response.ApiResponse;
import com.example.universitysocial.service.LikeService;
import com.example.universitysocial.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/posts/{postId}/likes")
@RequiredArgsConstructor
@Tag(name = "Like", description = "Post like management API")
@SecurityRequirement(name = "bearerAuth")
public class LikeController {

    private final LikeService likeService;

    @PostMapping
    @Operation(summary = "Like a post")
    public ResponseEntity<ApiResponse<String>> likePost(@PathVariable Long postId) {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        likeService.likePost(postId, userEmail);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .message("Post liked successfully")
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @DeleteMapping
    @Operation(summary = "Unlike a post")
    public ResponseEntity<ApiResponse<String>> unlikePost(@PathVariable Long postId) {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        likeService.unlikePost(postId, userEmail);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .message("Post unliked successfully")
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @GetMapping
    @Operation(summary = "Check if post is liked")
    public ResponseEntity<ApiResponse<Boolean>> isPostLiked(@PathVariable Long postId) {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        boolean isLiked = likeService.isPostLiked(postId, userEmail);
        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .message("Like status retrieved successfully")
                .payload(isLiked)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build());
    }
}
