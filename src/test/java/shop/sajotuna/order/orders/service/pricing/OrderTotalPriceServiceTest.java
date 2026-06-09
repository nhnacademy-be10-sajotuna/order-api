package shop.sajotuna.order.orders.service.pricing;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.orders.domain.OrderStatus;
import shop.sajotuna.order.orders.repository.OrderRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderTotalPriceServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderTotalPriceService orderTotalPriceService;

    @Test
    @DisplayName("recent order amount is calculated by DB SUM query")
    void calculateTotalOrderAmount_withSumQuery() {
        Long userId = 1L;

        when(orderRepository.sumRecentOrderAmount(
                eq(userId),
                any(LocalDateTime.class),
                eq(List.of(OrderStatus.PENDING, OrderStatus.SHIPPED, OrderStatus.DELIVERED))
        )).thenReturn(45_000L);

        Money result = orderTotalPriceService.calculateTotalOrderAmount(userId);

        assertThat(result).isEqualTo(Money.of(45_000));
        verify(orderRepository).sumRecentOrderAmount(
                eq(userId),
                any(LocalDateTime.class),
                eq(List.of(OrderStatus.PENDING, OrderStatus.SHIPPED, OrderStatus.DELIVERED))
        );
    }

    @Test
    @DisplayName("recent order amount returns zero when there is no order")
    void calculateTotalOrderAmount_noOrders() {
        Long userId = 1L;

        when(orderRepository.sumRecentOrderAmount(
                eq(userId),
                any(LocalDateTime.class),
                eq(List.of(OrderStatus.PENDING, OrderStatus.SHIPPED, OrderStatus.DELIVERED))
        )).thenReturn(0L);

        Money result = orderTotalPriceService.calculateTotalOrderAmount(userId);

        assertThat(result).isEqualTo(Money.zero());
    }
}
