package com.example.universitysocial.controller;

import com.example.universitysocial.dto.ScholarshipRequest;
import com.example.universitysocial.dto.ScholarshipResponse;
import com.example.universitysocial.dto.response.ApiResponse;
import com.example.universitysocial.service.ScholarshipService;
import com.example.universitysocial.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/scholarships")
@RequiredArgsConstructor
@Tag(name = "Scholarship", description = "Scholarship management API")
@SecurityRequirement(name = "bearerAuth")
public class ScholarshipController {

    private final ScholarshipService scholarshipService;

    @PostMapping
    @Operation(summary = "Create a new scholarship")
    public ResponseEntity<ApiResponse<ScholarshipResponse>> createScholarship(@Valid @RequestBody ScholarshipRequest request) {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        ScholarshipResponse response = scholarshipService.createScholarship(request, userEmail);
        return ResponseEntity.ok(ApiResponse.<ScholarshipResponse>builder()
                .message("Scholarship created successfully")
                .payload(response)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get scholarship by ID")
    public ResponseEntity<ApiResponse<ScholarshipResponse>> getScholarshipById(@PathVariable Long id) {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        ScholarshipResponse response = scholarshipService.getScholarshipById(id, userEmail);
        return ResponseEntity.ok(ApiResponse.<ScholarshipResponse>builder()
                .message("Scholarship retrieved successfully")
                .payload(response)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @GetMapping
    @Operation(summary = "Get all scholarships")
    public ResponseEntity<ApiResponse<List<ScholarshipResponse>>> getAllScholarships() {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        List<ScholarshipResponse> responses = scholarshipService.getAllScholarships(userEmail);
        return ResponseEntity.ok(ApiResponse.<List<ScholarshipResponse>>builder()
                .message("Scholarships retrieved successfully")
                .payload(responses)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get scholarships by user ID")
    public ResponseEntity<ApiResponse<List<ScholarshipResponse>>> getScholarshipsByUser(@PathVariable Long userId) {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        List<ScholarshipResponse> responses = scholarshipService.getScholarshipsByUser(userId, userEmail);
        return ResponseEntity.ok(ApiResponse.<List<ScholarshipResponse>>builder()
                .message("Scholarships retrieved successfully")
                .payload(responses)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update scholarship")
    public ResponseEntity<ApiResponse<ScholarshipResponse>> updateScholarship(
            @PathVariable Long id,
            @Valid @RequestBody ScholarshipRequest request) {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        ScholarshipResponse response = scholarshipService.updateScholarship(id, request, userEmail);
        return ResponseEntity.ok(ApiResponse.<ScholarshipResponse>builder()
                .message("Scholarship updated successfully")
                .payload(response)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete scholarship")
    public ResponseEntity<ApiResponse<String>> deleteScholarship(@PathVariable Long id) {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        scholarshipService.deleteScholarship(id, userEmail);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .message("Scholarship deleted successfully")
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build());
    }
}
