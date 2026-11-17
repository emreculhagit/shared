
package com.example.aiDemo.knowledge;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class KnowledgeService {

    private final KnowledgeChunkRepository repository;
    private final EmbeddingModel embeddingModel;


    public KnowledgeService(KnowledgeChunkRepository repository,EmbeddingModel embeddingModel) {
        this.repository = repository;
        this.embeddingModel = embeddingModel;

    }

    @Transactional
    public int addDocument(String documentName, String text) {
        if (documentName == null || documentName.isBlank()) {
            throw new IllegalArgumentException("Doküman adı boş olamaz.");
        }

        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Doküman içeriği boş olamaz.");
        }

        int chunkSize = 300;
        int overlap = 50;
        List<KnowledgeChunk> chunks = new ArrayList<>();

        for (int start = 0, index = 0;
             start < text.length();
             start += chunkSize - overlap, index++) {

            int end = Math.min(start + chunkSize, text.length());
            String content = text.substring(start, end).trim();

            if (!content.isEmpty()) {
                // 1. Metni embedding vektörüne dönüştür.
                float[] vector = embeddingModel.embed(content);

                // 2. Vektörü JSON benzeri bir metne dönüştür.
                String embeddingJson = java.util.Arrays.toString(vector);

                // 3. Metin ve vektörü aynı entity içinde sakla.
                KnowledgeChunk chunk =
                        new KnowledgeChunk(documentName, index, content);

                chunk.setEmbeddingJson(embeddingJson);
                chunks.add(chunk);
            }
        }

        repository.saveAll(chunks);

        System.out.println(
                "Kaydedilen parça sayısı: " + chunks.size()
        );

        return chunks.size();
    }

    public List<KnowledgeChunk> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return List.of();
        }

        return repository.findByContentContainingIgnoreCase(keyword);
    }

    public List<KnowledgeChunk> searchByKeywords(String question) {
        if (question == null || question.isBlank()) {
            return List.of();
        }

        String[] keywords = question
                .toLowerCase(java.util.Locale.ROOT)
                .replaceAll("[^\\p{L}\\p{N}\\s-]", " ")
                .split("\\s+");


        java.util.Set<String> stopWords = java.util.Set.of(
                "ne", "nedir", "nasil", "nasıl",
                "icin", "için", "bir", "ve",
                "mi", "mu", "mı", "mü",
                "ise", "gibi", "hakkinda",
                "hakkında", "ilgili", "yaziyor",
                "yazıyor", "hangi", "neresi",
                "neresidir", "turkiye", "türkiye"
        );

        java.util.Map<Long, KnowledgeChunk> results =
                new java.util.LinkedHashMap<>();

        for (String keyword : keywords) {
            String normalizedKeyword = keyword.replace('ı', 'i');

            if (normalizedKeyword.length() < 3
                    || stopWords.contains(keyword)) {
                continue;
            }

            for (KnowledgeChunk chunk :
                    repository.findByContentContainingIgnoreCase(normalizedKeyword)) {
                results.putIfAbsent(chunk.getId(), chunk);
            }
        }

        return List.copyOf(results.values());
    }

    private double cosineSimilarity(float[] vectorA, float[] vectorB) {

        if (vectorA.length != vectorB.length) {
            throw new IllegalArgumentException(
                    "Vektör boyutları eşleşmiyor."
            );
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];

            normA += Math.pow(vectorA[i], 2);
            normB += Math.pow(vectorB[i], 2);
        }

        if (normA == 0 || normB == 0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    private float[] parseEmbedding(String embeddingJson) {

        if (embeddingJson == null || embeddingJson.isBlank()) {
            return new float[0];
        }

        String cleaned = embeddingJson
                .replace("[", "")
                .replace("]", "");

        String[] values = cleaned.split(",");

        float[] vector = new float[values.length];

        for (int i = 0; i < values.length; i++) {
            vector[i] = Float.parseFloat(values[i].trim());
        }

        return vector;
    }


    public List<KnowledgeChunk> searchBySimilarity(
            String question,
            int topK) {

        if (question == null || question.isBlank()) {
            return List.of();
        }

        long totalStart = System.nanoTime();

        // 1. Soru embedding süresi
        long embeddingStart = System.nanoTime();
        float[] questionVector = embeddingModel.embed(question);
        long embeddingTime =
                (System.nanoTime() - embeddingStart) / 1_000_000;

        // 2. Veritabanından kayıtları çekme süresi
        long dbStart = System.nanoTime();
        List<KnowledgeChunk> chunks = repository.findAll();
        long dbTime =
                (System.nanoTime() - dbStart) / 1_000_000;

        double threshold = 0.50;
        // 3. Benzerlik hesaplama süresi
        long searchStart = System.nanoTime();

        List<KnowledgeChunk> results = chunks.stream()
                .filter(chunk ->
                        chunk.getEmbeddingJson() != null
                                && !chunk.getEmbeddingJson().isBlank())
                .map(chunk -> {
                    float[] documentVector =
                            parseEmbedding(chunk.getEmbeddingJson());

                    double score = cosineSimilarity(
                            questionVector,
                            documentVector
                    );

                    System.out.println(
                            "Doküman: " + chunk.getDocumentName()
                                    + " | Benzerlik: " + score
                    );

                    System.out.println("Parça içeriği: " + chunk.getContent());

                    return new java.util.AbstractMap.SimpleEntry<>(
                            chunk, score
                    );
                })
                .filter(entry -> entry.getValue() >= threshold)                .sorted((a, b) ->
                        Double.compare(b.getValue(), a.getValue()))
                .limit(topK)
                .map(java.util.Map.Entry::getKey)
                .toList();

        long searchTime =
                (System.nanoTime() - searchStart) / 1_000_000;

        long totalTime =
                (System.nanoTime() - totalStart) / 1_000_000;

        System.out.println("===== RAG PERFORMANCE =====");
        System.out.println("Embedding: " + embeddingTime + " ms");
        System.out.println("H2 veritabanı: " + dbTime + " ms");
        System.out.println("Benzerlik araması: " + searchTime + " ms");
        System.out.println("Toplam RAG: " + totalTime + " ms");
        System.out.println("Toplam parça sayısı: " + chunks.size());
        System.out.println("Bulunan parça sayısı: " + results.size());
        System.out.println("============================");

        return results;
    }

    public List<KnowledgeChunk> searchHybrid(String question, int topK) {

        if (question == null || question.isBlank()) {
            return List.of();
        }

        // 1. Kelime bazlı arama
        List<KnowledgeChunk> keywordResults =
                searchByKeywords(question);

        // 2. Anlamsal arama
        List<KnowledgeChunk> semanticResults =
                searchBySimilarity(question, topK);

        // 3. İki aramanın sonuçlarını birleştir
        java.util.Map<Long, KnowledgeChunk> combined =
                new java.util.LinkedHashMap<>();

        // Önce keyword sonuçlarını ekle
        for (KnowledgeChunk chunk : keywordResults) {
            combined.putIfAbsent(chunk.getId(), chunk);
        }

        // Ardından semantic sonuçlarını ekle
        for (KnowledgeChunk chunk : semanticResults) {
            combined.putIfAbsent(chunk.getId(), chunk);
        }

        // 4. Tekrarlanan kayıtları çıkar ve Top-K uygula
        return combined.values()
                .stream()
                .limit(topK)
                .toList();
    }

}
