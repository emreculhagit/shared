
package com.example.aiDemo.service;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemoryExtractionService {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    public MemoryExtractionService(
            ChatClient chatClient,
            ObjectMapper objectMapper) {
        this.chatClient = chatClient;
        this.objectMapper = objectMapper;
    }

    public List<MemoryCandidate> extract(String message) {

        String result = chatClient.prompt()

                .system("""
        Sen bir kullanıcı hafızası çıkarma bileşenisin.

        Kullanıcı mesajındaki açıkça belirtilmiş,
        gelecekte faydalı olabilecek kalıcı bilgileri çıkar.

        Kurallar:
        - Tahmin yapma, bilgi uydurma.
        - Geçici ve önemsiz ifadeleri çıkarma.
        - Birden fazla bilgi varsa ayrı nesneler oluştur.
        - Aynı türde bilgiler için her zaman aynı key'i kullan.
        - key İngilizce snake_case olmalı.
        - content yalnızca bilginin değerini içermeli.
        - Uygun bilgi yoksa boş JSON dizisi döndür.
        - Yalnızca geçerli JSON döndür.
        - Markdown veya açıklama ekleme.

        Örnek:
        Mesaj: Favori rengim mavi.
        Çıktı:
        [{"key":"favorite_color","content":"mavi"}]

        Mesaj: Artık favori rengim yeşil.
        Çıktı:
        [{"key":"favorite_color","content":"yeşil"}]

        Mesaj: Java öğreniyorum ve hedefim AI Developer olmak.
        Çıktı:
        [
          {"key":"learning_subject","content":"Java öğreniyor"},
          {"key":"learning_goal","content":"AI Developer olmak"}
        ]
        """)

                .user(message)
                .call()
                .content();
        System.out.println("HAFIZA ÇIKARMA HAM YANITI: " + result);

        try {
            String cleanedResult = result.trim();

            int start = cleanedResult.indexOf('[');

            if (start == -1) {
                System.err.println(
                        "Hafıza çıkarma için geçerli JSON dizisi bulunamadı. "
                                + "AI yanıtı: " + cleanedResult
                );

                return List.of();
            }

            int depth = 0;
            boolean inString = false;
            boolean escaped = false;
            int end = -1;

            for (int i = start; i < cleanedResult.length(); i++) {
                char c = cleanedResult.charAt(i);

                if (inString) {
                    if (escaped) {
                        escaped = false;
                    } else if (c == '\\') {
                        escaped = true;
                    } else if (c == '"') {
                        inString = false;
                    }
                } else {
                    if (c == '"') {
                        inString = true;
                    } else if (c == '[') {
                        depth++;
                    } else if (c == ']') {
                        depth--;

                        if (depth == 0) {
                            end = i;
                            break;
                        }
                    }
                }
            }

            if (end == -1) {
                throw new IllegalArgumentException(
                        "JSON dizisi tamamlanmamış."
                );
            }

            String json = cleanedResult.substring(start, end + 1);

            List<MemoryCandidate> memories = objectMapper.readValue(
                    json,
                    new TypeReference<List<MemoryCandidate>>() {}
            );

            if (memories == null) {
                return List.of();
            }

            return memories.stream()
                    .filter(memory -> memory != null)
                    .filter(memory -> memory.key() != null
                            && !memory.key().isBlank())
                    .filter(memory -> memory.content() != null
                            && !memory.content().isBlank())
                    .map(memory -> new MemoryCandidate(
                            memory.key().trim(),
                            memory.content().trim()
                    ))
                    .distinct()
                    .toList();

        } catch (Exception e) {
            System.err.println("QWEN'DEN GELEN HAM YANIT:");
            System.err.println(result);

            throw new IllegalStateException(
                    "AI hafıza sonucu geçerli bir JSON dizisi değil.",
                    e
            );
        }

    }
}
