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
public class PostResponse {
    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private Long authorId;
    private String authorUsername;
    private String authorNickName;
    private String authorProfileImage;
    private Long likesCount;
    private Long sharesCount;
    private Long savesCount;
    private Boolean isLiked;
    private Boolean isShared;
    private Boolean isSaved;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
