package com.example.universitysocial.controller;

import com.example.universitysocial.dto.response.ApiResponse;
import com.example.universitysocial.entity.FileMetaData;
import com.example.universitysocial.service.GcsFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/files")
@SecurityRequirement(name = "bearerAuth") // this will require auth for using
@AllArgsConstructor
public class FileController {
    private final GcsFileService fileService;

    @Operation(summary = "Upload a file")
    @PostMapping(value = "/upload-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<FileMetaData>> uploadFile(
            @RequestPart(value = "file", required = true) MultipartFile file) throws IOException {
        ApiResponse<FileMetaData> response = ApiResponse.<FileMetaData>builder()
                .status(HttpStatus.OK)
                .message("File uploaded successfully! metadata of the file upload is return")
                .payload(fileService.uploadFile(file))
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Preview a file")
    @GetMapping("/preview-file/{file-name}")
    public ResponseEntity<byte[]> getFileByName(@PathVariable("file-name") String fileName) throws IOException {
        var download = fileService.downloadFile(fileName);
        return ResponseEntity.ok()
                .contentType(download.mediaType())
                .body(download.bytes());
    }
}
