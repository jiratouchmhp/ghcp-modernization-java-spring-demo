package com.ensat.controllers;

import com.ensat.dto.ErrorResponse;
import com.ensat.dto.FileInfoResponse;
import com.ensat.services.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Controller for handling file downloads and serving static files
 */
@RestController
@RequestMapping("/files")
public class FileDownloadController {

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * Download a file
     */
    @GetMapping("/download/{filename:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        try {
            Path filePath = fileStorageService.load(filename);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, 
                               "attachment; filename=\"" + filename + "\"")
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Serve file as inline content (for images, etc.)
     */
    @GetMapping("/view/{filename:.+}")
    public ResponseEntity<Resource> viewFile(@PathVariable String filename) {
        try {
            Path filePath = fileStorageService.load(filename);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                String contentType = determineContentType(filename);
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, 
                               "inline; filename=\"" + filename + "\"")
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get file information
     */
    @GetMapping("/info/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Object> getFileInfo(@PathVariable String filename) {
        try {
            if (!fileStorageService.exists(filename)) {
                return ResponseEntity.notFound().build();
            }

            long fileSize = fileStorageService.getFileSize(filename);
            Path filePath = fileStorageService.load(filename);
            
            FileInfoResponse response = new FileInfoResponse(
                filename, 
                fileSize, 
                filePath.toString(), 
                determineContentType(filename), 
                true
            );
            
            return ResponseEntity.ok(response);

        } catch (IOException e) {
            ErrorResponse errorResponse = new ErrorResponse("Failed to get file information: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Determine content type based on file extension
     */
    private String determineContentType(String filename) {
        String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        
        switch (extension) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "pdf":
                return "application/pdf";
            case "txt":
                return "text/plain";
            case "html":
                return "text/html";
            case "css":
                return "text/css";
            case "js":
                return "application/javascript";
            case "json":
                return "application/json";
            case "xml":
                return "application/xml";
            case "zip":
                return "application/zip";
            default:
                return "application/octet-stream";
        }
    }
}
