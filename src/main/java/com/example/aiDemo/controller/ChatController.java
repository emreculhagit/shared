
package com.example.aiDemo.controller;

import com.example.aiDemo.conversation.ConversationService;
import com.example.aiDemo.service.ChatService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.ai.chat.messages.Message;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;
    private final ConversationService conversationService;

    public ChatController(
            ChatService chatService,
            ConversationService conversationService
    ) {
        this.chatService = chatService;
        this.conversationService = conversationService;
    }

    @PostMapping
    public ChatResponse chat(@Valid @RequestBody ChatRequest request) {

        String userId = request.userId();
        String message = request.message();

        // 1. Önceki konuşmaları veritabanından getir
        List<Message> history =
                conversationService.getHistory(userId);

        // 2. Kullanıcının mesajını kaydet
        conversationService.saveMessage(
                userId,
                "user",
                message
        );

        // 3. AI cevabını üret
        String answer = chatService.ask(
                message,
                history,
                userId
        );

        // 4. AI cevabını kaydet
        conversationService.saveMessage(
                userId,
                "assistant",
                answer
        );

        // 5. Cevabı döndür
        return new ChatResponse(answer);
    }

    public record ChatRequest(
            @NotBlank(message = "Mesaj boş olamaz.")
            String message,

            @NotBlank(message = "Kullanıcı ID boş olamaz.")
            String userId,

            List<ChatMessage> history
    ) {}

    public record ChatMessage(String role, String content) {}

    public record ChatResponse(String answer) {}
}
