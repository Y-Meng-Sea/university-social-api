package com.example.universitysocial.controller;

import com.example.universitysocial.dto.ScholarshipResponse;
import com.example.universitysocial.dto.response.ApiResponse;
import com.example.universitysocial.service.ScholarshipSaveService;
import com.example.universitysocial.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/saves")
@RequiredArgsConstructor
@Tag(name = "Scholarship Save", description = "Scholarship save management API")
@SecurityRequirement(name = "bearerAuth")
public class ScholarshipSaveController {

    private final ScholarshipSaveService scholarshipSaveService;

    @PostMapping("/scholarships/{scholarshipId}")
    @Operation(summary = "Save a scholarship")
    public ResponseEntity<ApiResponse<String>> saveScholarship(@PathVariable Long scholarshipId) {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        scholarshipSaveService.saveScholarship(scholarshipId, userEmail);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .message("Scholarship saved successfully")
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @DeleteMapping("/scholarships/{scholarshipId}")
    @Operation(summary = "Unsave a scholarship")
    public ResponseEntity<ApiResponse<String>> unsaveScholarship(@PathVariable Long scholarshipId) {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        scholarshipSaveService.unsaveScholarship(scholarshipId, userEmail);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .message("Scholarship unsaved successfully")
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @GetMapping("/scholarships")
    @Operation(summary = "Get all saved scholarships")
    public ResponseEntity<ApiResponse<List<ScholarshipResponse>>> getSavedScholarships() {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        List<ScholarshipResponse> responses = scholarshipSaveService.getSavedScholarships(userEmail);
        return ResponseEntity.ok(ApiResponse.<List<ScholarshipResponse>>builder()
                .message("Saved scholarships retrieved successfully")
                .payload(responses)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @GetMapping("/scholarships/{scholarshipId}/check")
    @Operation(summary = "Check if scholarship is saved")
    public ResponseEntity<ApiResponse<Boolean>> isScholarshipSaved(@PathVariable Long scholarshipId) {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        boolean isSaved = scholarshipSaveService.isScholarshipSaved(scholarshipId, userEmail);
        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .message("Save status retrieved successfully")
                .payload(isSaved)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build());
    }
}
