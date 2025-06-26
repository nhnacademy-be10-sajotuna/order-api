package shop.sajotuna.order.orders.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.orders.domain.Discounts;
import shop.sajotuna.order.orders.domain.OrderPrice;
import shop.sajotuna.order.orders.dto.OrderRequest;
import shop.sajotuna.order.orders.domain.Order;
import shop.sajotuna.order.orders.repository.OrderRepository;

@Service
@RequiredArgsConstructor
public class OrderCreateService {

    private final PricingService pricingService;
    private final DiscountService discountService;
    private final OrderRepository orderRepository;

    public Order processOrderCreation(OrderRequest request, Long userId) {
        OrderPrice orderPrice = pricingService.calculatePrices(request.getItems(), Money.of(request.getDeliveryPrice()));
        Discounts discount = discountService.discount(request.getOrderCouponId(), Money.of(request.getUsedPoint()), userId, orderPrice.getTotalProductPrice());
        Order order = Order.createUserOrder(
                request.getShippingDate(),
                request.getStreetAddress(),
                orderPrice,
                discount,
                userId);
        return orderRepository.save(order);
    }
}
