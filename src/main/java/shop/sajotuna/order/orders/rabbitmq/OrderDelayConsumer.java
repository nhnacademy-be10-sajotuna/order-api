package shop.sajotuna.order.orders.rabbitmq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.ImmediateAcknowledgeAmqpException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.sajotuna.order.orders.service.OrderStatusService;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderDelayConsumer {

    private final OrderStatusService orderStatusService;

    @Transactional
    @RabbitListener(queues = "${rabbitmq.order.dlx-queue}", containerFactory = "rabbitListenerContainerFactory")
    // TODO: 재시도 로직 구현
    public void handleExpiredOrder(OrderEvent orderEvent) {
        try {
            orderStatusService.cancelOrderBeforePayment(orderEvent.getUserId(), orderEvent.getOrderId());
        } catch (Exception e) {
            throw new ImmediateAcknowledgeAmqpException(e);
        }
    }
}
