package shop.sajotuna.order.orders.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import shop.sajotuna.order.orders.domain.Order;
import shop.sajotuna.order.orders.repository.OrderRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderDeliveryScheduler {
    private static final String SCHEDULE = "0 0 12 * * *";
    private final OrderRepository orderRepository;

    // 배송중으로 변경된 주문은 일정 시간경과 후 완료 처리 됨
    @Scheduled(cron = SCHEDULE) // 매일 낮 12시 마다 실행됨
    @Transactional
    public void deliveredOrder() {
        // 현재 시간 기준으로 1일 이상 지난 주문들을 가져온다
        List<Order> orders = orderRepository.findShippedOrders(LocalDateTime.now().minusDays(1));
        // 배송 날짜와 1일 이상 차이가 난다면 배송완료로 변경
        orders.forEach(Order::delivered);

        log.info("Orders delivered");
    }
}
