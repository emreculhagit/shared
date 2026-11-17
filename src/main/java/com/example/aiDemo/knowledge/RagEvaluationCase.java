package com.example.aiDemo.knowledge;

import java.util.List;

public record RagEvaluationCase(
        String question,
        List<String> expectedDocumentNames
) {
}