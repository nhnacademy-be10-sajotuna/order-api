package shop.sajotuna.order.coupon.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import shop.sajotuna.order.coupon.domain.Coupon;
import shop.sajotuna.order.coupon.domain.CouponPolicyType;
import shop.sajotuna.order.coupon.domain.CouponType;

@Getter
@Builder
@AllArgsConstructor
public class CouponResponse {
    private Long id;
    private String name;
    private CouponType couponType;
    private CouponPolicyType policyType;
    private Integer discountAmount;
    private Integer minOrderAmount;
    private Integer maxDiscountAmount;
    private Integer validDays;

    public static CouponResponse from(Coupon coupon) {
        return CouponResponse.builder()
                .id(coupon.getId())
                .name(coupon.getName())
                .couponType(coupon.getCouponType())
                .policyType(coupon.getPolicyType())
                .discountAmount(coupon.getDiscountAmount())
                .minOrderAmount(coupon.getMinOrderAmount().getAmount())
                .maxDiscountAmount(coupon.getMaxDiscountAmount().getAmount())
                .validDays(coupon.getValidDays())
                .build();
    }
}
