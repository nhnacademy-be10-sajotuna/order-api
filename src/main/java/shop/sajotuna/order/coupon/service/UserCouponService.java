package shop.sajotuna.order.coupon.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.sajotuna.order.coupon.domain.Coupon;
import shop.sajotuna.order.coupon.domain.UserCoupon;
import shop.sajotuna.order.coupon.dto.UserCouponResponse;
import shop.sajotuna.order.coupon.repository.UserCouponRepository;
import shop.sajotuna.order.coupon.repository.CouponRepository;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserCouponService {
    private final UserCouponRepository userCouponRepository;


    public List<UserCouponResponse> getUserCoupons(Long userId) {
        List<UserCoupon> userCoupons = userCouponRepository.findByUserId(userId);

        return userCoupons.stream()
                .map(userCoupon -> {
                    Coupon coupon = userCoupon.getCoupon();

                    return new UserCouponResponse(
                            userCoupon.getId(),
                            coupon.getName(),
                            coupon.getType(),
                            coupon.getDiscountAmount(),
                            coupon.getMinOrderAmount(),
                            coupon.getMaxDiscountAmount(),
                            userCoupon.getIssuedAt(),
                            userCoupon.getExpiresAt(),
                            userCoupon.getType()
                    );
                })
                .collect(Collectors.toList());
    }

}
