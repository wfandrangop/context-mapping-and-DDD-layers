package com.veritrabajo.backend.serviceorder.infraestructure.storage;

import com.veritrabajo.backend.serviceorder.service.ImageStoragePort;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Local filesystem implementation of ImageStoragePort.
 * Stores uploaded images under ./uploads/ relative to the working directory.
 */
@Component
public class LocalImageStorageAdapter implements ImageStoragePort {

    private static final String UPLOAD_DIR = "uploads";

    @Override
    public String store(String filename, byte[] content) {
        String safeName = buildSafeName(filename);
        Path target = Paths.get(UPLOAD_DIR, safeName);
        writeFile(target, content);
        return UPLOAD_DIR + "/" + safeName;
    }

    @Override
    public void delete(String url) {
        Path target = Paths.get(url);
        deleteFile(target);
    }

    private String buildSafeName(String filename) {
        String base = (filename == null || filename.isBlank())
                ? "photo"
                : filename.replaceAll("[^a-zA-Z0-9._-]", "_");
        return UUID.randomUUID() + "_" + base;
    }

    private void writeFile(Path target, byte[] content) {
        try {
            Files.createDirectories(target.getParent());
            Files.write(target, content);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to store image: " + ex.getMessage(), ex);
        }
    }

    private void deleteFile(Path target) {
        try {
            Files.deleteIfExists(target);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to delete image: " + ex.getMessage(), ex);
        }
    }
}
