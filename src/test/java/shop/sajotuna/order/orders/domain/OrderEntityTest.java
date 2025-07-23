package shop.sajotuna.order.orders.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import shop.sajotuna.order.common.domain.Money;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
public class OrderEntityTest {
    private Order order;

    @BeforeEach
    void setUp() {
        order = createTestOrder();
    }

    @Test
    @DisplayName("order 메서드 검증")
    void testUpdate() {
        order.completePayment();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);

        order.shipped();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.SHIPPED);

        order.delivered();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    private Order createTestOrder() {
        Orderer orderer = new Orderer(1L, "홍길동", "010-1234-5678", "test@example.com");
        ShippingInfo shippingInfo = ShippingInfo.create(
                "홍길동", "010-1234-5678", "test@example.com",
                "서울시 강남구 테헤란로 123",
                LocalDateTime.now().plusDays(3)
        );
        OrderPrice orderPrice = OrderPrice.create(Money.of(17000), Money.of(0), Money.of(3000));
        Discounts discounts = new Discounts(Money.of(0), Money.of(0), null);

        OrderProduct orderProduct = createTestOrderProduct();

        return Order.createOrder(orderer, shippingInfo, orderPrice, discounts, List.of(orderProduct));
    }

    private OrderProduct createTestOrderProduct() {
        return OrderProduct.builder()
                .isbn("1235433121")
                .qty(1)
                .amount(Money.of(10000))
                .packagingRequest(false)
                .build();
    }
}
