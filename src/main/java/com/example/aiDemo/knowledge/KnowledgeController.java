
package com.example.aiDemo.knowledge;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/knowledge")
public class KnowledgeController {

    private final KnowledgeService knowledgeService;

    public KnowledgeController(KnowledgeService knowledgeService) {
        this.knowledgeService = knowledgeService;
    }

    @PostMapping
    public AddKnowledgeResponse addDocument(
            @RequestBody AddKnowledgeRequest request) {

        int chunkCount = knowledgeService.addDocument(
                request.documentName(),
                request.text()
        );

        return new AddKnowledgeResponse(
                "Doküman başarıyla kaydedildi.",
                chunkCount
        );
    }

    @GetMapping("/search")
    public List<KnowledgeChunk> search(
            @RequestParam String keyword) {

        return knowledgeService.search(keyword);
    }

    public record AddKnowledgeRequest(
            String documentName,
            String text
    ) {}

    public record AddKnowledgeResponse(
            String message,
            int chunkCount
    ) {}
}
