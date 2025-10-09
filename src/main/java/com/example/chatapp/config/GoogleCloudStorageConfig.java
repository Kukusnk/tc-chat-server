package com.example.chatapp.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Configuration
public class GoogleCloudStorageConfig {

    //    @Value("${gcp.storage.credentials}")
//    private String credentialsPath;
//
//    @Bean
//    public Storage storage() throws IOException {
//        InputStream credentialsStream = getClass().getClassLoader().getResourceAsStream(credentialsPath);
//        return StorageOptions.newBuilder()
//                .setCredentials(ServiceAccountCredentials.fromStream(credentialsStream))
//                .build()
//                .getService();
//    }
    @Value("${gcp.storage.bucket}")
    private String bucketName;

//    @Bean
//    public Storage storage() {
//        return StorageOptions.getDefaultInstance().getService();
//    }

    @Value("${gcp.credentials.json}") // JSON из переменной окружения
    private String googleCredentials;

    @Value("${gcp.storage.project-id}")
    private String projectId;

    @Bean
    public Storage storage() throws IOException {
        if (googleCredentials == null || googleCredentials.isBlank()) {
            throw new IllegalStateException("GOOGLE_CREDENTIALS_JSON env variable is missing!");
        }

        GoogleCredentials credentials = GoogleCredentials
                .fromStream(new ByteArrayInputStream(googleCredentials.getBytes()))
                .createScoped("https://www.googleapis.com/auth/cloud-platform");

        return StorageOptions.newBuilder()
                .setProjectId(projectId)
                .setCredentials(credentials)
                .build()
                .getService();
    }

    @Bean
    public String bucketName() {
        return bucketName;
    }
}
