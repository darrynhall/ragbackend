package org.aero.ingestion.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AlertService {

    public void sendFailureAlert(String filename, String userId, String stage, String message) {
        // TODO: integrate email or webhook here
        log.warn("ALERT - Ingestion failed: file={} user={} stage={} reason={}", filename, userId, stage, message);
    }
}
