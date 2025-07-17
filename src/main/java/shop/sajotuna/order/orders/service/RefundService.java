package shop.sajotuna.order.orders.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.sajotuna.order.coupon.domain.UserCoupon;
import shop.sajotuna.order.coupon.exception.CouponNotFoundException;
import shop.sajotuna.order.coupon.repository.UserCouponRepository;
import shop.sajotuna.order.orders.domain.Order;
import shop.sajotuna.order.stock.service.StockService;

@Service
@RequiredArgsConstructor
public class RefundService {

    private final StockService stockService;
    private final UserCouponRepository userCouponRepository;

    public void returnCoupon(Order order) {
        order.getOrderProducts().forEach(
                product -> {
                    UserCoupon appliedCoupon = product.getAppliedCoupon();
                    if (appliedCoupon != null) {
                        appliedCoupon.returnCoupon();
                    }
                }
        );

        Long usedOrderCouponId = order.getDiscounts().getUsedCouponId();
        if (usedOrderCouponId != null) {
            UserCoupon userCoupon = userCouponRepository.findById(usedOrderCouponId).orElseThrow(() -> new CouponNotFoundException(usedOrderCouponId));
            userCoupon.returnCoupon();
        }
    }

    public void returnStock(Order order) {
        order.getOrderProducts().forEach(
                product -> stockService.increaseStock(product.getIsbn(), product.getQty())
        );
    }
}
