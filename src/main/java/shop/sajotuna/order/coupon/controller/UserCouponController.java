package shop.sajotuna.order.coupon.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.sajotuna.order.coupon.service.CouponQueueService;
import shop.sajotuna.order.coupon.dto.request.UserCouponRequest;
import shop.sajotuna.order.coupon.dto.response.UserCouponDetailResponse;
import shop.sajotuna.order.coupon.dto.response.UserCouponResponse;
import shop.sajotuna.order.coupon.dto.request.WelcomeCouponRequest;
import shop.sajotuna.order.coupon.service.UserCouponService;
import shop.sajotuna.order.coupon.service.dto.event.CouponEvent;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coupons/users")
public class UserCouponController {
    private final UserCouponService userCouponService;
    private final CouponQueueService couponQueueService;

    // 유저가 가진 쿠폰 목록 조회
    @GetMapping
    public ResponseEntity<List<UserCouponDetailResponse>> getUserCoupons(@RequestHeader(name = "X-User-Id") Long userId) {
        return ResponseEntity.ok(userCouponService.getUserCoupons(userId));
    }

    // 유저 쿠폰 발급
    @PostMapping
    public ResponseEntity<UserCouponResponse> createUserCoupon(@RequestBody @Valid UserCouponRequest userCouponRequest) {
        return ResponseEntity.ok(userCouponService.saveUserCoupon(userCouponRequest));
    }

    @PostMapping("/issue-welcome")
    public ResponseEntity<UserCouponResponse> issueWelcomeCoupon(@RequestBody @Valid WelcomeCouponRequest welcomeCouponRequest) {
        return ResponseEntity.ok(userCouponService.issueWelcomeCoupon(welcomeCouponRequest.getUserId()));
    }

    @PostMapping("/request-issue")
    public ResponseEntity<Void> requestUserCoupon(@RequestBody @Valid CouponEvent couponEvent) {
        couponQueueService.sendIssueCouponMessage(couponEvent);
        return ResponseEntity.ok().build();
    }
}