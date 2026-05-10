package com.veritrabajo.backend.serviceexecution.domain.port;

public interface ImageStoragePort {

    String store(String filename, byte[] content);

    void delete(String url);
}
