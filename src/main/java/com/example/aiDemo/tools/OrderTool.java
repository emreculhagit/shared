package com.example.aiDemo.tools;

import com.example.aiDemo.order.Order;
import com.example.aiDemo.order.OrderRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import org.springframework.ai.tool.annotation.ToolParam;

@Component
public class OrderTool {

    private final OrderRepository orderRepository;

    public OrderTool(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Tool(description = """
        Kullanıcının sipariş numarasını verdiği siparişin
        durumunu, müşteri adını ve tutarını veritabanından sorgular.
        Kullanıcı sipariş durumu, kargo veya sipariş bilgisi sorarsa
        mutlaka bu aracı kullan.
        """)
    public String getOrderStatus(
            @ToolParam(description = "Sorgulanacak sipariş numarası")
            Long orderId) {

        System.out.println("===== TOOL ÇAĞRILDI =====");
        System.out.println("Sipariş ID: " + orderId);

        return orderRepository.findById(orderId)
                .map(order -> "Sipariş No: " + order.getId()
                        + ", Müşteri: " + order.getCustomerName()
                        + ", Durum: " + order.getStatus()
                        + ", Tutar: " + order.getTotalAmount() + " TL")
                .orElse("Sipariş bulunamadı.");
    }
}