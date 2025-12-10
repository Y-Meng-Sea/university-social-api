package com.example.universitysocial.service;

import com.example.universitysocial.dto.PostRequest;
import com.example.universitysocial.dto.PostResponse;

import java.util.List;

public interface PostService {
    PostResponse createPost(PostRequest request, String userEmail);
    PostResponse getPostById(Long postId, String userEmail);
    List<PostResponse> getAllPosts(String userEmail);
    List<PostResponse> getPostsByUser(Long userId, String userEmail);
    PostResponse updatePost(Long postId, PostRequest request, String userEmail);
    void deletePost(Long postId, String userEmail);
}
