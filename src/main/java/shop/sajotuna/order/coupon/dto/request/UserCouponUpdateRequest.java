package shop.sajotuna.order.coupon.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import shop.sajotuna.order.coupon.domain.UserCouponType;

@Data
public class UserCouponUpdateRequest {
    @NotNull
    private UserCouponType type;
}
