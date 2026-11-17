
package com.example.aiDemo.knowledge;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RagEvaluationService {

    public double calculatePrecision(
            int relevantCount,
            int retrievedCount) {

        if (retrievedCount == 0) {
            return 0.0;
        }

        return (double) relevantCount / retrievedCount;
    }

    public double calculateRecall(
            int relevantRetrievedCount,
            int totalRelevantCount) {

        if (totalRelevantCount == 0) {
            return 0.0;
        }

        return (double) relevantRetrievedCount / totalRelevantCount;
    }

    public double calculateF1(
            double precision,
            double recall) {

        if (precision + recall == 0) {
            return 0.0;
        }

        return 2 * (precision * recall) / (precision + recall);
    }

    public RagEvaluationResult evaluate(
            int relevantRetrievedCount,
            int retrievedCount,
            int totalRelevantCount) {

        double precision = calculatePrecision(
                relevantRetrievedCount,
                retrievedCount
        );

        double recall = calculateRecall(
                relevantRetrievedCount,
                totalRelevantCount
        );

        double f1Score = calculateF1(
                precision,
                recall
        );

        return new RagEvaluationResult(
                precision,
                recall,
                f1Score
        );
    }

    public RagEvaluationResult evaluateSearch(
            List<KnowledgeChunk> retrievedChunks,
            List<String> expectedDocumentNames,
            int topK) {

        if (retrievedChunks == null
                || expectedDocumentNames == null
                || topK <= 0) {

            return new RagEvaluationResult(0.0, 0.0, 0.0);
        }

        // Top-K sonuçlarını al.
        List<KnowledgeChunk> topResults = retrievedChunks.stream()
                .limit(topK)
                .toList();

        // Getirilen benzersiz dokümanları bul.
        List<String> retrievedDocuments = topResults.stream()
                .map(KnowledgeChunk::getDocumentName)
                .distinct()
                .toList();

        // Beklenen dokümanlardan kaç tanesi bulundu?
        long relevantCount = retrievedDocuments.stream()
                .filter(expectedDocumentNames::contains)
                .count();

        double precision = calculatePrecision(
                (int) relevantCount,
                retrievedDocuments.size()
        );

        double recall = calculateRecall(
                (int) relevantCount,
                expectedDocumentNames.size()
        );

        double f1Score = calculateF1(
                precision,
                recall
        );

        System.out.println("===== RAG EVALUATION =====");
        System.out.println("Beklenen doküman: "
                + expectedDocumentNames.size());
        System.out.println("Bulunan ilgili doküman: "
                + relevantCount);
        System.out.println("Getirilen benzersiz doküman: "
                + retrievedDocuments.size());
        System.out.println("Precision: " + precision);
        System.out.println("Recall: " + recall);
        System.out.println("F1 Score: " + f1Score);
        System.out.println("==========================");

        return new RagEvaluationResult(
                precision,
                recall,
                f1Score
        );
    }
}
