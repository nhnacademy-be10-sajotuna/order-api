package shop.sajotuna.order.coupon.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shop.sajotuna.order.coupon.exception.CouponNotFoundException;
import shop.sajotuna.order.coupon.repository.CategoryCouponRepository;
import shop.sajotuna.order.coupon.repository.UserCouponRepository;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class CategoryCouponValidator {

    private final CategoryCouponRepository categoryCouponRepository;

    public void validateCoupon(Long couponId, Set<Long> categoryIds) {
        if (!categoryCouponRepository.existsByCouponIdAndCategoryIdIn(couponId, categoryIds)) {
            throw new CouponNotFoundException(couponId);
        }
    }
}
