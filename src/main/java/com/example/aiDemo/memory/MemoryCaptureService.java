
package com.example.aiDemo.memory;

import org.springframework.stereotype.Service;

@Service
public class MemoryCaptureService {

    private final MemoryService memoryService;

    public MemoryCaptureService(MemoryService memoryService) {
        this.memoryService = memoryService;
    }

    public void capture(String message, String userId) {

        System.out.println("HAFIZA YAKALAMA MESAJI: " + message);
        if (message == null || message.isBlank()) {
            return;
        }



        String normalized = message.toLowerCase()
                .replace("ı", "i")
                .trim();

        if (normalized.contains("java")
                && (normalized.contains("öğreniyorum")
                || normalized.contains("öğrenmek istiyorum"))) {

            memoryService.remember(
                    userId,
                    "Kullanıcı Java öğreniyor."
            );
        }

        if (normalized.contains("spring boot")
                && (normalized.contains("öğreniyorum")
                || normalized.contains("öğrenmek istiyorum"))) {
            System.out.println("JAVA HAFIZASI KOŞULU EŞLEŞTİ");
            memoryService.remember(
                    userId,
                    "Kullanıcı Spring Boot öğreniyor."
            );
        }
    }
}
