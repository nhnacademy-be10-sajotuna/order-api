package shop.sajotuna.order.coupon.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import shop.sajotuna.order.coupon.repository.CouponRepository;

@RequiredArgsConstructor
@RestController
public class CouponController {
    private final CouponRepository couponRepository;
}
