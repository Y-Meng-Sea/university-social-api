package com.example.universitysocial.controller;

import com.example.universitysocial.dto.PostResponse;
import com.example.universitysocial.dto.response.ApiResponse;
import com.example.universitysocial.service.SaveService;
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
@RequestMapping("/api/v1/saves")
@RequiredArgsConstructor
@Tag(name = "Save", description = "Post save management API")
@SecurityRequirement(name = "bearerAuth")
public class SaveController {

    private final SaveService saveService;

    @PostMapping("/posts/{postId}")
    @Operation(summary = "Save a post")
    public ResponseEntity<ApiResponse<String>> savePost(@PathVariable Long postId) {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        saveService.savePost(postId, userEmail);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .message("Post saved successfully")
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @DeleteMapping("/posts/{postId}")
    @Operation(summary = "Unsave a post")
    public ResponseEntity<ApiResponse<String>> unsavePost(@PathVariable Long postId) {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        saveService.unsavePost(postId, userEmail);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .message("Post unsaved successfully")
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @GetMapping("/posts")
    @Operation(summary = "Get all saved posts")
    public ResponseEntity<ApiResponse<List<PostResponse>>> getSavedPosts() {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        List<PostResponse> responses = saveService.getSavedPosts(userEmail);
        return ResponseEntity.ok(ApiResponse.<List<PostResponse>>builder()
                .message("Saved posts retrieved successfully")
                .payload(responses)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @GetMapping("/posts/{postId}/check")
    @Operation(summary = "Check if post is saved")
    public ResponseEntity<ApiResponse<Boolean>> isPostSaved(@PathVariable Long postId) {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        boolean isSaved = saveService.isPostSaved(postId, userEmail);
        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .message("Save status retrieved successfully")
                .payload(isSaved)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build());
    }
}
