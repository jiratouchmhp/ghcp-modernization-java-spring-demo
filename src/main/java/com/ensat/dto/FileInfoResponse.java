package com.ensat.dto;

/**
 * Response class for file information
 */
public class FileInfoResponse {
    private String name;
    private long size;
    private String path;
    private String contentType;
    private boolean exists;

    public FileInfoResponse(String name, long size, String path, String contentType, boolean exists) {
        this.name = name;
        this.size = size;
        this.path = path;
        this.contentType = contentType;
        this.exists = exists;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public boolean isExists() {
        return exists;
    }

    public void setExists(boolean exists) {
        this.exists = exists;
    }
}
