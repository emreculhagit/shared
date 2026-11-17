
package com.example.aiDemo.knowledge;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "knowledge_chunks")
public class KnowledgeChunk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String documentName;

    private Integer chunkIndex;

    @Column(length = 10000)
    private String content;

    // Embedding vektörünü metin olarak saklayacağız.
    @Column(length = 20000)
    private String embeddingJson;

    private LocalDateTime createdAt;

    public KnowledgeChunk() {
    }

    public KnowledgeChunk(
            String documentName,
            Integer chunkIndex,
            String content) {
        this.documentName = documentName;
        this.chunkIndex = chunkIndex;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getDocumentName() {
        return documentName;
    }

    public Integer getChunkIndex() {
        return chunkIndex;
    }

    public String getContent() {
        return content;
    }

    public String getEmbeddingJson() {
        return embeddingJson;
    }

    public void setEmbeddingJson(String embeddingJson) {
        this.embeddingJson = embeddingJson;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
