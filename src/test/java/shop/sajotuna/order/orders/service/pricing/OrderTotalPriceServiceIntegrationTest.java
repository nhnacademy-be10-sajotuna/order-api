package shop.sajotuna.order.orders.service.pricing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.orders.domain.Discounts;
import shop.sajotuna.order.orders.domain.Order;
import shop.sajotuna.order.orders.domain.OrderPrice;
import shop.sajotuna.order.orders.domain.Orderer;
import shop.sajotuna.order.orders.domain.OrderStatus;
import shop.sajotuna.order.orders.domain.ShippingInfo;
import shop.sajotuna.order.orders.repository.OrderRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @Test
    void findUserIdsWithOrdersExpiringFromGradeWindow_returnsOnlyUsersInExpiredWindow() {
        Order expiredActiveOrder = paidOrder(Money.of(10_000), Money.zero(), Money.zero());
        ReflectionTestUtils.setField(expiredActiveOrder, "createdAt", LocalDateTime.of(2026, 2, 11, 10, 0));

        Order duplicatedExpiredActiveOrder = paidOrder(Money.of(20_000), Money.zero(), Money.zero());
        ReflectionTestUtils.setField(duplicatedExpiredActiveOrder, "createdAt", LocalDateTime.of(2026, 2, 11, 12, 0));

        Order stillIncludedOrder = paidOrder(2L, Money.of(30_000), Money.zero(), Money.zero());
        ReflectionTestUtils.setField(stillIncludedOrder, "createdAt", LocalDateTime.of(2026, 2, 12, 0, 0));

        Order expiredCancelledOrder = cancelledOrder(3L, Money.of(40_000));
        ReflectionTestUtils.setField(expiredCancelledOrder, "createdAt", LocalDateTime.of(2026, 2, 11, 10, 0));

        Order expiredGuestOrder = paidGuestOrder(Money.of(50_000));
        ReflectionTestUtils.setField(expiredGuestOrder, "createdAt", LocalDateTime.of(2026, 2, 11, 10, 0));

        orderRepository.saveAll(List.of(
                expiredActiveOrder,
                duplicatedExpiredActiveOrder,
                stillIncludedOrder,
                expiredCancelledOrder,
                expiredGuestOrder
        ));

        List<Long> userIds = orderRepository.findUserIdsWithOrdersExpiringFromGradeWindow(
                LocalDateTime.of(2026, 2, 11, 0, 0),
                LocalDateTime.of(2026, 2, 12, 0, 0),
                List.of(OrderStatus.PENDING, OrderStatus.SHIPPED, OrderStatus.DELIVERED)
        );

        assertThat(userIds).containsExactly(USER_ID);
    }

    private Order paidOrder(Money totalProductPrice, Money couponDiscountAmount, Money usedPoint) {
        return paidOrder(USER_ID, totalProductPrice, couponDiscountAmount, usedPoint);
    }

    private Order paidOrder(Long userId, Money totalProductPrice, Money couponDiscountAmount, Money usedPoint) {
        Order order = createOrder(totalProductPrice, couponDiscountAmount, usedPoint);
        ReflectionTestUtils.setField(order, "orderer", new Orderer(userId, "tester", "010-1234-5678", "tester@example.com"));
        order.completePayment();
        return order;
    }

    private Order cancelledOrder(Money totalProductPrice) {
        return cancelledOrder(USER_ID, totalProductPrice);
    }

    private Order cancelledOrder(Long userId, Money totalProductPrice) {
        Order order = paidOrder(userId, totalProductPrice, Money.zero(), Money.zero());
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

    private Order paidGuestOrder(Money totalProductPrice) {
        Order order = Order.createOrder(
                new Orderer(null, "guest", "010-1234-5678", "guest@example.com"),
                ShippingInfo.create(
                        "guest",
                        "010-1234-5678",
                        "guest@example.com",
                        "Seoul",
                        LocalDate.now().plusDays(3)
                ),
                OrderPrice.create(totalProductPrice, Money.zero(), Money.zero()),
                new Discounts(Money.zero(), Money.zero(), null),
                List.of()
        );
        order.completePayment();
        return order;
    }
}
