
package com.example.aiDemo.knowledge;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//@Component kapattık
public class EmbeddingMigrationRunner implements CommandLineRunner {

    private final KnowledgeChunkRepository repository;
    private final EmbeddingModel embeddingModel;

    public EmbeddingMigrationRunner(
            KnowledgeChunkRepository repository,
            EmbeddingModel embeddingModel) {
        this.repository = repository;
        this.embeddingModel = embeddingModel;
    }

    @Override
    @Transactional
    public void run(String... args) {
        List<KnowledgeChunk> chunks =
                repository.findAll().stream()
                        .filter(chunk ->
                                chunk.getEmbeddingJson() == null
                                        || chunk.getEmbeddingJson().isBlank())
                        .toList();

        int updated = 0;

        for (KnowledgeChunk chunk : chunks) {
            float[] vector =
                    embeddingModel.embed(chunk.getContent());

            chunk.setEmbeddingJson(
                    java.util.Arrays.toString(vector));

            updated++;
        }

        repository.saveAll(chunks);

        System.out.println(
                "Embedding migration tamamlandı. Güncellenen kayıt: "
                        + updated);
    }
}
