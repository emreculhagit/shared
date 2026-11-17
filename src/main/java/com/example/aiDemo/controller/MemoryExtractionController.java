
package com.example.aiDemo.controller;

import com.example.aiDemo.service.MemoryCandidate;
import com.example.aiDemo.service.MemoryExtractionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/memory-extraction")
public class MemoryExtractionController {

    private final MemoryExtractionService extractionService;

    public MemoryExtractionController(
            MemoryExtractionService extractionService) {
        this.extractionService = extractionService;
    }

    @PostMapping
    public ExtractionResponse extract(
            @RequestBody ExtractionRequest request) {

        List<MemoryCandidate> result = extractionService.extract(request.message());

        return new ExtractionResponse(result);    }

    public record ExtractionRequest(String message) {}

    public record ExtractionResponse(List<MemoryCandidate> result) {}}
