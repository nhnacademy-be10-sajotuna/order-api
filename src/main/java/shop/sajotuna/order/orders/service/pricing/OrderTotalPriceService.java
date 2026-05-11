package shop.sajotuna.order.orders.service.pricing;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.orders.domain.OrderStatus;
import shop.sajotuna.order.orders.repository.OrderRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderTotalPriceService {

    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public Money calculateTotalOrderAmount(Long userId) {
        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
        Long totalAmount = orderRepository.sumRecentOrderAmount(
                userId,
                threeMonthsAgo,
                List.of(OrderStatus.PENDING, OrderStatus.SHIPPED, OrderStatus.DELIVERED)
        );
        return Money.of(totalAmount.intValue());
    }
}
