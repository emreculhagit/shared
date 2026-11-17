
package com.example.aiDemo.service;

import com.example.aiDemo.memory.MemoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemoryProcessingService {

    private final MemoryExtractionService extractionService;
    private final MemoryService memoryService;

    public MemoryProcessingService(
            MemoryExtractionService extractionService,
            MemoryService memoryService) {
        this.extractionService = extractionService;
        this.memoryService = memoryService;
    }

    public List<MemoryCandidate> process(String message, String userId) {

        List<MemoryCandidate> candidates = extractionService.extract(message);

        for (MemoryCandidate candidate : candidates) {

            if (candidate == null
                    || candidate.key() == null
                    || candidate.key().isBlank()
                    || candidate.content() == null
                    || candidate.content().isBlank()) {
                continue;
            }

            if (candidate.content().length() > 300) {
                continue;
            }

            memoryService.remember(
                    userId,
                    candidate.key(),
                    candidate.content()
            );
        }

        return candidates;
    }
}
