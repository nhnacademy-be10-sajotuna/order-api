package shop.sajotuna.order.orders.service.pricing;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.orders.domain.Order;
import shop.sajotuna.order.orders.repository.OrderRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderTotalPriceServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderTotalPriceService orderTotalPriceService;

    @Test
    @DisplayName("3개월간 총 주문 금액 계산 성공 - 주문이 있는 경우")
    void calculateTotalOrderAmount_withOrders() {
        // given
        Long userId = 1L;
        
        Order order1 = mock(Order.class);
        Order order2 = mock(Order.class);
        Order order3 = mock(Order.class);
        
        when(order1.getFinalProductPrice()).thenReturn(Money.of(10000));
        when(order2.getFinalProductPrice()).thenReturn(Money.of(15000));
        when(order3.getFinalProductPrice()).thenReturn(Money.of(20000));
        
        List<Order> orders = List.of(order1, order2, order3);
        
        when(orderRepository.findByOrdererUserIdAndCreatedAtAfter(eq(userId), any(LocalDateTime.class)))
                .thenReturn(orders);

        // when
        Money result = orderTotalPriceService.calculateTotalOrderAmount(userId);

        // then
        assertThat(result).isEqualTo(Money.of(45000));
        verify(orderRepository).findByOrdererUserIdAndCreatedAtAfter(eq(userId), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("3개월간 총 주문 금액 계산 성공 - 주문이 없는 경우")
    void calculateTotalOrderAmount_noOrders() {
        // given
        Long userId = 1L;
        
        when(orderRepository.findByOrdererUserIdAndCreatedAtAfter(eq(userId), any(LocalDateTime.class)))
                .thenReturn(List.of());

        // when
        Money result = orderTotalPriceService.calculateTotalOrderAmount(userId);

        // then
        assertThat(result).isEqualTo(Money.zero());
        verify(orderRepository).findByOrdererUserIdAndCreatedAtAfter(eq(userId), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("3개월간 총 주문 금액 계산 성공 - 단일 주문")
    void calculateTotalOrderAmount_singleOrder() {
        // given
        Long userId = 1L;
        
        Order order = mock(Order.class);
        when(order.getFinalProductPrice()).thenReturn(Money.of(30000));
        
        when(orderRepository.findByOrdererUserIdAndCreatedAtAfter(eq(userId), any(LocalDateTime.class)))
                .thenReturn(List.of(order));

        // when
        Money result = orderTotalPriceService.calculateTotalOrderAmount(userId);

        // then
        assertThat(result).isEqualTo(Money.of(30000));
        verify(orderRepository).findByOrdererUserIdAndCreatedAtAfter(eq(userId), any(LocalDateTime.class));
    }
}