package org.aero.ingestion.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;

/**
 * Store files in AWS S3.
 */
@Service
@Profile("aws")
public class S3FileStorageService implements FileStorageService {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucket;

    public S3FileStorageService() {
        this.s3Client = S3Client.builder().build();
    }

    @Override
    public void save(String filename, InputStream content) {
        try {
            byte[] data = content.readAllBytes();
            s3Client.putObject(PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(filename)
                    .build(), RequestBody.fromBytes(data));
        } catch (Exception e) {
            throw new RuntimeException("Failed to store file to S3", e);
        }
    }

	@Override
	public void save(String filename, InputStream content, long length) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public InputStream getFileInputStream(String filename) {
		// TODO Auto-generated method stub
		return null;
	}
}
