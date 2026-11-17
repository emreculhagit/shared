package com.example.aiDemo.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class EmbeddingTestRunner implements CommandLineRunner {

    private final EmbeddingModel embeddingModel;

    public EmbeddingTestRunner(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    @Override
    public void run(String... args) {
        float[] vector = embeddingModel.embed(
                "Spring Data JPA veritabanı işlemlerini kolaylaştırır."
        );

        System.out.println("=== EMBEDDING TEST ===");
        System.out.println("Vektör boyutu: " + vector.length);
        System.out.println("======================");
    }
}