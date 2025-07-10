package org.aero.ingestion.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobClientBuilder;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobStorageException;
import com.azure.storage.blob.specialized.BlockBlobClient;

import lombok.extern.log4j.Log4j2;

/**
 * Store files in Azure Blob Storage.
 */
@Service
@Log4j2
public class AzureBlobFileStorageService implements FileStorageService {

	@Value("${azure.blob.endpoint}")
	private String blobEndpoint;

	@Value("${azure.blob.connection-string}")
	private String connectionString;

	@Value("${azure.blob.container-name}")
	private String container;

	@Override
	public void save(String filename, String targetCloudStorageFolder, InputStream content) throws IOException {
		
		BlockBlobClient client = new BlobClientBuilder().connectionString(connectionString).containerName(container)
				.blobName(filename).buildClient().getBlockBlobClient();
		
		Long length = (long) content.available();
		
		content.reset();
		
		log.info("Uploading file to Blob store file = %s , size %s".formatted(filename, length));		
	
		boolean overwriteIfExists = true;
		
		client.upload(content, length, overwriteIfExists);
		
		log.info("Completed Uploading file to Blob store file = %s , size %s".formatted(filename, length));

	}

	@Override
	public InputStream getFileInputStream(String filename) {
		
		BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().connectionString(connectionString)
				.buildClient();

		BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(container);
		
		BlobClient blobClient = containerClient.getBlobClient(filename);

		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			blobClient.download(out); // true to overwrite if exists)

			ByteArrayInputStream inputStream = new ByteArrayInputStream(out.toByteArray());

			return inputStream;
		} catch (BlobStorageException e) {
			// Handle common Azure Storage errors
			if (e.getStatusCode() == 404) {
				log.error("Error: Blob not found. Please check the container and blob name.");
				log.error("Container: '" + container + "', Blob: '" + filename + "'");
			} else {
				log.error("An Azure Storage error occurred: " + e.getMessage());
			}
			e.printStackTrace();
		} catch (IOException e1) {
			log.error("An Azure Storage error occurred: " + e1.getMessage());
		}
		return null;

	}
}
