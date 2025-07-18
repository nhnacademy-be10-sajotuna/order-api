package shop.sajotuna.order.orders.rabbitmq;

import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.sajotuna.order.orders.domain.Order;
import shop.sajotuna.order.orders.domain.OrderStatus;
import shop.sajotuna.order.orders.repository.OrderRepository;
import shop.sajotuna.order.orders.service.OrderStatusService;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderDelayConsumer {

    private final OrderStatusService orderStatusService;
    private final OrderRepository orderRepository;

    @Transactional
    @RabbitListener(queues = "${rabbitmq.order.dlx-queue}", containerFactory = "orderListenerContainerFactory")
    public void handleExpiredOrder(OrderEvent orderEvent, Channel channel) {
        Order order = orderRepository.findById(orderEvent.getOrderId()).orElse(null);

        if (order == null || order.getStatus() != OrderStatus.BEFORE_PAYMENT) {
            log.info("Order {} already processed, status: {}",
                    orderEvent.getOrderId(), order != null ? order.getStatus() : "NOT_FOUND");
            return;
        }

        orderStatusService.refundAndCleanup(order, orderEvent.getUserId());
    }

}
