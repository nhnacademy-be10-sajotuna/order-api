package shop.sajotuna.order.coupon.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.sajotuna.order.coupon.domain.Coupon;
import shop.sajotuna.order.coupon.domain.UserCoupon;
import shop.sajotuna.order.coupon.domain.UserCouponType;
import shop.sajotuna.order.coupon.dto.UserCouponRequest;
import shop.sajotuna.order.coupon.dto.UserCouponResponse;
import shop.sajotuna.order.coupon.repository.UserCouponRepository;
import shop.sajotuna.order.coupon.repository.CouponRepository;
import shop.sajotuna.order.coupon.exception.CouponNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserCouponService {
    private final UserCouponRepository userCouponRepository;
    private final CouponRepository couponRepository;

    // 유저가 가진 쿠폰 목록 조회
    public List<UserCouponResponse> getUserCoupons(Long userId) {
        List<UserCoupon> userCoupons = userCouponRepository.findByUserId(userId);

        return userCoupons.stream().map(UserCouponResponse::from).collect(Collectors.toList());
    }

    // 유저 쿠폰 생성
    public UserCouponResponse saveUserCoupon(UserCouponRequest userCouponRequest) {
        Coupon coupon = couponRepository.findById(userCouponRequest.getCouponId()).orElseThrow(CouponNotFoundException::new);

        UserCoupon userCoupon = userCouponRepository.save(new UserCoupon(coupon, userCouponRequest.getUserId(), userCouponRequest.getIssuedAt(), coupon.getValidDays()));

        return UserCouponResponse.from(userCoupon);
    }

    // 유저 쿠폰 내역 변경
    @Transactional
    public void updateUserCoupon(Long userCouponId, UserCouponType userCouponType) {
        UserCoupon userCoupon = userCouponRepository.findById(userCouponId).orElseThrow(CouponNotFoundException::new);
        userCoupon.setType(userCouponType);
    }

    @Transactional
    public int useCoupon(Long userCouponId, int totalPrice) {
        UserCoupon userCoupon = userCouponRepository.findById(userCouponId).orElseThrow(CouponNotFoundException::new);
        userCoupon.useCoupon();
        return userCoupon.getCoupon().calculateDiscount(totalPrice);
    }
}
