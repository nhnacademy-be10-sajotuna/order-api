package shop.sajotuna.order.coupon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import shop.sajotuna.order.coupon.domain.Coupon;
import shop.sajotuna.order.coupon.domain.CouponType;

@Data
@AllArgsConstructor
public class CouponResponse {
    private Long id;
    private String name;
    private CouponType type;
    private Integer discountAmount;
    private Integer minOrderAmount;
    private Integer maxDiscountAmount;
    private Integer validDays;

    public static CouponResponse from(Coupon coupon) {
        return new CouponResponse(coupon.getId(), coupon.getName(), coupon.getType(), coupon.getDiscountAmount(), coupon.getMinOrderAmount(), coupon.getMaxDiscountAmount(), coupon.getValidDays());
    }
}
