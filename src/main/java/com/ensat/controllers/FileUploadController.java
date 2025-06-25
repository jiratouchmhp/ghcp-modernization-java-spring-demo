package com.ensat.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * File Upload Controller for handling file uploads to local storage.
 */
@Controller
@RequestMapping("/files")
public class FileUploadController {

    @Value("${file.upload.path:/uploads}")
    private String uploadPath;

    /**
     * Show file upload form
     */
    @GetMapping("/upload")
    public String showUploadForm(Model model) {
        return "fileupload";
    }

    /**
     * Handle file upload
     */
    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {
        
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Please select a file to upload");
            return "redirect:/files/upload";
        }

        try {
            // Create upload directory if it doesn't exist
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

            // Save file
            Path filePath = uploadDir.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            redirectAttributes.addFlashAttribute("success", 
                "File uploaded successfully: " + originalFilename);
            redirectAttributes.addFlashAttribute("filename", uniqueFilename);
            redirectAttributes.addFlashAttribute("originalName", originalFilename);

        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", 
                "Failed to upload file: " + e.getMessage());
        }

        return "redirect:/files/upload";
    }

    /**
     * Handle multiple file uploads
     */
    @PostMapping("/upload/multiple")
    public String handleMultipleFileUpload(@RequestParam("files") MultipartFile[] files,
                                           RedirectAttributes redirectAttributes) {
        
        if (files.length == 0) {
            redirectAttributes.addFlashAttribute("error", "Please select files to upload");
            return "redirect:/files/upload";
        }

        int successCount = 0;
        int errorCount = 0;
        StringBuilder successMessages = new StringBuilder();
        StringBuilder errorMessages = new StringBuilder();

        try {
            // Create upload directory if it doesn't exist
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    try {
                        // Generate unique filename
                        String originalFilename = file.getOriginalFilename();
                        String fileExtension = "";
                        if (originalFilename != null && originalFilename.contains(".")) {
                            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                        }
                        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

                        // Save file
                        Path filePath = uploadDir.resolve(uniqueFilename);
                        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                        successCount++;
                        successMessages.append(originalFilename).append(", ");

                    } catch (IOException e) {
                        errorCount++;
                        errorMessages.append(file.getOriginalFilename())
                                    .append(" (").append(e.getMessage()).append("), ");
                    }
                }
            }

            if (successCount > 0) {
                redirectAttributes.addFlashAttribute("success", 
                    successCount + " files uploaded successfully: " + 
                    successMessages.toString().replaceAll(", $", ""));
            }

            if (errorCount > 0) {
                redirectAttributes.addFlashAttribute("error", 
                    errorCount + " files failed to upload: " + 
                    errorMessages.toString().replaceAll(", $", ""));
            }

        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", 
                "Failed to create upload directory: " + e.getMessage());
        }

        return "redirect:/files/upload";
    }

    /**
     * REST API endpoint for file upload
     */
    @PostMapping("/api/upload")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> uploadFileApi(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        
        if (file.isEmpty()) {
            response.put("success", false);
            response.put("message", "Please select a file to upload");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            // Create upload directory if it doesn't exist
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

            // Save file
            Path filePath = uploadDir.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            response.put("success", true);
            response.put("message", "File uploaded successfully");
            response.put("filename", uniqueFilename);
            response.put("originalName", originalFilename);
            response.put("filePath", filePath.toString());
            response.put("fileSize", file.getSize());

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "Failed to upload file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * List uploaded files
     */
    @GetMapping("/list")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> listUploadedFiles() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            java.util.List<String> files = Files.list(uploadDir)
                    .filter(Files::isRegularFile)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(java.util.stream.Collectors.toList());

            response.put("success", true);
            response.put("files", files);
            response.put("count", files.size());

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "Failed to list files: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Delete uploaded file
     */
    @DeleteMapping("/delete/{filename}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteFile(@PathVariable String filename) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Path filePath = Paths.get(uploadPath).resolve(filename);
            
            if (!Files.exists(filePath)) {
                response.put("success", false);
                response.put("message", "File not found");
                return ResponseEntity.notFound().build();
            }

            Files.delete(filePath);
            
            response.put("success", true);
            response.put("message", "File deleted successfully");
            return ResponseEntity.ok(response);

        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "Failed to delete file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
