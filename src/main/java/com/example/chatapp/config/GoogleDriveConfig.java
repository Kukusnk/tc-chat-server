package com.example.chatapp.config;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Configuration
@EnableConfigurationProperties(GoogleDriveProperties.class)
@RequiredArgsConstructor
public class GoogleDriveConfig {

    private final ResourceLoader resourceLoader;

    @Bean
    public Drive driveClient(GoogleDriveProperties properties) throws IOException, GeneralSecurityException {
        ClassPathResource resource = new ClassPathResource(properties.getCredentialsPath());

        GoogleCredentials credentials = ServiceAccountCredentials
                .fromStream(resource.getInputStream())
                .createScoped(Collections.singleton(DriveScopes.DRIVE_FILE));

        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

        return new Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                requestInitializer)
                .setApplicationName(properties.getApplicationName())
                .build();
    }
}