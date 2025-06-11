package shop.sajotuna.order.coupon.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import shop.sajotuna.order.coupon.domain.CouponType;
import shop.sajotuna.order.coupon.domain.UserCouponType;
import java.time.LocalDateTime;

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
    private LocalDateTime issuedAt;
    private LocalDateTime expiresDate;
    private UserCouponType UserCoupontype;


}
