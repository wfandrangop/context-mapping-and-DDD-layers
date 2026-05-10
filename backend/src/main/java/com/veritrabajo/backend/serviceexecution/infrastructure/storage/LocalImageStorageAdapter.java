package com.veritrabajo.backend.serviceexecution.infrastructure.storage;

import com.veritrabajo.backend.serviceexecution.domain.exception.ImageStorageException;
import com.veritrabajo.backend.serviceexecution.domain.port.ImageStoragePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

@Component
public class LocalImageStorageAdapter implements ImageStoragePort {

    private static final String PUBLIC_URL_PREFIX = "uploads/";
    private final Path uploadRoot;

    public LocalImageStorageAdapter(@Value("${app.storage.upload-dir:uploads}") String uploadDir) {
        this.uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
        ensureUploadDirectory();
    }

    @Override
    public String store(String filename, byte[] content) {
        Objects.requireNonNull(content, "Image content is required");
        String safeName = buildSafeName(filename);
        Path target = uploadRoot.resolve(safeName).normalize();
        writeFile(target, content);
        return PUBLIC_URL_PREFIX + safeName;
    }

    @Override
    public void delete(String url) {
        Path target = resolveStoredPath(url);
        deleteFile(target);
    }

    private String buildSafeName(String filename) {
        String base = (filename == null || filename.isBlank())
                ? "photo"
                : filename.replaceAll("[^a-zA-Z0-9._-]", "_");
        return UUID.randomUUID() + "_" + base;
    }

    private Path resolveStoredPath(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("Image url is required");
        }
        String normalizedUrl = url.replace('\\', '/');
        String relativePath = normalizedUrl.startsWith(PUBLIC_URL_PREFIX)
                ? normalizedUrl.substring(PUBLIC_URL_PREFIX.length())
                : normalizedUrl;
        Path target = uploadRoot.resolve(relativePath).normalize();
        if (!target.startsWith(uploadRoot)) {
            throw new IllegalArgumentException("Image url points outside upload directory");
        }
        return target;
    }

    private void ensureUploadDirectory() {
        try {
            Files.createDirectories(uploadRoot);
            if (!Files.isDirectory(uploadRoot) || !Files.isWritable(uploadRoot)) {
                throw new ImageStorageException("Upload directory is not writable: " + uploadRoot);
            }
        } catch (IOException ex) {
            throw new ImageStorageException("Failed to initialize upload directory: " + uploadRoot,
                    ex);
        }
    }

    private void writeFile(Path target, byte[] content) {
        try {
            Files.createDirectories(target.getParent());
            Files.write(target, content);
        } catch (IOException ex) {
            throw new ImageStorageException("Failed to store image", ex);
        }
    }

    private void deleteFile(Path target) {
        try {
            Files.deleteIfExists(target);
        } catch (IOException ex) {
            throw new ImageStorageException("Failed to delete image", ex);
        }
    }
}
