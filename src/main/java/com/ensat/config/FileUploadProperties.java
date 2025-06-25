package com.ensat.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for file upload
 */
@Configuration
@ConfigurationProperties(prefix = "file.upload")
public class FileUploadProperties {
    
    private String path = "./uploads";
    private long maxFileSize = 10 * 1024 * 1024; // 10MB
    private long maxRequestSize = 10 * 1024 * 1024; // 10MB
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    public long getMaxFileSize() {
        return maxFileSize;
    }
    
    public void setMaxFileSize(long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }
    
    public long getMaxRequestSize() {
        return maxRequestSize;
    }
    
    public void setMaxRequestSize(long maxRequestSize) {
        this.maxRequestSize = maxRequestSize;
    }
}
