package org.aero.ingestion.controller;

import java.util.ArrayList;
import java.util.List;

import org.aero.ingestion.service.EtlPipeline;
import org.aero.ingestion.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileUploadController {

	private final FileUploadService fileUploadService;

	@PostMapping(consumes = { "multipart/form-data", MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<List<String>> uploadFiles(@RequestPart(name = "file") final MultipartFile[] files) {

		List<String> fileDownloadUris = new ArrayList<>();

		String targetCloudStorageFolder  = "";
				 List<String> allowedUserBadges = null;
				 List<String> allowedUserGroups = null;
				 List<String> allowedSharePointPermissions = null;
		for (MultipartFile file : files) {

			try {

				fileUploadService.upload(file, targetCloudStorageFolder, allowedUserBadges, allowedUserGroups,
						allowedSharePointPermissions);

			} catch (Exception e) {
				throw new RuntimeException("Failed to process file: " + file.getName(), e);
			}
			String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/files/download/")
					.path(file.getName()).toUriString();
			fileDownloadUris.add(fileDownloadUri);
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(fileDownloadUris);
	}

	@Autowired
	EtlPipeline etlPipeline;

	@GetMapping("/ingestfromtemp")
	public String ingestFromTemp() {
		return etlPipeline.runIngestion();
	}
}
