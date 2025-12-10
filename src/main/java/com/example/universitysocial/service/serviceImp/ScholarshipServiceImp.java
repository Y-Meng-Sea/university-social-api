package com.example.universitysocial.service.serviceImp;

import com.example.universitysocial.dto.ScholarshipRequest;
import com.example.universitysocial.dto.ScholarshipResponse;
import com.example.universitysocial.entity.Scholarship;
import com.example.universitysocial.entity.User;
import com.example.universitysocial.exception.ResourceNotFoundException;
import com.example.universitysocial.repository.ScholarshipRepository;
import com.example.universitysocial.repository.ScholarshipSaveRepository;
import com.example.universitysocial.repository.UserRepository;
import com.example.universitysocial.service.ScholarshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScholarshipServiceImp implements ScholarshipService {

    private final ScholarshipRepository scholarshipRepository;
    private final UserRepository userRepository;
    private final ScholarshipSaveRepository scholarshipSaveRepository;

    @Override
    @Transactional
    public ScholarshipResponse createScholarship(ScholarshipRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Scholarship scholarship = Scholarship.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .link(request.getLink())
                .author(user)
                .build();

        scholarship = scholarshipRepository.save(scholarship);
        return mapToScholarshipResponse(scholarship, user);
    }

    @Override
    public ScholarshipResponse getScholarshipById(Long scholarshipId, String userEmail) {
        Scholarship scholarship = scholarshipRepository.findById(scholarshipId)
                .orElseThrow(() -> new ResourceNotFoundException("Scholarship not found with id: " + scholarshipId));

        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return mapToScholarshipResponse(scholarship, currentUser);
    }

    @Override
    public List<ScholarshipResponse> getAllScholarships(String userEmail) {
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Scholarship> scholarships = scholarshipRepository.findAll();
        return scholarships.stream()
                .map(scholarship -> mapToScholarshipResponse(scholarship, currentUser))
                .collect(Collectors.toList());
    }

    @Override
    public List<ScholarshipResponse> getScholarshipsByUser(Long userId, String userEmail) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Scholarship> scholarships = scholarshipRepository.findByAuthor(user);

        return scholarships.stream()
                .map(scholarship -> mapToScholarshipResponse(scholarship, currentUser))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ScholarshipResponse updateScholarship(Long scholarshipId, ScholarshipRequest request, String userEmail) {
        Scholarship scholarship = scholarshipRepository.findById(scholarshipId)
                .orElseThrow(() -> new ResourceNotFoundException("Scholarship not found with id: " + scholarshipId));

        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if user is the author
        if (!scholarship.getAuthor().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not authorized to update this scholarship");
        }

        scholarship.setTitle(request.getTitle());
        scholarship.setDescription(request.getDescription());
        if (request.getImageUrl() != null) {
            scholarship.setImageUrl(request.getImageUrl());
        }
        if (request.getLink() != null) {
            scholarship.setLink(request.getLink());
        }

        scholarship = scholarshipRepository.save(scholarship);
        return mapToScholarshipResponse(scholarship, currentUser);
    }

    @Override
    @Transactional
    public void deleteScholarship(Long scholarshipId, String userEmail) {
        Scholarship scholarship = scholarshipRepository.findById(scholarshipId)
                .orElseThrow(() -> new ResourceNotFoundException("Scholarship not found with id: " + scholarshipId));

        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if user is the author
        if (!scholarship.getAuthor().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not authorized to delete this scholarship");
        }

        scholarshipRepository.delete(scholarship);
    }

    private ScholarshipResponse mapToScholarshipResponse(Scholarship scholarship, User currentUser) {
        boolean isSaved = scholarshipSaveRepository.existsByUserAndScholarship(currentUser, scholarship);

        return ScholarshipResponse.builder()
                .id(scholarship.getId())
                .title(scholarship.getTitle())
                .description(scholarship.getDescription())
                .imageUrl(scholarship.getImageUrl())
                .link(scholarship.getLink())
                .authorId(scholarship.getAuthor().getId())
                .authorUsername(scholarship.getAuthor().getUsername())
                .authorNickName(scholarship.getAuthor().getNickName())
                .authorProfileImage(scholarship.getAuthor().getProfileImage())
                .savesCount((long) scholarship.getSaves().size())
                .isSaved(isSaved)
                .createdAt(scholarship.getCreatedAt())
                .updatedAt(scholarship.getUpdatedAt())
                .build();
    }
}
