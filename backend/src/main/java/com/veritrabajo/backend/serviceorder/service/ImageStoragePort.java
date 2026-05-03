package com.veritrabajo.backend.serviceorder.service;

/**
 * Port (outbound) for storing and removing service evidence images.
 * Implementations decide the actual storage mechanism (local, S3, etc.).
 */
public interface ImageStoragePort {

    String store(String filename, byte[] content);

    void delete(String url);
}
