
package com.example.aiDemo.conversation;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConversationMessageRepository
        extends JpaRepository<ConversationMessage, Long> {

    List<ConversationMessage>
    findByUserIdOrderByCreatedAtAsc(String userId);

    List<ConversationMessage> findByUserIdOrderByCreatedAtDesc(
            String userId,
            Pageable pageable
    );
}
