package org.aero.ingestion.service;

import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.models.DriveItem;
import com.microsoft.graph.requests.GraphServiceClient;
import okhttp3.Request;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class GraphApiService {

    private final GraphServiceClient<Request> graphClient;

    public GraphApiService(TokenCredentialAuthProvider authProvider) {
        this.graphClient = GraphServiceClient.builder().authenticationProvider(authProvider).buildClient();
    }

    public byte[] fetchFileBytes(String siteId, String driveId, String itemId) {
        DriveItem item = graphClient.sites(siteId).drives(driveId).items(itemId).buildRequest().get();
        return graphClient.sites(siteId).drives(driveId).items(itemId).content().buildRequest().get().readAllBytes();
    }
}
