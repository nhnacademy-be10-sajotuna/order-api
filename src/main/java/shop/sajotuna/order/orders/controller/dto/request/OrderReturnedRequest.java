package shop.sajotuna.order.orders.controller.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderReturnedRequest {
    @NotNull
    private Long userId;
}
