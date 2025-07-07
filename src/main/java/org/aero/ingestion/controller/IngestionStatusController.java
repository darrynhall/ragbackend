package org.aero.ingestion.controller;

import lombok.RequiredArgsConstructor;

import org.aero.ingestion.model.IngestionStatus;
import org.aero.ingestion.repository.IngestionStatusRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/status")
@RequiredArgsConstructor
public class IngestionStatusController {

    private final IngestionStatusRepository repository;

    @GetMapping("/{filename}")
    public IngestionStatus getStatus(@PathVariable String filename) {
        return repository.findById(filename).orElse(null);
    }

    @GetMapping
    public Page<IngestionStatus> getAllStatuses(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @GetMapping("/user/{userId}")
    public Page<IngestionStatus> getByUser(@PathVariable String userId, Pageable pageable) {
        return repository.findByUserId(userId, pageable);
    }

    @GetMapping("/recent/{days}")
    public Page<IngestionStatus> getRecent(@PathVariable int days, Pageable pageable) {
        return repository.findByTimestampAfter(LocalDateTime.now().minusDays(days), pageable);
    }

    @GetMapping("/user/{userId}/recent/{days}")
    public Page<IngestionStatus> getUserRecent(@PathVariable String userId, @PathVariable int days, Pageable pageable) {
        return repository.findByUserIdAndTimestampAfter(userId, LocalDateTime.now().minusDays(days), pageable);
    }
}
