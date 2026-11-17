package com.example.aiDemo.order;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class OrderDataInitializer implements CommandLineRunner {

    private final OrderRepository orderRepository;

    public OrderDataInitializer(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public void run(String... args) {

        if (orderRepository.count() == 0) {

            orderRepository.save(new Order(
                    8472L, "Ahmet Yılmaz", "HAZIRLANIYOR", 1250
            ));

            orderRepository.save(new Order(
                    8473L, "Mehmet Demir", "KARGODA", 2400
            ));

            orderRepository.save(new Order(
                    8474L, "Ayşe Kaya", "TESLİM EDİLDİ", 850
            ));
        }
    }
}