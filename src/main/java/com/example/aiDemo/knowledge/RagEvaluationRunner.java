
package com.example.aiDemo.knowledge;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RagEvaluationRunner {

    private final KnowledgeService knowledgeService;
    private final RagEvaluationService evaluationService;

    public RagEvaluationRunner(
            KnowledgeService knowledgeService,
            RagEvaluationService evaluationService) {

        this.knowledgeService = knowledgeService;
        this.evaluationService = evaluationService;
    }

    public void evaluateAll() {

        List<RagEvaluationCase> cases =
                RagGoldenDataset.getCases();

        System.out.println("===== RAG EVALUATION STARTED =====");

        for (RagEvaluationCase evaluationCase : cases) {

            System.out.println("\nSoru: "
                    + evaluationCase.question());

            // Gerçek Hybrid Search çalıştırılır.
            List<KnowledgeChunk> results =
                    knowledgeService.searchHybrid(
                            evaluationCase.question(),
                            3
                    );

            // Arama sonuçları değerlendirilir.
            evaluationService.evaluateSearch(
                    results,
                    evaluationCase.expectedDocumentNames(),
                    3
            );
        }

        System.out.println("\n===== RAG EVALUATION FINISHED =====");
    }
}
