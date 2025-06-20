package shop.sajotuna.order.coupon.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.sajotuna.order.coupon.dto.UserCouponRequest;
import shop.sajotuna.order.coupon.dto.UserCouponResponse;
import shop.sajotuna.order.coupon.dto.WelcomeCouponRequest;
import shop.sajotuna.order.coupon.service.UserCouponService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coupons/users")
public class UserCouponController {
    private final UserCouponService userCouponService;

    // 유저가 가진 쿠폰 목록 조회
    @GetMapping
    public ResponseEntity<List<UserCouponResponse>> getUserCoupons(@RequestHeader(name = "X-User-Id") Long userId) {
        return ResponseEntity.ok(userCouponService.getUserCoupons(userId));
    }

    // 유저 쿠폰 발급
    @PostMapping
    public ResponseEntity<UserCouponResponse> createUserCoupon(@RequestBody @Valid UserCouponRequest userCouponRequest) {
        return ResponseEntity.ok(userCouponService.saveUserCoupon(userCouponRequest));
    }

    // 웰컴 쿠폰 발급
    @PostMapping("/issue-welcome")
    public ResponseEntity<UserCouponResponse> issueWelcomeCoupon(@RequestBody @Valid WelcomeCouponRequest welcomeCouponRequest) {
        return ResponseEntity.ok(userCouponService.issueWelcomeCoupon(welcomeCouponRequest.getUserId()));
    }

}