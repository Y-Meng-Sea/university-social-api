package com.example.universitysocial.service;

public interface LikeService {
    void likePost(Long postId, String userEmail);
    void unlikePost(Long postId, String userEmail);
    boolean isPostLiked(Long postId, String userEmail);
}
