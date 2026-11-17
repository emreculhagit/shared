
package com.example.aiDemo.conversation;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConversationService {

    private final ConversationMessageRepository repository;

    public ConversationService(
            ConversationMessageRepository repository
    ) {
        this.repository = repository;
    }

    public List<Message> getHistory(String userId) {
        List<ConversationMessage> messages =
                repository.findByUserIdOrderByCreatedAtDesc(
                        userId,
                        PageRequest.of(0, 20)
                );

        return messages.stream()
                .sorted((a, b) ->
                        a.getCreatedAt().compareTo(b.getCreatedAt()))
                .map(message -> {
                    if ("user".equals(message.getRole())) {
                        return (Message) new UserMessage(
                                message.getContent()
                        );
                    }

                    return (Message) new AssistantMessage(
                            message.getContent()
                    );
                })
                .toList();
    }

    public void saveMessage(
            String userId,
            String role,
            String content
    ) {
        repository.save(
                new ConversationMessage(userId, role, content)
        );
    }
}
