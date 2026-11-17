package com.example.aiDemo.memory;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface MemoryRepository extends JpaRepository<Memory, Long> {

    List<Memory> findByUserId(String userId);

    List<Memory> findByUserIdAndContent(
            String userId,
            String content
    );

    Optional<Memory> findByUserIdAndKey(
            String userId,
            String key
    );
}