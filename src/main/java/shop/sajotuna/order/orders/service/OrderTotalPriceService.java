package shop.sajotuna.order.orders.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.orders.domain.Order;
import shop.sajotuna.order.orders.repository.OrderRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderTotalPriceService {

    private final OrderRepository orderRepository;

    // 3개월 간 순수 주문 금액 계산
    @Transactional(readOnly = true)
    public Money calculateTotalOrderAmount(Long userId) {
        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
        List<Order> orders = orderRepository.findByOrdererUserIdAndCreatedAtAfter(userId, threeMonthsAgo);
        return orders.stream()
                .map(Order::getFinalProductPrice)
                .reduce(Money.zero(), Money::plus);
    }
}
