package com.example.ingestion.service;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Abstraction for storing uploaded files.
 */
public interface FileStorageService {
    /**
     * Persist the given content under the provided filename.
     *
     * @param filename name of the file
     * @param content file data
     */
    void save(String filename, InputStream content);

	void save(String filename, InputStream content, long length);

	InputStream getFileInputStream(String filename);
}
