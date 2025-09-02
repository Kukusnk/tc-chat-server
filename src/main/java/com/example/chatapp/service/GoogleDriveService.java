package com.example.chatapp.service;

//@Service
//@RequiredArgsConstructor
//@Slf4j
@Deprecated
public class GoogleDriveService {
//
//    private final Drive driveClient;
//    private final GoogleDriveProperties properties;
//
//    public String uploadAvatar(MultipartFile file, String username) throws IOException {
//        // Create file metadata
//        File fileMetadata = new File();
//        fileMetadata.setName(generateFileName(username, file.getOriginalFilename()));
//        fileMetadata.setParents(Collections.singletonList(properties.getFolderId()));
//
//        // Create file content
//        FileContent mediaContent = new FileContent(
//                file.getContentType(),
//                convertMultipartFileToFile(file)
//        );
//
//        // Upload file
//        File uploadedFile = driveClient.files()
//                .create(fileMetadata, mediaContent)
//                .setFields("id,webViewLink,webContentLink")
//                .execute();
//
//        // Make file publicly accessible
//        makeFilePublic(uploadedFile.getId());
//
//        log.info("Avatar uploaded successfully. File ID: {}", uploadedFile.getId());
//
//        // Return direct link for viewing
//        return generateDirectLink(uploadedFile.getId());
//    }
//
//    public void deleteAvatar(String fileId) throws IOException {
//        driveClient.files().delete(fileId).execute();
//        log.info("Avatar deleted successfully. File ID: {}", fileId);
//    }
//
//    public String updateAvatar(MultipartFile newFile, String oldFileId, String userId) throws IOException {
//        // Delete old file
//        if (oldFileId != null && !oldFileId.isEmpty()) {
//            deleteAvatar(oldFileId);
//        }
//
//        // Upload new file
//        return uploadAvatar(newFile, userId);
//    }
//
//    private void makeFilePublic(String fileId) throws IOException {
//        Permission permission = new Permission();
//        permission.setType("anyone");
//        permission.setRole("reader");
//
//        driveClient.permissions()
//                .create(fileId, permission)
//                .execute();
//    }
//
//    private String generateFileName(String userId, String originalFilename) {
//        String extension = getFileExtension(originalFilename);
//        return String.format("avatar_%s_%s%s",
//                userId,
//                System.currentTimeMillis(),
//                extension);
//    }
//
//    private String getFileExtension(String filename) {
//        if (filename == null || !filename.contains(".")) {
//            return ".jpg";
//        }
//        return filename.substring(filename.lastIndexOf("."));
//    }
//
//    private String generateDirectLink(String fileId) {
//        return "https://drive.google.com/uc?export=view&id=" + fileId;
//    }
//
//    private java.io.File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
//        java.io.File tempFile = java.io.File.createTempFile("upload", "tmp");
//        multipartFile.transferTo(tempFile);
//        return tempFile;
//    }
//
//    private String extractFileIdFromUrl(String driveUrl) {
//        // Extract file ID from Google Drive URL for deletion
//        if (driveUrl.contains("id=")) {
//            return driveUrl.substring(driveUrl.indexOf("id=") + 3);
//        }
//        return null;
//    }
}

