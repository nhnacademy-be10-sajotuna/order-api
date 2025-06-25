package shop.sajotuna.order.orders.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.sajotuna.order.coupon.domain.UserCoupon;
import shop.sajotuna.order.coupon.exception.CouponNotFoundException;
import shop.sajotuna.order.coupon.repository.UserCouponRepository;
import shop.sajotuna.order.orders.dto.OrderRequest;
import shop.sajotuna.order.orders.entity.Discounts;
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

    public Discounts discount(OrderRequest orderRequest, Long userId, int totalProductPrice) {

        int couponDiscountAmount = 0;
        if (orderRequest.getOrderCouponId() != null) {
            UserCoupon userCoupon = getUserCouponById(orderRequest.getOrderCouponId());
            couponDiscountAmount = userCoupon.applyCoupon(totalProductPrice);
        }

        if(orderRequest.getUsedPoint() > 0){
            UserPoint userPoint = getUserPointByUserId(userId);
            userPoint.redeemPoint(orderRequest.getUsedPoint());
            pointHistoryWriter.savePointRedeemHistory(userId, orderRequest.getUsedPoint());
        }

        return new Discounts(couponDiscountAmount, orderRequest.getUsedPoint());
    }

    private UserPoint getUserPointByUserId(Long userId) {
        return userPointRepository.findByUserId(userId)
                .orElseThrow(UserPointNotFoundException::new);
    }

    private UserCoupon getUserCouponById(Long userCouponId) {
        return userCouponRepository.findById(userCouponId)
                .orElseThrow(CouponNotFoundException::new);
    }
}
