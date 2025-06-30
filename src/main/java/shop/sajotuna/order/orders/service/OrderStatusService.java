package shop.sajotuna.order.orders.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.sajotuna.order.orders.domain.Order;
import shop.sajotuna.order.orders.repository.OrderRepository;
import shop.sajotuna.order.payment.domain.Payment;
import shop.sajotuna.order.payment.repository.PaymentRepository;
import shop.sajotuna.order.point.domain.PointPolicyType;
import shop.sajotuna.order.point.exception.OrderNotFoundException;
import shop.sajotuna.order.point.service.PointQueueService;
import shop.sajotuna.order.point.service.dto.event.PointEvent;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderStatusService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final PointQueueService pointQueueService;

    private final String SCHEDULE = "0 0 12 * * *";

    // 주문 배송 중으로 변경
    @Transactional
    public void shippedOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(OrderNotFoundException::new);
        order.shipped();
    }

    // 배송중으로 변경된 주문은 일정 시간경과 후 완료 처리 됨
    @Scheduled(cron = SCHEDULE) // 매일 낮 12시 마다 실행됨
    @Transactional
    public void deliveredOrder() {
        // 현재 시간 기준으로 1일 이상 지난 주문들을 가져온다
        List<Order> orders = orderRepository.findShippedOrders(LocalDateTime.now().minusDays(1));
        // 배송 날짜와 1일 이상 차이가 난다면 배송완료로 변경
        orders.forEach(Order::delivered);
    }

    // 주문 반품 처리
    @Transactional
    public void returnedOrder(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(OrderNotFoundException::new);
        order.returned();

        // 반품시 결제금액은 포인트로 적립됨
        Payment payment = paymentRepository.getPaymentByOrder_Id(orderId);
        pointQueueService.sendEarnPointsMessage(new PointEvent(userId, PointPolicyType.RETURNED, payment.getAmount()));
    }

    // 주문 취소 처리
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(OrderNotFoundException::new);
        order.cancelled();
    }
}
