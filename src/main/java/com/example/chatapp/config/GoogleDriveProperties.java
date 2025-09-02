package com.example.chatapp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "google.drive")
@Data
public class GoogleDriveProperties {
    private String credentialsPath = "credentials/team-challenge-chat-470408-f0287fce68a3.json";
    private String applicationName = "Avatar Service";
    private String folderId;
}
