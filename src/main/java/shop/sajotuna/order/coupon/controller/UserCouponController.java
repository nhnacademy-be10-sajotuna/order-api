package shop.sajotuna.order.coupon.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import shop.sajotuna.order.coupon.dto.UserCouponResponse;
import shop.sajotuna.order.coupon.repository.UserCouponRepository;
import shop.sajotuna.order.coupon.service.UserCouponService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserCouponController {
    private final UserCouponService userCouponService;

    @GetMapping("/{userId}/coupons")
    public ResponseEntity<List<UserCouponResponse>> getUserCoupons(@PathVariable Long userId) {
        return ResponseEntity.ok(userCouponService.getUserCoupons(userId));
    }

}