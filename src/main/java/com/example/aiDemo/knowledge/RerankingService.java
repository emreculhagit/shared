
package com.example.aiDemo.knowledge;

import com.example.aiDemo.knowledge.KnowledgeChunk;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class RerankingService {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    public RerankingService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
        this.objectMapper = new ObjectMapper();
    }

    public List<KnowledgeChunk> rerank(
            String question,
            List<KnowledgeChunk> chunks,
            int topK) {

        if (chunks == null || chunks.isEmpty()) {
            return List.of();
        }

        long rerankStart = System.nanoTime();

        try {
            StringBuilder context = new StringBuilder();

            for (KnowledgeChunk chunk : chunks) {
                context.append("ID: ")
                        .append(chunk.getId())
                        .append("\nİçerik: ")
                        .append(chunk.getContent())
                        .append("\n\n");
            }

            String prompt = """
                    Sen bir RAG reranking uzmanısın.

                    Görevin, verilen doküman parçalarını kullanıcının
                    sorusuyla ilgililiklerine göre en alakalıdan
                    en az alakalıya doğru sıralamaktır.

                    Kullanıcının sorusu:
                    %s

                    Doküman parçaları:
                    %s

                    Kurallar:
                    - Sadece verilen ID değerlerini kullan.
                    - En alakalı parçanın ID'si ilk sırada olsun.
                    - En fazla %d ID döndür.
                    - Sadece JSON dizisi döndür.
                    - Açıklama veya Markdown kullanma.

                    Örnek çıktı:
                    [33, 34]
                    """.formatted(
                    question,
                    context,
                    topK
            );

            String result = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();

            long rerankTime =
                    (System.nanoTime() - rerankStart) / 1_000_000;

            System.out.println("===== RERANKING PERFORMANCE =====");
            System.out.println("Reranking Qwen süresi: " + rerankTime + " ms");
            System.out.println("=================================");

            System.out.println("===== RERANKING RESULT =====");
            System.out.println(result);
            System.out.println("============================");

            if (result == null || result.isBlank()) {
                return chunks.stream()
                        .limit(topK)
                        .toList();
            }

            // JSON dizisinin başlangıç ve bitişini bul.
            int start = result.indexOf("[");
            int end = result.lastIndexOf("]");

            if (start == -1 || end == -1 || end < start) {
                System.out.println("Reranking JSON dizisi bulunamadı.");
                return chunks.stream()
                        .limit(topK)
                        .toList();
            }

            String jsonArray = result.substring(start, end + 1);

            // Hem sayı hem de metin ID formatını destekle.
            JsonNode root = objectMapper.readTree(jsonArray);

            List<Long> rankedIds = new ArrayList<>();

            if (root.isArray()) {
                for (JsonNode node : root) {
                    try {
                        rankedIds.add(Long.parseLong(node.asText()));
                    } catch (NumberFormatException e) {
                        System.out.println(
                                "Geçersiz reranking ID: " + node
                        );
                    }
                }
            }

            if (rankedIds.isEmpty()) {
                return chunks.stream()
                        .limit(topK)
                        .toList();
            }

            // Qwen'in sıralamasına göre parçaları yeniden sırala.
            List<KnowledgeChunk> rerankedChunks = chunks.stream()
                    .filter(chunk -> rankedIds.contains(chunk.getId()))
                    .sorted(Comparator.comparingInt(
                            chunk -> rankedIds.indexOf(chunk.getId())
                    ))
                    .limit(topK)
                    .toList();

            if (rerankedChunks.isEmpty()) {
                return chunks.stream()
                        .limit(topK)
                        .toList();
            }

            return rerankedChunks;

        } catch (Exception e) {
            System.out.println("Reranking hatası: " + e.getMessage());

            return chunks.stream()
                    .limit(topK)
                    .toList();
        }
    }
}
