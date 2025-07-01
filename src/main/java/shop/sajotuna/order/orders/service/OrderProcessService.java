package shop.sajotuna.order.orders.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.orders.domain.Discounts;
import shop.sajotuna.order.orders.domain.OrderPrice;
import shop.sajotuna.order.orders.domain.OrderProduct;
import shop.sajotuna.order.orders.controller.dto.response.OrderResponse;
import shop.sajotuna.order.orders.domain.Order;
import shop.sajotuna.order.orders.repository.OrderRepository;
import shop.sajotuna.order.orders.service.dto.command.CreateOrderCommand;
import shop.sajotuna.order.point.domain.PointPolicyType;
import shop.sajotuna.order.point.service.PointQueueService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderProcessService {

    private final PricingService pricingService;
    private final DiscountService discountService;
    private final OrderRepository orderRepository;
    private final OrderProductCreateService orderProductCreateService;
    private final PointQueueService pointQueueService;

    @Transactional
    public OrderResponse processOrder(CreateOrderCommand command) {

        List<OrderProduct> orderProducts = orderProductCreateService.createOrderProducts(command.getItems());
        OrderPrice orderPrice = pricingService.calculatePrices(orderProducts);

        Discounts discounts = new Discounts(Money.zero(), Money.zero());

        // 비회원 주문일 경우 할인이 적용되지 않음
        if (command.getUserId() != null) {
            discounts = discountService.discount(command.getOrderCouponId(), command.getUsedPoint(), command.getUserId(), orderPrice.getTotalProductPrice());
        }

        Order order = Order.createOrder(command.getOrderer(), command.getShippingInfo(), orderPrice, discounts, orderProducts);
        orderRepository.save(order);

        // 비회원 주문일 경우 포인트 적립이 되지 않음
        if(command.getUserId() != null) {
            pointQueueService.sendEarnPointsMessage(command.getUserId(), PointPolicyType.PURCHASE, order.getFinalProductPrice());
        }

        return OrderResponse.from(order);
    }
}
