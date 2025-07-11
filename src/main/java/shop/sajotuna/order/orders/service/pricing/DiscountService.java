package shop.sajotuna.order.orders.service.pricing;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.coupon.domain.UserCoupon;
import shop.sajotuna.order.coupon.exception.CouponNotFoundException;
import shop.sajotuna.order.coupon.repository.UserCouponRepository;
import shop.sajotuna.order.orders.domain.Discounts;
import shop.sajotuna.order.orders.domain.OrderProduct;
import shop.sajotuna.order.point.service.PointService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DiscountService {

    private final UserCouponRepository userCouponRepository;
    private final PointService pointService;

    @Transactional
    public Discounts applyDiscountsToProducts(Long orderCouponId, Money usedPoint, Long userId, 
                                              List<OrderProduct> orderProducts) {

        Money totalCouponDiscountAmount = Money.zero();
        
        // 상품별 쿠폰 적용
        for (OrderProduct orderProduct : orderProducts) {
            totalCouponDiscountAmount = totalCouponDiscountAmount.plus(orderProduct.applyCouponDiscount());
        }

        // 주문 쿠폰 적용 (전체 주문 금액에서 이미 적용된 상품별 할인 제외)
        if (orderCouponId != null) {
            Money totalProductPrice = calculateTotalProductPrice(orderProducts);
            UserCoupon orderCoupon = getUserCouponById(orderCouponId);
            Money orderDiscountBase = totalProductPrice.minus(totalCouponDiscountAmount);
            Money orderDiscount = orderCoupon.applyCoupon(orderDiscountBase);
            totalCouponDiscountAmount = totalCouponDiscountAmount.plus(orderDiscount);
        }

        // 포인트 사용
        if (usedPoint.isPositive()) {
            pointService.redeemPoints(userId, usedPoint);
        }
        
        return new Discounts(totalCouponDiscountAmount, usedPoint);
    }
    
    private Money calculateTotalProductPrice(List<OrderProduct> orderProducts) {
        return orderProducts.stream()
            .map(OrderProduct::getTotalPrice)
            .reduce(Money.zero(), Money::plus);
    }

    private UserCoupon getUserCouponById(Long userCouponId) {
        return userCouponRepository.findByIdWithCoupon(userCouponId)
                .orElseThrow(() -> new CouponNotFoundException(userCouponId));
    }
}
