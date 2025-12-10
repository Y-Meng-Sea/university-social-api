package com.example.universitysocial.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String nickName;
    private String email;
    private String profileImage;
    private Long followersCount;
    private Long followingCount;
    private Boolean isFollowing;
    private LocalDateTime createdAt;
}
