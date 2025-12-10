package com.example.universitysocial.controller;

import com.example.universitysocial.dto.response.ApiResponse;
import com.example.universitysocial.service.ShareService;
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
@RequestMapping("/api/v1/posts/{postId}/shares")
@RequiredArgsConstructor
@Tag(name = "Share", description = "Post share management API")
@SecurityRequirement(name = "bearerAuth")
public class ShareController {

    private final ShareService shareService;

    @PostMapping
    @Operation(summary = "Share a post")
    public ResponseEntity<ApiResponse<String>> sharePost(@PathVariable Long postId) {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        shareService.sharePost(postId, userEmail);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .message("Post shared successfully")
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @DeleteMapping
    @Operation(summary = "Unshare a post")
    public ResponseEntity<ApiResponse<String>> unsharePost(@PathVariable Long postId) {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        shareService.unsharePost(postId, userEmail);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .message("Post unshared successfully")
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @GetMapping
    @Operation(summary = "Check if post is shared")
    public ResponseEntity<ApiResponse<Boolean>> isPostShared(@PathVariable Long postId) {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        boolean isShared = shareService.isPostShared(postId, userEmail);
        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .message("Share status retrieved successfully")
                .payload(isShared)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build());
    }
}
