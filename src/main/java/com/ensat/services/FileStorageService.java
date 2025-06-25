package com.ensat.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for handling file operations
 */
@Service
public class FileStorageService {

    @Value("${file.upload.path:/uploads}")
    private String uploadPath;

    /**
     * Initialize storage directory
     */
    public void init() throws IOException {
        Path uploadDir = Paths.get(uploadPath);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
    }

    /**
     * Store a file and return the stored filename
     */
    public String store(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Cannot store empty file");
        }

        // Initialize directory if needed
        init();

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

        // Save file
        Path filePath = Paths.get(uploadPath).resolve(uniqueFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return uniqueFilename;
    }

    /**
     * Load a file as Path
     */
    public Path load(String filename) {
        return Paths.get(uploadPath).resolve(filename);
    }

    /**
     * Check if file exists
     */
    public boolean exists(String filename) {
        return Files.exists(load(filename));
    }

    /**
     * Delete a file
     */
    public boolean delete(String filename) throws IOException {
        Path filePath = load(filename);
        if (Files.exists(filePath)) {
            Files.delete(filePath);
            return true;
        }
        return false;
    }

    /**
     * List all files in upload directory
     */
    public List<String> listAllFiles() throws IOException {
        init();
        return Files.list(Paths.get(uploadPath))
                .filter(Files::isRegularFile)
                .map(Path::getFileName)
                .map(Path::toString)
                .collect(Collectors.toList());
    }

    /**
     * Get file size
     */
    public long getFileSize(String filename) throws IOException {
        Path filePath = load(filename);
        if (Files.exists(filePath)) {
            return Files.size(filePath);
        }
        return 0;
    }

    /**
     * Get upload directory path
     */
    public String getUploadPath() {
        return uploadPath;
    }
}
