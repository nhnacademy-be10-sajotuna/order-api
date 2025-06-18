package shop.sajotuna.order.orders.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderReturnedRequest {
    @NotNull
    private Long userId;
}
