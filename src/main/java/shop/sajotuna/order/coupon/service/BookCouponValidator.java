package shop.sajotuna.order.coupon.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shop.sajotuna.order.coupon.exception.CouponNotFoundException;
import shop.sajotuna.order.coupon.repository.BookCouponRepository;
import shop.sajotuna.order.coupon.repository.UserCouponRepository;

@Component
@RequiredArgsConstructor
public class BookCouponValidator {

    private final BookCouponRepository bookCouponRepository;
    private final UserCouponRepository userCouponRepository;

    public void validateCoupon(Long userId, Long couponId, String isbn) {
        if (!bookCouponRepository.existsByCouponIdAndIsbn(couponId, isbn)) {
            throw new CouponNotFoundException();
        }
    }
}
