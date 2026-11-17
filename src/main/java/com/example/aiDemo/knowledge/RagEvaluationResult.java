package com.example.aiDemo.knowledge;

public record RagEvaluationResult(
        double precision,
        double recall,
        double f1Score
) {
}