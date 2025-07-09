package shop.sajotuna.order.coupon.dto.response;

import lombok.Builder;
import lombok.Getter;
import shop.sajotuna.order.coupon.domain.*;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserCouponDetailResponse {
    private Long userCouponId;
    private LocalDateTime issuedAt;
    private LocalDateTime expiresAt;
    private UserCouponType status;

    // 쿠폰 기본 정보
    private String couponName;
    private CouponType couponType;
    private CouponPolicyType policyType;
    private Integer discountAmount;
    private Integer minOrderAmount;
    private Integer maxDiscountAmount;

    public static UserCouponDetailResponse from(UserCoupon userCoupon, Coupon coupon) {
        return UserCouponDetailResponse.builder()
                .userCouponId(userCoupon.getId())
                .issuedAt(userCoupon.getIssuedAt())
                .expiresAt(userCoupon.getExpiresAt())
                .status(userCoupon.getType())
                .couponName(coupon.getName())
                .couponType(coupon.getCouponType())
                .policyType(coupon.getPolicyType())
                .discountAmount(coupon.getDiscountAmount())
                .minOrderAmount(coupon.getMinOrderAmount().getAmount())
                .maxDiscountAmount(coupon.getMaxDiscountAmount().getAmount())
                .build();
    }
}
