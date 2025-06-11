package shop.sajotuna.order.coupon.dto;

import shop.sajotuna.order.coupon.domain.CouponType;

public class CouponRequest {
    private String name;
    private CouponType type;
    private Integer discountAmount;
    private Integer minOrderAmount;
    private Integer maxDiscountAmount;
    private Integer validDays;
}
