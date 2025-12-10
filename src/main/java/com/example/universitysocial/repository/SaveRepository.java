package com.example.universitysocial.repository;

import com.example.universitysocial.entity.Post;
import com.example.universitysocial.entity.Save;
import com.example.universitysocial.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SaveRepository extends JpaRepository<Save, Long> {
    Optional<Save> findByUserAndPost(User user, Post post);
    boolean existsByUserAndPost(User user, Post post);
    List<Save> findByUser(User user);
}
