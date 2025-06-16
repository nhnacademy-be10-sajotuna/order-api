package shop.sajotuna.order.orders.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import shop.sajotuna.order.orders.entity.OrderStatus;

@Data
public class OrderProductUpdateRequest {
    @NotNull
    OrderStatus status;
}
