package shop.sajotuna.order.coupon.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.sajotuna.order.coupon.dto.request.CouponRequest;
import shop.sajotuna.order.coupon.dto.response.CouponResponse;
import shop.sajotuna.order.coupon.service.CouponService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/coupons")
public class CouponAdminController {

    private final CouponService couponService;

    // 모든 쿠폰 조회
    @GetMapping
    public ResponseEntity<List<CouponResponse>> getAllCoupons(){
        return ResponseEntity.ok(couponService.getAllCoupons());
    }

    // 쿠폰 생성
    @PostMapping
    public ResponseEntity<CouponResponse> createCoupon(@RequestBody @Valid CouponRequest couponRequest) {
        return ResponseEntity.ok(couponService.saveCoupon(couponRequest));
    }

    // 책 쿠폰 생성
    @PostMapping("/book")
    public ResponseEntity<CouponResponse> createBookCoupon(@RequestParam String isbn, @RequestBody @Valid CouponRequest couponRequest) {
        return ResponseEntity.ok(couponService.saveBookCoupon(isbn, couponRequest));
    }

    // 카테고리 쿠폰 생성
    @PostMapping("/category")
    public ResponseEntity<CouponResponse> createCategoryCoupon(@RequestParam Long categoryId, @RequestBody @Valid CouponRequest couponRequest) {
        return ResponseEntity.ok(couponService.saveCategoryCoupon(categoryId, couponRequest));
    }

    // 쿠폰 수정
    @PutMapping("/{coupon-id}")
    public ResponseEntity<Void> updateCoupon(@PathVariable(name = "coupon-id") Long couponId, @RequestBody @Valid CouponRequest couponRequest) {
        couponService.editCoupon(couponId, couponRequest);

        return ResponseEntity.noContent().build();
    }

    // 쿠폰 삭제
    @DeleteMapping("/{coupon-id}")
    public ResponseEntity<Void> deleteCouponById(@PathVariable(name = "coupon-id") Long couponId) {
        couponService.deleteCoupon(couponId);

        return ResponseEntity.noContent().build();
    }
}
