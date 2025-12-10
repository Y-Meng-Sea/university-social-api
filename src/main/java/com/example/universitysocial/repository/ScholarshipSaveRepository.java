package com.example.universitysocial.repository;

import com.example.universitysocial.entity.Scholarship;
import com.example.universitysocial.entity.ScholarshipSave;
import com.example.universitysocial.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScholarshipSaveRepository extends JpaRepository<ScholarshipSave, Long> {
    Optional<ScholarshipSave> findByUserAndScholarship(User user, Scholarship scholarship);
    boolean existsByUserAndScholarship(User user, Scholarship scholarship);
    List<ScholarshipSave> findByUser(User user);
}
