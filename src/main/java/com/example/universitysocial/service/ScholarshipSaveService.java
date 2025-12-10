package com.example.universitysocial.service;

import com.example.universitysocial.dto.ScholarshipResponse;

import java.util.List;

public interface ScholarshipSaveService {
    void saveScholarship(Long scholarshipId, String userEmail);
    void unsaveScholarship(Long scholarshipId, String userEmail);
    List<ScholarshipResponse> getSavedScholarships(String userEmail);
    boolean isScholarshipSaved(Long scholarshipId, String userEmail);
}
