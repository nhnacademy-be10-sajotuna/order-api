package shop.sajotuna.order.coupon.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.sajotuna.order.coupon.dto.response.CouponResponse;
import shop.sajotuna.order.coupon.service.CouponService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coupons")
public class CouponController {
    private final CouponService couponService;

    // 쿠폰 조회
    @GetMapping("/{coupon-id}")
    public ResponseEntity<CouponResponse> getCouponById(@PathVariable(name = "coupon-id") Long couponId) {
        return ResponseEntity.ok(couponService.findCoupon(couponId));
    }
}
