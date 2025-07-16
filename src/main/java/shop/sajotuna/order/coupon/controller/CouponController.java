package shop.sajotuna.order.coupon.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.sajotuna.order.coupon.dto.request.BookInfo;
import shop.sajotuna.order.coupon.dto.response.CouponResponse;
import shop.sajotuna.order.coupon.service.CouponService;

import java.util.List;

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

    @GetMapping("/book-coupons")
    public ResponseEntity<List<CouponResponse>> getBookCoupons(@ModelAttribute BookInfo bookInfo) {
        List<CouponResponse> coupons = couponService.findBookCoupons(bookInfo);
        return ResponseEntity.ok(coupons);
    }
}
