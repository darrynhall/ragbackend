package com.example.ingestion.service;

import com.microsoft.graph.models.DriveItem;
import com.microsoft.graph.requests.GraphServiceClient;
import lombok.RequiredArgsConstructor;
import okhttp3.Request;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SharePointCrawler {

    private final GraphServiceClient<Request> graphClient;
    private final FileUploadService fileUploadService;

    // Run every 5 minutes
    @Scheduled(fixedRate = 300_000)
    public void crawl() {
        // TODO: List items from a SharePoint folder
        List<DriveItem> items = List.of(); // graphClient.sites("site-id").drive().items()...
        for (DriveItem item : items) {
            // TODO: Download and call fileUploadService.upload(...)
        }
    }
}
