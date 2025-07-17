package shop.sajotuna.order.orders.rabbitmq;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class OrderEvent {
    private Long orderId;
    private Long userId;
}
