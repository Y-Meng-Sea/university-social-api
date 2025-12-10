package com.example.universitysocial.controller;

import com.example.universitysocial.dto.PostRequest;
import com.example.universitysocial.dto.PostResponse;
import com.example.universitysocial.dto.response.ApiResponse;
import com.example.universitysocial.service.PostService;
import com.example.universitysocial.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@Tag(name = "Post", description = "Post management API")
@SecurityRequirement(name = "bearerAuth")
public class PostController {

    private final PostService postService;

    @PostMapping
    @Operation(summary = "Create a new post")
    public ResponseEntity<ApiResponse<PostResponse>> createPost(@Valid @RequestBody PostRequest request) {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        PostResponse response = postService.createPost(request, userEmail);
        return ResponseEntity.ok(ApiResponse.<PostResponse>builder()
                .message("Post created successfully")
                .payload(response)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get post by ID")
    public ResponseEntity<ApiResponse<PostResponse>> getPostById(@PathVariable Long id) {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        PostResponse response = postService.getPostById(id, userEmail);
        return ResponseEntity.ok(ApiResponse.<PostResponse>builder()
                .message("Post retrieved successfully")
                .payload(response)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @GetMapping
    @Operation(summary = "Get all posts")
    public ResponseEntity<ApiResponse<List<PostResponse>>> getAllPosts() {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        List<PostResponse> responses = postService.getAllPosts(userEmail);
        return ResponseEntity.ok(ApiResponse.<List<PostResponse>>builder()
                .message("Posts retrieved successfully")
                .payload(responses)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get posts by user ID")
    public ResponseEntity<ApiResponse<List<PostResponse>>> getPostsByUser(@PathVariable Long userId) {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        List<PostResponse> responses = postService.getPostsByUser(userId, userEmail);
        return ResponseEntity.ok(ApiResponse.<List<PostResponse>>builder()
                .message("Posts retrieved successfully")
                .payload(responses)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update post")
    public ResponseEntity<ApiResponse<PostResponse>> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody PostRequest request) {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        PostResponse response = postService.updatePost(id, request, userEmail);
        return ResponseEntity.ok(ApiResponse.<PostResponse>builder()
                .message("Post updated successfully")
                .payload(response)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete post")
    public ResponseEntity<ApiResponse<String>> deletePost(@PathVariable Long id) {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        postService.deletePost(id, userEmail);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .message("Post deleted successfully")
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build());
    }
}
