
package com.example.aiDemo.controller;

import com.example.aiDemo.memory.Memory;
import com.example.aiDemo.memory.MemoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/memories")
public class MemoryController {

    private final MemoryService memoryService;

    public MemoryController(MemoryService memoryService) {
        this.memoryService = memoryService;
    }

    @PostMapping("/{userId}")
    public Memory saveMemory(
            @PathVariable String userId,
            @RequestBody MemoryRequest request) {

        return memoryService.remember(userId, request.content());
    }

    @GetMapping("/{userId}")
    public List<Memory> getMemories(@PathVariable String userId) {
        return memoryService.getMemories(userId);
    }

    public record MemoryRequest(String content) {
    }
}
