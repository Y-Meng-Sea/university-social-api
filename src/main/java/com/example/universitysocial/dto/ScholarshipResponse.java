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
public class ScholarshipResponse {
    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private String link;
    private Long authorId;
    private String authorUsername;
    private String authorNickName;
    private String authorProfileImage;
    private Long savesCount;
    private Boolean isSaved;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
