package org.aero.ingestion.service;

import java.io.InputStream;

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

	InputStream getFileInputStream(String filename);

	void save(String filename, String targetCloudStorageFolder, InputStream content, long length);
}
