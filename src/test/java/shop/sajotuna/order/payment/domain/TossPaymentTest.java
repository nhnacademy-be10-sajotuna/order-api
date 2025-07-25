package shop.sajotuna.order.payment.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.orders.domain.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class TossPaymentTest {
    @Test
    @DisplayName("tossPayment 생성")
    void createPayment() {
        Order order = createTestOrder();

        Payment payment = new Payment(order, PaymentMethod.CARD);

        TossPayment tossPayment = TossPayment.builder()
                .orderId(order.getOrderNumber())
                .paymentKey(UUID.randomUUID().toString())
                .amount(payment.getAmount().getAmount()).build();

        assertThat(tossPayment).isNotNull();
        assertThat(tossPayment.getAmount()).isEqualTo(payment.getAmount().getAmount());
        assertThat(tossPayment.getPaymentKey()).isNotNull();
        assertThat(tossPayment.getOrderId()).isEqualTo(order.getOrderNumber());
    }

    private Order createTestOrder() {
        Orderer orderer = new Orderer(1L, "홍길동", "010-1234-5678", "test@example.com");
        ShippingInfo shippingInfo = ShippingInfo.create(
                "홍길동", "010-1234-5678", "test@example.com",
                "서울시 강남구 테헤란로 123",
                LocalDate.now().plusDays(3)
        );
        OrderPrice orderPrice = OrderPrice.create(Money.of(17000), Money.of(0), Money.of(3000));
        Discounts discounts = new Discounts(Money.of(0), Money.of(0), null);

        OrderProduct orderProduct = createTestOrderProduct();

        Order order = Order.createOrder(orderer, shippingInfo, orderPrice, discounts, List.of(orderProduct));

        order.completePayment();

        return order;
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
