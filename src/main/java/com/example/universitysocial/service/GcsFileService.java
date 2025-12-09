package com.example.universitysocial.service;

import com.example.universitysocial.entity.FileMetaData;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class GcsFileService {
    @Value("${gcp.bucket}")
    private String bucketName;

    private final Storage storage;

    public GcsFileService(Storage storage) {
        this.storage = storage;
    }

    public FileMetaData uploadFile(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String contentType = file.getContentType() != null ? file.getContentType() : "application/octet-stream";

        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(contentType)
                .build();

        storage.create(blobInfo, file.getBytes());

        String fileUrl = String.format("https://storage.googleapis.com/%s/%s", bucketName, fileName);

        return FileMetaData.builder()
                .fileName(fileName)
                .fileType(contentType)
                .fileUrl(fileUrl)
                .fileSize(file.getSize())
                .build();
    }

    public FileDownload downloadFile(String fileName) throws IOException {
        BlobId blobId = BlobId.of(bucketName, fileName);
        Blob blob = storage.get(blobId);

        if (blob == null || !blob.exists()) {
            throw new IOException("File not found: " + fileName);
        }

        byte[] data = blob.getContent();
        MediaType mediaType = resolveMediaType(fileName, blob.getContentType());

        return new FileDownload(data, mediaType);
    }

    private MediaType resolveMediaType(String fileName, String contentType) {
        // Prefer stored content type when valid
        if (contentType != null && !contentType.isBlank()) {
            try {
                return MediaType.parseMediaType(contentType);
            } catch (Exception ignored) {
                // fall back to extension detection
            }
        }

        String lower = fileName.toLowerCase();
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
            return MediaType.IMAGE_JPEG;
        } else if (lower.endsWith(".png")) {
            return MediaType.IMAGE_PNG;
        } else if (lower.endsWith(".gif")) {
            return MediaType.IMAGE_GIF;
        } else if (lower.endsWith(".pdf")) {
            return MediaType.APPLICATION_PDF;
        }

        return MediaType.APPLICATION_OCTET_STREAM;
    }
}
