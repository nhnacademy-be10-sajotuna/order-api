package shop.sajotuna.order.coupon.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import shop.sajotuna.order.coupon.domain.CouponType;
import shop.sajotuna.order.coupon.domain.UserCouponType;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class UserCouponResponse {
    private Long userCouponId;
    private String name;
    private CouponType type;
    private Integer discountAmount;
    private Integer minOrderAmount;
    private Integer maxDiscountAmount;
    private LocalDate issuedAt;
    private LocalDate expiresDate;
    private UserCouponType UserCoupontype;


}
