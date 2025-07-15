package shop.sajotuna.order.orders.service.process;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import shop.sajotuna.order.orders.domain.Discounts;
import shop.sajotuna.order.orders.domain.Order;
import shop.sajotuna.order.orders.domain.OrderProduct;
import shop.sajotuna.order.orders.service.pricing.DiscountService;
import shop.sajotuna.order.orders.service.dto.command.CreateOrderCommand;
import shop.sajotuna.order.point.domain.PointPolicyType;
import shop.sajotuna.order.point.service.PointService;
import shop.sajotuna.order.point.service.dto.event.PointEvent;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserOrderProcessor implements OrderProcessor {
    
    private final DiscountService discountService;
    private final ApplicationEventPublisher eventPublisher;
    private final PointService pointService;
    
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
        PointEvent event = pointService.earnPoints(command.getUserId(), PointPolicyType.PURCHASE, order.getFinalProductPrice());

        eventPublisher.publishEvent(event);
        order.setEarnedPoint(event.getPointAmount());
    }
}