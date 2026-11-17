package com.example.aiDemo.knowledge;

import java.util.List;

public class RagGoldenDataset {

    public static List<RagEvaluationCase> getCases() {

        return List.of(
                new RagEvaluationCase(
                        "RAG nedir?",
                        List.of("rag-dokumani.txt")
                ),
                new RagEvaluationCase(
                        "Embedding nedir?",
                        List.of("embedding-dokumani.txt")
                ),
                new RagEvaluationCase(
                        "Spring Boot nedir?",
                        List.of("spring-boot-dokumani.txt")
                )
        );
    }
}