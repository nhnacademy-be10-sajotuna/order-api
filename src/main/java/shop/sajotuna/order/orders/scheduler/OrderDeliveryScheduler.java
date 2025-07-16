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
    public void deliverOrders() {
        try{
            // 출고 날짜로부터 1일 이상 경과한 주문들을 배송완료로 변경
            List<Order> orders = orderRepository.findShippedOrders(LocalDateTime.now().minusDays(1));
            orders.forEach(Order::delivered);

            log.info("Orders delivered");
        } catch (Exception e) {
            log.error("주문 배송완료 처리 중 오류 발생");
            throw e;
        }
    }
}
