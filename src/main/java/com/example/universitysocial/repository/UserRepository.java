package com.example.universitysocial.repository;

import com.example.universitysocial.entiry.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
