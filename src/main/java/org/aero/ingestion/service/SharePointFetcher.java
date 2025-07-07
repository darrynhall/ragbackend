package org.aero.ingestion.service;

import com.microsoft.graph.models.DriveItem;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.azure.identity.ClientSecretCredential;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.aero.ingestion.model.IngestionStatus;
import org.aero.ingestion.repository.IngestionStatusRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class SharePointFetcher {

    private final ClientSecretCredential clientSecretCredential;
    private final FileUploadService fileUploadService;
    private final IngestionStatusRepository statusRepository;

    private final String siteId = "YOUR_SITE_ID";
    private final String driveId = "YOUR_DRIVE_ID";
    private final String folderId = "YOUR_FOLDER_ID";

    private GraphServiceClient graphClient;

    @Scheduled(fixedRate = 300000)
    public void fetchFiles() {
        log.info("Fetching files from SharePoint folder");

        if (graphClient == null) {
            TokenCredentialAuthProvider authProvider = new TokenCredentialAuthProvider(clientSecretCredential);
            graphClient = GraphServiceClient.builder().authenticationProvider(authProvider).buildClient();
        }

        Set<String> currentFilenames = new HashSet<>();

        try {
            // v5+ SDK: Use request builder to get children

            java.util.List<DriveItem> items = graphClient
                    .sites(siteId)
                    .drives(driveId)
                    .items(folderId)
                    .children()
                    .buildRequest()
                    .get()
                    .getCurrentPage();

            for (DriveItem item : items) {
                if (item.file != null && !item.name.endsWith(".tmp")) {
                    currentFilenames.add(item.name);
                    boolean exists = statusRepository.existsById(item.name);
                    if (!exists) {
                        log.info("Downloading and ingesting new SharePoint file: {}", item.name);
                        try (InputStream contentStream = graphClient
                                .sites(siteId)
                                .drives(driveId)
                                .items(item.id)
                                .content()
                                .buildRequest()
                                .get()) {

                        	long contentLength = contentStream.readAllBytes().length;
                        	contentStream.reset(); // Reset the stream to allow re-reading
                            fileUploadService.upload(item.name, contentStream, contentLength, "sharepoint");

                        } catch (Exception ex) {
                            log.error("Failed to download or ingest: {}", item.name, ex);
                        }
                    }
                }
            }

            statusRepository.findAll().forEach(status -> {
                if (status.getUserId().equals("sharepoint") && !currentFilenames.contains(status.getFilename())) {
                    log.info("Detected deleted SharePoint file: {}", status.getFilename());
                    fileUploadService.deleteByFilename(status.getFilename());
                }
            });

        } catch (Exception e) {
            log.error("SharePoint fetch error", e);
        }
    }
}
