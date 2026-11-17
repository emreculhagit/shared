
package com.example.aiDemo.controller;

import com.example.aiDemo.service.MemoryCandidate;
import com.example.aiDemo.service.MemoryProcessingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/memory-processing")
public class MemoryProcessingController {

    private final MemoryProcessingService processingService;

    public MemoryProcessingController(
            MemoryProcessingService processingService) {
        this.processingService = processingService;
    }

    @PostMapping
    public ProcessingResponse process(
            @RequestBody ProcessingRequest request) {

        List<MemoryCandidate> result = processingService.process(
                request.message(),
                request.userId()
        );

        return new ProcessingResponse(result);
    }

    public record ProcessingRequest(String userId, String message) {}

    public record ProcessingResponse(List<MemoryCandidate> savedCandidates) {}
}
