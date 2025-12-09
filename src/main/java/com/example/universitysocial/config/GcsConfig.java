package com.example.universitysocial.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.FileInputStream;
import java.io.InputStream;

@Configuration
public class GcsConfig {

    // Path to your service account JSON (can be classpath: or filesystem path)
    @Value("${gcp.credentials.location:}")
    private String credentialsPath;

    // Your Google Cloud project ID
    @Value("${gcp.project-id:}")
    private String projectId;

    @Bean
    public Storage storage() throws Exception {

        // Builder for GCS client
        StorageOptions.Builder builder = StorageOptions.newBuilder();

        // Set project ID if provided
        if (projectId != null && !projectId.isBlank()) {
            builder.setProjectId(projectId);
        }

        // Try to load credentials (classpath, file path, or default)
        GoogleCredentials credentials = loadCredentials();
        if (credentials != null) {
            builder.setCredentials(credentials);
        }

        // Create the GCS storage client
        return builder.build().getService();
    }

    /**
     * Loads Google Cloud credentials in a simple way:
     * 1. If no path is provided → use default credentials
     * 2. If path starts with "classpath:" → load from resources folder
     * 3. Otherwise → treat it as a normal file path
     */
    private GoogleCredentials loadCredentials() throws Exception {

        // If no credential path → fallback to default credentials (IDE login / GCP environment)
        if (credentialsPath == null || credentialsPath.isBlank()) {
            System.out.println("INFO: Using default GCP credentials");
            return GoogleCredentials.getApplicationDefault();
        }

        InputStream stream;

        // Load credentials from classpath
        if (credentialsPath.startsWith("classpath:")) {
            String resourcePath = credentialsPath.substring("classpath:".length());
            Resource resource = new ClassPathResource(resourcePath);

            if (!resource.exists()) {
                System.err.println("WARNING: Credentials not found in classpath. Using default credentials.");
                return GoogleCredentials.getApplicationDefault();
            }

            stream = resource.getInputStream();

        } else {
            // Load credentials from file system path
            stream = new FileInputStream(credentialsPath);
        }

        // Convert file/stream → GoogleCredentials object
        return GoogleCredentials.fromStream(stream);
    }
}
