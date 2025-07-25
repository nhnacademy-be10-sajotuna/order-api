package shop.sajotuna.order.orders.service.process;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class OrderProcessorFactoryTest {

    @Autowired
    private OrderProcessorFactory orderProcessorFactory;

    @Test
    @DisplayName("userId가 null인 경우에 guestOrderProcessor를 반환한다")
    void getGuestOrderProcessor() {
        // given
        Long userId = null;

        // when
        OrderProcessor orderProcessor = orderProcessorFactory.getOrderProcessor(userId);

        // then
        Assertions.assertThat(orderProcessor).isInstanceOf(GuestOrderProcessor.class);
    }

    @Test
    @DisplayName("userId가 있는 경우에 userOrderProcessor를 반환한다")
    void getUserOrderProcessor() {
        // given
        Long userId = 1L;

        // when
        OrderProcessor orderProcessor = orderProcessorFactory.getOrderProcessor(userId);

        // then
        Assertions.assertThat(orderProcessor).isInstanceOf(UserOrderProcessor.class);
    }

}