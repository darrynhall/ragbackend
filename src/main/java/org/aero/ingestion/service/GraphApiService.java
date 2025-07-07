package org.aero.ingestion.service;

import org.springframework.stereotype.Service;

import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.models.DriveItem;
import com.microsoft.graph.requests.GraphServiceClient;

import okhttp3.Request;

@Service
public class GraphApiService {

    private final GraphServiceClient<Request> graphClient;

    public GraphApiService(TokenCredentialAuthProvider authProvider) {
        this.graphClient = GraphServiceClient.builder().authenticationProvider(authProvider).buildClient();
    }

    public byte[] fetchFileBytes(String siteId, String driveId, String itemId) {
        DriveItem item = graphClient.sites(siteId).drives(driveId).items(itemId).buildRequest().get();
        try (var is = graphClient.sites(siteId).drives(driveId).items(itemId).content().buildRequest().get()) {
            return is.readAllBytes();
        } catch (java.io.IOException e) {
            throw new RuntimeException("Failed to read file bytes", e);
        }
    }
}
