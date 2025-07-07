package shop.sajotuna.order.orders.service.process;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shop.sajotuna.order.orders.domain.Discounts;
import shop.sajotuna.order.orders.domain.Order;
import shop.sajotuna.order.orders.domain.OrderProduct;
import shop.sajotuna.order.orders.service.pricing.DiscountService;
import shop.sajotuna.order.orders.service.dto.command.CreateOrderCommand;
import shop.sajotuna.order.point.domain.PointPolicyType;
import shop.sajotuna.order.point.service.PointQueueService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserOrderProcessor implements OrderProcessor {
    
    private final DiscountService discountService;
    private final PointQueueService pointQueueService;
    
    @Override
    public Discounts processDiscounts(CreateOrderCommand command, List<OrderProduct> orderProducts) {
        return discountService.applyDiscountsToProducts(
            command.getOrderCouponId(),
            command.getUsedPoint(),
            command.getUserId(),
            orderProducts
        );
    }
    
    @Override
    public void processPointEarn(CreateOrderCommand command, Order order) {
        pointQueueService.sendEarnPointsMessage(
            command.getUserId(),
            PointPolicyType.PURCHASE,
            order.getFinalProductPrice()
        );
    }
}