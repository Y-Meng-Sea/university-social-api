package com.example.universitysocial.service;

import com.example.universitysocial.dto.PostResponse;

import java.util.List;

public interface SaveService {
    void savePost(Long postId, String userEmail);
    void unsavePost(Long postId, String userEmail);
    List<PostResponse> getSavedPosts(String userEmail);
    boolean isPostSaved(Long postId, String userEmail);
}
