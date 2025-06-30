package shop.sajotuna.order.orders.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import shop.sajotuna.order.orders.domain.Order;
import shop.sajotuna.order.orders.domain.OrderStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
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
        
        return new OrderInfoResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getFinalPrice().getAmount(),
                order.getStatus(),
                order.getCreatedAt(),
                firstProductIsbn,
                totalProductCount
        );
    }
}
