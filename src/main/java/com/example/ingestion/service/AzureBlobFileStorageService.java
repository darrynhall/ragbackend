package com.example.ingestion.service;

import com.azure.storage.blob.BlobClientBuilder;
import com.azure.storage.blob.specialized.BlockBlobClient;
import com.azure.core.util.BinaryData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;

/**
 * Store files in Azure Blob Storage.
 */
@Service
public class AzureBlobFileStorageService implements FileStorageService {

    @Value("${azure.blob.endpoint}")
    private String blobEndpoint;

    @Value("${azure.blob.container-name}")
    private String container;

    @Override
    public void save(String filename, InputStream content) {
        BlockBlobClient client = new BlobClientBuilder()
                .endpoint(blobEndpoint)
                .containerName(container)
                .blobName(filename)
                .buildClient()
                .getBlockBlobClient();

        try {
            byte[] data = content.readAllBytes();
            client.upload(BinaryData.fromBytes(data), true);
        } catch (Exception e) {
            throw new RuntimeException("Failed to store file to Azure Blob Storage", e);
        }
    }
}
