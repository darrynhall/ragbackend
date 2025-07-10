package org.aero.ingestion.service;

import java.io.IOException;
import java.io.InputStream;

/**
 * Abstraction for storing uploaded files.
 */
public interface FileStorageService {

	InputStream getFileInputStream(String filename);

	void save(String filename, String targetCloudStorageFolder, InputStream content) throws IOException;
}
