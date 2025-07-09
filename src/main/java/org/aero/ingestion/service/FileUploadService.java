package org.aero.ingestion.service;

import static org.aero.ingestion.constants.AppConstants.FILE_UPLOADED_QUEUE;
import static org.aero.ingestion.constants.AppConstants.FILE_DELETED_QUEUE; 

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.aero.ingestion.entity.FileDeletionEvent;
import org.aero.ingestion.entity.FileUploadEvent;
import org.aero.loggedinuser.person.LoggedInUser;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service responsible for initiating the ingestion pipeline for uploaded files.
 * In a real implementation this would also persist the file to storage.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FileUploadService {

	private final JmsTemplate jmsTemplate;
	private final FileStorageService fileStorageService;
	private final LoggedInUser loggedInUser;

	/**
	 * Upload the file content and send an event to start processing.
	 *
	 * @param filename    name of the file
	 * @param content     the file content
	 * @param badgeNumber id of the user uploading the file
	 * @throws IOException 
	 */
	public void upload(MultipartFile file, String targetCloudStorageFolder, List<String> allowedUserBadges,
			List<String> allowedUserGroups, List<String> allowedSharePointPermissions) throws IOException {		
		String badgeNumber = loggedInUser.getBadge();
		InputStream content = new ByteArrayInputStream(file.getBytes());
		String fileName = file.getOriginalFilename();
		fileStorageService.save(fileName, targetCloudStorageFolder, content, file.getBytes().length);
		log.info("Received file '{}' from user '{}'; emitting upload event", fileName, badgeNumber);
		jmsTemplate.convertAndSend(FILE_UPLOADED_QUEUE,
				new FileUploadEvent(fileName, badgeNumber,   allowedUserBadges,
						 allowedUserGroups,  allowedSharePointPermissions));
	}
	

	/**
	 * Upload the file content and send an event to start processing.
	 *
	 * @param filename    name of the file
	 * @param content     the file content
	 * @param badgeNumber id of the user uploading the file
	 * @throws IOException 
	 */
	public void upload(InputStream contentStream, String fileName, String targetCloudStorageFolder, List<String> allowedUserBadges,
			List<String> allowedUserGroups, List<String> allowedSharePointPermissions) throws IOException {		
		String badgeNumber = loggedInUser.getBadge();
		
    	long contentLength = contentStream.readAllBytes().length;
    	contentStream.reset(); // Reset the stream to allow re-reading
		 
 
		fileStorageService.save(fileName, targetCloudStorageFolder, contentStream, contentLength);
		log.info("Received file '{}' from user '{}'; emitting upload event", fileName, badgeNumber);
		jmsTemplate.convertAndSend(FILE_UPLOADED_QUEUE,
				new FileUploadEvent(fileName, badgeNumber,   allowedUserBadges,
						 allowedUserGroups,  allowedSharePointPermissions));
	}

	/**
	 * Delete the stored file and emit a deletion event.
	 *
	 * @param filename name of the file to delete
	 */
	public void deleteByFilename(String filename) {
		String badgeNumber = loggedInUser.getBadge();
		log.info("Deleting stored file '{}'; emitting delete event for badge {}", filename, badgeNumber);
		jmsTemplate.convertAndSend(FILE_DELETED_QUEUE, new FileDeletionEvent(filename, badgeNumber));
	}
}
