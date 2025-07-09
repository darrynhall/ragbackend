package org.aero.ingestion.service;

import lombok.RequiredArgsConstructor;

import org.aero.ingestion.entity.ChunkHash;
import org.aero.ingestion.entity.FileHash;
import org.aero.ingestion.repository.ChunkHashRepository;
import org.aero.ingestion.repository.FileHashRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeconflictionService {

    private final FileHashRepository fileHashRepo;
    private final ChunkHashRepository chunkHashRepo;

    public boolean isDuplicateFile(byte[] fileBytes, String filename) {
        long size = fileBytes.length;

        Optional<FileHash> existing = fileHashRepo.findAll()
                .stream()
                .filter(f -> f.getFilename().equals(filename) && f.getSize() == size)
                .findFirst();

        if (existing.isPresent()) {
            String newHash = DigestUtils.md5DigestAsHex(fileBytes);
            if (newHash.equals(existing.get().getHash())) {
                return true;
            }
        }

        String hash = DigestUtils.md5DigestAsHex(fileBytes);
        fileHashRepo.save(FileHash.builder()
                .hash(hash)
                .filename(filename)
                .size(size)
                .ingestedAt(LocalDateTime.now())
                .build());

        return false;
    }

    public List<String> filterDuplicateChunks(List<String> chunks, String filename) {
        return chunks.stream()
                .filter(chunk -> {
                    String hash = DigestUtils.md5DigestAsHex(chunk.getBytes(StandardCharsets.UTF_8));
                    if (chunkHashRepo.existsByHash(hash)) {
                        return false;
                    } else {
                        chunkHashRepo.save(ChunkHash.builder()
                                .hash(hash)
                                .filename(filename)
                                .ingestedAt(LocalDateTime.now())
                                .build());
                        return true;
                    }
                })
                .collect(Collectors.toList());
    }
}
