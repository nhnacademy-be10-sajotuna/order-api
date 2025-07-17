package shop.sajotuna.order.orders.rabbitmq;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.sajotuna.order.orders.service.OrderStatusService;

@Service
@RequiredArgsConstructor
public class OrderDelayConsumer {

    private final OrderStatusService orderStatusService;

    @Transactional
    @RabbitListener(queues = "${rabbitmq.order.dlx-queue}", containerFactory = "rabbitListenerContainerFactory")
    public void handleExpiredOrder(OrderEvent orderEvent) {
        orderStatusService.cancelOrderBeforePayment(orderEvent.getUserId(), orderEvent.getOrderId());
    }
}
