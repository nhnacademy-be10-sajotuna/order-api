package shop.sajotuna.order.orders.service.pricing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.orders.domain.Discounts;
import shop.sajotuna.order.orders.domain.Order;
import shop.sajotuna.order.orders.domain.OrderPrice;
import shop.sajotuna.order.orders.domain.Orderer;
import shop.sajotuna.order.orders.domain.ShippingInfo;
import shop.sajotuna.order.orders.repository.OrderRepository;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class OrderTotalPriceServiceIntegrationTest {

    private static final Long USER_ID = 1L;

    @Autowired
    private OrderTotalPriceService orderTotalPriceService;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
    }

    @Test
    void calculateTotalOrderAmount_sumsOnlyPaidActiveOrders() {
        orderRepository.save(paidOrder(Money.of(10_000), Money.of(1_000), Money.of(500)));
        orderRepository.save(paidOrder(Money.of(20_000), Money.zero(), Money.zero()));
        orderRepository.save(cancelledOrder(Money.of(50_000)));
        orderRepository.save(beforePaymentOrder(Money.of(70_000)));

        Money result = orderTotalPriceService.calculateTotalOrderAmount(USER_ID);

        assertThat(result).isEqualTo(Money.of(28_500));
    }

    private Order paidOrder(Money totalProductPrice, Money couponDiscountAmount, Money usedPoint) {
        Order order = createOrder(totalProductPrice, couponDiscountAmount, usedPoint);
        order.completePayment();
        return order;
    }

    private Order cancelledOrder(Money totalProductPrice) {
        Order order = paidOrder(totalProductPrice, Money.zero(), Money.zero());
        order.cancelled();
        return order;
    }

    private Order beforePaymentOrder(Money totalProductPrice) {
        return createOrder(totalProductPrice, Money.zero(), Money.zero());
    }

    private Order createOrder(Money totalProductPrice, Money couponDiscountAmount, Money usedPoint) {
        return Order.createOrder(
                new Orderer(USER_ID, "tester", "010-1234-5678", "tester@example.com"),
                ShippingInfo.create(
                        "tester",
                        "010-1234-5678",
                        "tester@example.com",
                        "Seoul",
                        LocalDate.now().plusDays(3)
                ),
                OrderPrice.create(totalProductPrice, Money.zero(), Money.zero()),
                new Discounts(couponDiscountAmount, usedPoint, null),
                List.of()
        );
    }
}
