package com.example.universitysocial.repository;

import com.example.universitysocial.entity.Scholarship;
import com.example.universitysocial.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScholarshipRepository extends JpaRepository<Scholarship, Long> {
    List<Scholarship> findByAuthor(User author);
}
