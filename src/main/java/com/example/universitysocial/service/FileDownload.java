package com.example.universitysocial.service;

import org.springframework.http.MediaType;

/**
 * Simple DTO for file download responses.
 */
public record FileDownload(byte[] bytes, MediaType mediaType) {
}
