package shop.sajotuna.order.orders.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.coupon.domain.UserCoupon;
import shop.sajotuna.order.coupon.exception.CouponNotFoundException;
import shop.sajotuna.order.coupon.repository.UserCouponRepository;
import shop.sajotuna.order.orders.domain.Discounts;
import shop.sajotuna.order.point.domain.UserPoint;
import shop.sajotuna.order.point.exception.UserPointNotFoundException;
import shop.sajotuna.order.point.repository.UserPointRepository;
import shop.sajotuna.order.point.service.PointHistoryWriter;

@Service
@RequiredArgsConstructor
public class DiscountService {

    private final UserPointRepository userPointRepository;
    private final UserCouponRepository userCouponRepository;
    private final PointHistoryWriter pointHistoryWriter;

    @Transactional
    public Discounts discount(Long orderCouponId, Money usedPoint, Long userId, Money totalProductPrice) {
        Money couponDiscountAmount = Money.zero();
        if (orderCouponId != null) {
            UserCoupon userCoupon = getUserCouponById(orderCouponId);
            couponDiscountAmount = userCoupon.applyCoupon(totalProductPrice);
        }

        if (usedPoint.isPositive()) {
            UserPoint userPoint = getUserPointByUserId(userId);
            userPoint.redeemPoint(usedPoint);
            pointHistoryWriter.savePointRedeemHistory(userId, usedPoint);
        }
        return new Discounts(couponDiscountAmount, usedPoint);
    }

    private UserPoint getUserPointByUserId(Long userId) {
        return userPointRepository.findByUserId(userId)
                .orElseThrow(UserPointNotFoundException::new);
    }

    private UserCoupon getUserCouponById(Long userCouponId) {
        return userCouponRepository.findByIdWithCoupon(userCouponId)
                .orElseThrow(CouponNotFoundException::new);
    }
}
