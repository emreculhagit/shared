
package com.example.aiDemo.memory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MemoryService {

    private final MemoryRepository memoryRepository;

    public MemoryService(MemoryRepository memoryRepository) {
        this.memoryRepository = memoryRepository;
    }

    // Mevcut davranış: aynı içerik varsa tekrar kaydetme.
    @Transactional
    public Memory remember(String userId, String content) {
        List<Memory> existingMemories =
                memoryRepository.findByUserIdAndContent(userId, content);

        if (!existingMemories.isEmpty()) {
            return existingMemories.get(0);
        }

        return memoryRepository.save(new Memory(userId, content));
    }

    // Yeni davranış: aynı kullanıcı ve key varsa içeriği güncelle.
    @Transactional
    public Memory remember(String userId, String key, String content) {
        Optional<Memory> existingMemory =
                memoryRepository.findByUserIdAndKey(userId, key);

        if (existingMemory.isPresent()) {
            Memory memory = existingMemory.get();
            memory.setContent(content);
            return memoryRepository.save(memory);
        }

        return memoryRepository.save(new Memory(userId, key, content));
    }

    @Transactional(readOnly = true)
    public List<Memory> getMemories(String userId) {
        return memoryRepository.findByUserId(userId);
    }
}
