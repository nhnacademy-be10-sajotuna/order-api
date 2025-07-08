package shop.sajotuna.order.coupon.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WelcomeCouponRequest {
    @NotNull
    private Long userId;
}
