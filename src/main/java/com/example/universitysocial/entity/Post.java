package com.example.universitysocial.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.File;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    private File image;

    private Integer likes;

    private Integer shares;
}
