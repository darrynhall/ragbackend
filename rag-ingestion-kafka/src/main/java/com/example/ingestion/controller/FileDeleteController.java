package com.example.ingestion.controller;

import com.azure.search.documents.SearchClient;
import com.azure.storage.blob.BlobClientBuilder;
import com.example.ingestion.model.IngestionStatus;
import com.example.ingestion.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileDeleteController {

    private final ChunkHashRepository chunkHashRepo;
    private final FileHashRepository fileHashRepo;
    private final IngestionStatusRepository statusRepo;
    private final SearchClient searchClient;
    private final S3Client s3Client;

    @Value("${azure.blob.container-name}")
    private String blobContainer;

    @Value("${aws.s3.bucket-name}")
    private String s3Bucket;

    @Value("${azure.blob.endpoint}")
    private String blobEndpoint;

    @DeleteMapping("/delete/{filename}")
    public String deleteArtifacts(@PathVariable String filename) {
        // Delete chunk and file hashes
        chunkHashRepo.deleteAll(chunkHashRepo.findAll().stream()
                .filter(c -> filename.equals(c.getFilename())).toList());
        fileHashRepo.deleteAll(fileHashRepo.findAll().stream()
                .filter(f -> filename.equals(f.getFilename())).toList());
        statusRepo.save(IngestionStatus.builder()
                .filename(filename)
                .userId("system")
                .status("DELETED")
                .timestamp(java.time.LocalDateTime.now())
                .build());

        // Delete from Azure AI Search
        try {
            searchClient.deleteDocument("id", filename);
        } catch (Exception e) {
            System.out.println("Azure AI Search deletion failed: " + e.getMessage());
        }

        // Delete from Azure Blob Storage
        try {
            new BlobClientBuilder()
                    .endpoint(blobEndpoint)
                    .containerName(blobContainer)
                    .blobName(filename)
                    .buildClient()
                    .delete();
        } catch (Exception e) {
            System.out.println("Azure Blob deletion failed: " + e.getMessage());
        }

        // Delete from AWS S3
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(s3Bucket)
                    .key(filename)
                    .build());
        } catch (Exception e) {
            System.out.println("AWS S3 deletion failed: " + e.getMessage());
        }

        return "Deleted artifacts for: " + filename;
    }
}
