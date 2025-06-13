package shop.sajotuna.order.orders.dto;

import lombok.Data;
import shop.sajotuna.order.orders.entity.OrderStatus;

@Data
public class OrderProductUpdateRequest {
    OrderStatus status;
}
