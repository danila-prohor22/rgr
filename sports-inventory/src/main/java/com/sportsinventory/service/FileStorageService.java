package com.sportsinventory.service;

import com.sportsinventory.dto.response.FileResponse;
import com.sportsinventory.entity.StoredFile;
import com.sportsinventory.exception.ResourceNotFoundException;
import com.sportsinventory.repository.StoredFileRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    private static final long MAX_FILE_SIZE_BYTES = 10L * 1024L * 1024L;

    private final StoredFileRepository storedFileRepository;

    @Value("${app.files.upload-dir:uploads}")
    private String uploadDir;

    @PostConstruct
    public void init() throws IOException {
        Files.createDirectories(getUploadPath());
    }

    @Transactional
    public FileResponse upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new IllegalArgumentException("File size exceeds 10MB limit");
        }

        String originalFileName = StringUtils.cleanPath(
                Objects.requireNonNullElse(file.getOriginalFilename(), "file")
        );
        originalFileName = Paths.get(originalFileName).getFileName().toString();
        if (originalFileName.isBlank() || originalFileName.contains("..")) {
            throw new IllegalArgumentException("Invalid file name");
        }

        String storedFileName = UUID.randomUUID() + getExtension(originalFileName);
        Path targetPath = getUploadPath().resolve(storedFileName).normalize();

        try {
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new IllegalStateException("Could not store file", ex);
        }

        StoredFile storedFile = StoredFile.builder()
                .originalFileName(originalFileName)
                .storedFileName(storedFileName)
                .contentType(file.getContentType())
                .sizeBytes(file.getSize())
                .storagePath(targetPath.toString())
                .uploadedAt(LocalDateTime.now())
                .build();

        return toResponse(storedFileRepository.save(storedFile));
    }

    public StoredFile getMetadata(Long id) {
        return storedFileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("File not found: " + id));
    }

    public Resource download(Long id) {
        StoredFile storedFile = getMetadata(id);
        try {
            Path filePath = Paths.get(storedFile.getStoragePath()).toAbsolutePath().normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new ResourceNotFoundException("File content not found: " + id);
            }
            return resource;
        } catch (MalformedURLException ex) {
            throw new ResourceNotFoundException("File content not found: " + id);
        }
    }

    @Transactional
    public void delete(Long id) {
        StoredFile storedFile = getMetadata(id);
        try {
            Files.deleteIfExists(Paths.get(storedFile.getStoragePath()).toAbsolutePath().normalize());
        } catch (IOException ex) {
            throw new IllegalStateException("Could not delete file", ex);
        }
        storedFileRepository.delete(storedFile);
    }

    private Path getUploadPath() {
        return Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    private String getExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex < 0) {
            return "";
        }
        return fileName.substring(dotIndex);
    }

    private FileResponse toResponse(StoredFile storedFile) {
        return FileResponse.builder()
                .id(storedFile.getId())
                .originalFileName(storedFile.getOriginalFileName())
                .contentType(storedFile.getContentType())
                .sizeBytes(storedFile.getSizeBytes())
                .uploadedAt(storedFile.getUploadedAt())
                .downloadUrl("/api/files/" + storedFile.getId())
                .build();
    }
}
