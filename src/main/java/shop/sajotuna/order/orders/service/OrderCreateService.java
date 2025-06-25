package shop.sajotuna.order.orders.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.sajotuna.order.orders.dto.OrderRequest;
import shop.sajotuna.order.orders.entity.Order;
import shop.sajotuna.order.orders.repository.OrderRepository;

@Service
@RequiredArgsConstructor
public class OrderCreateService {

    private final PricingService pricingService;
    private final DiscountService discountService;
    private final OrderRepository orderRepository;

    public Order processOrderCreation(OrderRequest request, Long userId) {
        Order order = request.toEntity(userId);
        order.setOrderPrice(pricingService.calculatePrices(request));
        order.setDiscounts(discountService.discount(request, userId, order.getOrderPrice().getTotalProductPrice()));

        return orderRepository.save(order);
    }
}
