package com.example.ingestion.controller;

import com.example.ingestion.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileUploadService fileUploadService;

    @PostMapping
    public String upload(@RequestParam("file") MultipartFile file,
                         @RequestParam(value = "userId", defaultValue = "anonymous") String userId) {
        try {
            fileUploadService.upload(file.getOriginalFilename(), file.getInputStream(), userId);
            return "Upload started for: " + file.getOriginalFilename();
        } catch (Exception e) {
            throw new RuntimeException("Upload failed", e);
        }
    }
}
