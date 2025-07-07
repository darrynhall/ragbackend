package com.example.ingestion.service;

import org.aero.ingestion.repository.ChunkHashRepository;
import org.aero.ingestion.repository.FileHashRepository;
import org.aero.ingestion.service.DeconflictionService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class DeconflictionServiceTest {

    @Test
    public void testFilterDuplicateChunks() {
        ChunkHashRepository chunkRepo = mock(ChunkHashRepository.class);
        FileHashRepository fileRepo = mock(FileHashRepository.class);

        DeconflictionService service = new DeconflictionService(fileRepo, chunkRepo);
        when(chunkRepo.existsByHash(anyString())).thenReturn(false);

        List<String> result = service.filterDuplicateChunks(List.of("test1", "test2"), "test.txt");
        assertEquals(2, result.size());
    }
}
