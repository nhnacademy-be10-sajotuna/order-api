package shop.sajotuna.order.coupon.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.sajotuna.order.coupon.repository.CouponRepository;
import shop.sajotuna.order.coupon.service.CouponService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/order")
public class CouponController {
    private final CouponService couponService;
}
