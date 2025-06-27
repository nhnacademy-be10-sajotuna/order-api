package shop.sajotuna.order.coupon.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.sajotuna.order.coupon.dto.BookInfo;
import shop.sajotuna.order.coupon.dto.CouponResponse;
import shop.sajotuna.order.coupon.service.UserCouponService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coupons/available-coupons")
public class AvailableCouponController {

    private final UserCouponService userCouponService;

    // 사용 가능한 북 쿠폰 조회
    @GetMapping("/book")
    public ResponseEntity<List<CouponResponse>> getAvailableBookCoupons(@RequestHeader("X-User-Id") Long userId, BookInfo bookInfo) {
        List<CouponResponse> availableCoupons = userCouponService.getAvailableCoupons(userId,bookInfo);
        return ResponseEntity.ok(availableCoupons);
    }

    // 사용 가능한 오더 쿠폰 조회
    @GetMapping("/order")
    public ResponseEntity<List<CouponResponse>> getAvailableOrderCoupons(@RequestHeader("X-User-Id") Long userId){
        List<CouponResponse> availableCoupons = userCouponService.getAvailableOrderCoupons(userId, 10);
        return ResponseEntity.ok(availableCoupons);
    }
}
