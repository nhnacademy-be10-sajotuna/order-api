package shop.sajotuna.order.coupon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import shop.sajotuna.order.coupon.domain.UserCoupon;
import shop.sajotuna.order.coupon.domain.UserCouponType;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UserCouponResponse {
    private Long userCouponId;
    private LocalDateTime issuedAt;
    private LocalDateTime expiresDate;
    private UserCouponType userCouponType;

    public static UserCouponResponse from(UserCoupon userCoupon) {
        return new UserCouponResponse(userCoupon.getId(), userCoupon.getIssuedAt(), userCoupon.getExpiresAt(), userCoupon.getType());
    }
}
