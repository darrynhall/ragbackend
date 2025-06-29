package com.example.ingestion.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.Parameter;

import java.util.ArrayList;
import java.util.List;

import com.example.ingestion.service.FileUploadService;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileUploadService fileUploadService;

    @PostMapping("/upload")
    public ResponseEntity<List<String>> uploadFiles(
            @Parameter(description = "Files to upload", required = true)
            @RequestParam("files") MultipartFile[] files) {
        List<String> fileDownloadUris = new ArrayList<>();
        String userId = "system"; // Replace with actual user id if available
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            try {
                fileUploadService.upload(fileName, file.getInputStream(), userId);
            } catch (Exception e) {
                throw new RuntimeException("Failed to process file: " + fileName, e);
            }
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/files/download/")
                    .path(fileName)
                    .toUriString();
            fileDownloadUris.add(fileDownloadUri);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(fileDownloadUris);
    }
}
