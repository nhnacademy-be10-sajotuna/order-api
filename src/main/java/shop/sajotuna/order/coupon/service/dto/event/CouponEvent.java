package shop.sajotuna.order.coupon.service.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CouponEvent {
    private Long userId;
    private Long couponId;
}
