
package com.example.aiDemo.memory;

import jakarta.persistence.*;

@Entity
@Table(name = "memories")
public class Memory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    private String content;

    @Column(name = "memory_key")
    private String key;

    public Memory() {
    }

    public Memory(String userId, String content) {
        this.userId = userId;
        this.content = content;
    }

    public Memory(String userId, String key, String content) {
        this.userId = userId;
        this.key = key;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
