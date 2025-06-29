package com.example.ingestion.controller;



import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.ingestion.service.FileUploadService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileUploadService fileUploadService;

//    @Operation(summary = "Upload one or more files", description = "Upload files using a file picker.",
//        requestBody = @RequestBody(content = @Content(mediaType = "multipart/form-data",
//            array = @ArraySchema(schema = @Schema( format = "binary")))))
//    @PostMapping(path="/upload")
//    public ResponseEntity<List<String>> uploadFiles(
    
    
	@PostMapping( consumes = { "multipart/form-data", MediaType.APPLICATION_JSON_VALUE })
	public  ResponseEntity<List<String>> importPlanData(@RequestPart(name = "file") final MultipartFile[] files) {
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
