package com.example.universitysocial.service;

import com.example.universitysocial.dto.ScholarshipRequest;
import com.example.universitysocial.dto.ScholarshipResponse;

import java.util.List;

public interface ScholarshipService {
    ScholarshipResponse createScholarship(ScholarshipRequest request, String userEmail);
    ScholarshipResponse getScholarshipById(Long scholarshipId, String userEmail);
    List<ScholarshipResponse> getAllScholarships(String userEmail);
    List<ScholarshipResponse> getScholarshipsByUser(Long userId, String userEmail);
    ScholarshipResponse updateScholarship(Long scholarshipId, ScholarshipRequest request, String userEmail);
    void deleteScholarship(Long scholarshipId, String userEmail);
}
