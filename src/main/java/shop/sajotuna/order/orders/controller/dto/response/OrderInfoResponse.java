package shop.sajotuna.order.orders.controller.dto.response;

import lombok.Builder;
import lombok.Getter;
import shop.sajotuna.order.orders.domain.Order;
import shop.sajotuna.order.orders.domain.OrderStatus;

import java.time.LocalDateTime;

@Getter
@Builder
public class OrderInfoResponse {
    private Long orderId;
    private String orderNumber;
    private int finalPrice;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private String firstProductIsbn;
    private int totalProductCount;

    public static OrderInfoResponse from(Order order) {
        String firstProductIsbn = order.getOrderProducts().getFirst().getIsbn();
        
        int totalProductCount = order.getOrderProducts().size();
        
        return OrderInfoResponse.builder()
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .finalPrice(order.getFinalPrice().getAmount())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .firstProductIsbn(firstProductIsbn)
                .totalProductCount(totalProductCount)
                .build();
    }
}
