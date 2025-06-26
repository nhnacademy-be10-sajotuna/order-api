package shop.sajotuna.order.orders.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import shop.sajotuna.order.orders.domain.Order;
import shop.sajotuna.order.orders.domain.OrderStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class OrderResponse {
    private Long orderId;
    private LocalDateTime shippingDate;
    private LocalDateTime createdAt;
    private int TotalPrice;
    private OrderStatus status;

    public static OrderResponse from(Order order) {
        return new OrderResponse(order.getId(), order.getShippingDate(), order.getCreatedAt(), order.getTotalPrice(), order.getStatus());
    }
}
