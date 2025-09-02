package com.example.chatapp.config;

//@Configuration
//@EnableConfigurationProperties(GoogleDriveProperties.class)
//@RequiredArgsConstructor
@Deprecated
public class GoogleDriveConfig {

//    private final ResourceLoader resourceLoader;
//
//    @Bean
//    public Drive driveClient(GoogleDriveProperties properties) throws IOException, GeneralSecurityException {
//        ClassPathResource resource = new ClassPathResource(properties.getCredentialsPath());
//
//        GoogleCredentials credentials = ServiceAccountCredentials
//                .fromStream(resource.getInputStream())
//                .createScoped(Collections.singleton(DriveScopes.DRIVE_FILE));
//
//        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);
//
//        return new Drive.Builder(
//                GoogleNetHttpTransport.newTrustedTransport(),
//                GsonFactory.getDefaultInstance(),
//                requestInitializer)
//                .setApplicationName(properties.getApplicationName())
//                .build();
//    }
}