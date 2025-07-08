package shop.sajotuna.order.coupon.dto.response;

import lombok.Builder;
import lombok.Getter;
import shop.sajotuna.order.coupon.domain.UserCoupon;
import shop.sajotuna.order.coupon.domain.UserCouponType;
import java.time.LocalDateTime;

@Getter
@Builder
public class UserCouponResponse {
    private Long userCouponId;
    private LocalDateTime issuedAt;
    private LocalDateTime expiresDate;
    private UserCouponType userCouponType;

    public static UserCouponResponse from(UserCoupon userCoupon) {
        return UserCouponResponse.builder()
                .userCouponId(userCoupon.getId())
                .issuedAt(userCoupon.getIssuedAt())
                .expiresDate(userCoupon.getExpiresAt())
                .userCouponType(userCoupon.getType())
                .build();
    }
}
