package shop.sajotuna.order.payment.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.orders.domain.*;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PaymentTest {

    @Test
    @DisplayName("payment 생성")
    void createPayment() {
        Order order = createTestOrder();

        Payment payment = new Payment(order, PaymentMethod.CARD);

        assertThat(payment).isNotNull();
        assertThat(payment.getAmount()).isEqualTo(order.getFinalPrice());
        assertThat(payment.getMethod()).isEqualTo(PaymentMethod.CARD);
        assertThat(payment.getOrder()).isEqualTo(order);
    }

    @Test
    @DisplayName("order가 null인 경우 예외 발생")
    void payment_orderNull_throwsException() {
        assertThatThrownBy(() -> new Payment(null, PaymentMethod.CARD))
                .isInstanceOf(NullPointerException.class);
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
