package com.example.universitysocial.entiry;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table (name = "mengsea")

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String email;
    private String password;
}
