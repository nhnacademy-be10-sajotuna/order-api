package shop.sajotuna.order.coupon.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WelcomeCouponRequest {
    @NotNull
    private Long userId;
}
