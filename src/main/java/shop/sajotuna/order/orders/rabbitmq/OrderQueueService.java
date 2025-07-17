package shop.sajotuna.order.orders.rabbitmq;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderQueueService {

    private final RabbitTemplate rabbitTemplate;
    private final OrderRabbitProperties orderRabbitProperties;

    public void sendOrderMessage(OrderEvent event) {
        rabbitTemplate.convertAndSend(
                orderRabbitProperties.getExchange(),
                orderRabbitProperties.getRoutingKey(),
                event
        );
    }
}
