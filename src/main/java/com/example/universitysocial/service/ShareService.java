package com.example.universitysocial.service;

public interface ShareService {
    void sharePost(Long postId, String userEmail);
    void unsharePost(Long postId, String userEmail);
    boolean isPostShared(Long postId, String userEmail);
}
