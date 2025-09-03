package com.example.chatapp.service;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

//@Service
//public class AvatarStorageService {
//
//    private final Storage storage;
//    private final String bucketName;
//
//    public AvatarStorageService(Storage storage,
//                                @Value("${gcp.storage.bucket}") String bucketName) {
//        this.storage = storage;
//        this.bucketName = bucketName;
//    }
//
//    public String uploadAvatar(MultipartFile file, String username) throws IOException {
//        String fileName = username + "-" + UUID.randomUUID() + "-" + file.getOriginalFilename();
//
//        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, fileName)
//                .setContentType(file.getContentType())
//                .build();
//
//        storage.create(blobInfo, file.getBytes());
//
//        // Публичная ссылка (если бакет открыт)
//        return String.format("https://storage.googleapis.com/%s/%s", bucketName, fileName);
//    }
//}

//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class AvatarStorageService {
//
//    private final Storage storage;
//    private final String bucketName;
//
//    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
//            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
//    );
//    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
//
//    public String uploadAvatar(MultipartFile file, String username) throws IOException {
//        validateFile(file);
//
//        String fileName = generateFileName(file.getOriginalFilename(), username);
//
//        BlobId blobId = BlobId.of(bucketName, "avatars/" + fileName);
//        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
//                .setContentType(file.getContentType())
//                .setCacheControl("public, max-age=86400")
//                .build();
//
//        try {
//            Blob blob = storage.create(blobInfo, file.getBytes());
//
//            // Делаем файл публично доступным
//            storage.createAcl(blobId, Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));
//
//            String publicUrl = String.format("https://storage.googleapis.com/%s/avatars/%s",
//                    bucketName, fileName);
//
//            log.info("Avatar uploaded successfully for user: {}, URL: {}", username, publicUrl);
//            return publicUrl;
//
//        } catch (StorageException e) {
//            log.error("Failed to upload avatar for user: {}", username, e);
//            throw new RuntimeException("Failed to upload avatar: " + e.getMessage());
//        }
//    }
//
//    public void deleteAvatar(String avatarUrl) {
//        if (avatarUrl == null || avatarUrl.isEmpty()) {
//            return;
//        }
//
//        try {
//            String fileName = extractFileNameFromUrl(avatarUrl);
//            BlobId blobId = BlobId.of(bucketName, "avatars/" + fileName);
//            boolean deleted = storage.delete(blobId);
//
//            if (deleted) {
//                log.info("Avatar deleted successfully: {}", fileName);
//            } else {
//                log.warn("Avatar not found for deletion: {}", fileName);
//            }
//        } catch (Exception e) {
//            log.error("Failed to delete avatar: {}", avatarUrl, e);
//        }
//    }
//
//    private void validateFile(MultipartFile file) {
//        if (file.isEmpty()) {
//            throw new IllegalArgumentException("File is empty");
//        }
//
//        if (file.getSize() > MAX_FILE_SIZE) {
//            throw new IllegalArgumentException("File size exceeds 5MB limit");
//        }
//
//        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
//            throw new IllegalArgumentException("Invalid file type. Allowed types: " +
//                    String.join(", ", ALLOWED_CONTENT_TYPES));
//        }
//    }
//
//    private String generateFileName(String originalFileName, String username) {
//        String extension = "";
//        if (originalFileName != null && originalFileName.contains(".")) {
//            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
//        }
//
//        return username + "_" + UUID.randomUUID().toString() + extension;
//    }
//
//    private String extractFileNameFromUrl(String url) {
//        return url.substring(url.lastIndexOf("/") + 1);
//    }
//}

@Service
@RequiredArgsConstructor
@Slf4j
public class AvatarStorageService {

    private final Storage storage;
    private final String bucketName;

    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    public String uploadAvatar(MultipartFile file, String username) throws IOException {
        validateFile(file);

        String fileName = generateFileName(file.getOriginalFilename(), username);

        BlobId blobId = BlobId.of(bucketName, "avatars/" + fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(file.getContentType())
                .setCacheControl("public, max-age=86400") // кэш на 1 день
                .build();

        try {
            // Загружаем файл в GCS
            storage.create(blobInfo, file.getBytes());

            // Поскольку bucket публичный, ACL больше не нужен
            String publicUrl = String.format("https://storage.googleapis.com/%s/avatars/%s",
                    bucketName, fileName);

            log.info("Avatar uploaded successfully for user: {}, URL: {}", username, publicUrl);
            return publicUrl;

        } catch (StorageException e) {
            log.error("Failed to upload avatar for user: {}", username, e);
            throw new RuntimeException("Failed to upload avatar: " + e.getMessage());
        }
    }

    public void deleteAvatar(String avatarUrl) {
        if (avatarUrl == null || avatarUrl.isEmpty()) {
            return;
        }

        try {
            String fileName = extractFileNameFromUrl(avatarUrl);
            BlobId blobId = BlobId.of(bucketName, "avatars/" + fileName);
            boolean deleted = storage.delete(blobId);

            if (deleted) {
                log.info("Avatar deleted successfully: {}", fileName);
            } else {
                log.warn("Avatar not found for deletion: {}", fileName);
            }
        } catch (Exception e) {
            log.error("Failed to delete avatar: {}", avatarUrl, e);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds 5MB limit");
        }

        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("Invalid file type. Allowed types: " +
                    String.join(", ", ALLOWED_CONTENT_TYPES));
        }
    }

    private String generateFileName(String originalFileName, String username) {
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }

        return username + "_" + UUID.randomUUID().toString() + extension;
    }

    private String extractFileNameFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }
}

