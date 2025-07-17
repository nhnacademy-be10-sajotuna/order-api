package shop.sajotuna.order.point.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import shop.sajotuna.order.point.rabbitmq.PointRabbitProperties;
import shop.sajotuna.order.point.service.dto.event.PointEarnRequest;
import shop.sajotuna.order.point.service.PointQueueService;

@Service
@RequiredArgsConstructor
public class PointQueueServiceImpl implements PointQueueService {

    private final PointRabbitProperties pointRabbitProperties;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void sendEarnPointsMessage(PointEarnRequest event) {
        rabbitTemplate.convertAndSend(pointRabbitProperties.getExchange(),
                pointRabbitProperties.getRoutingKey(),
                event);
    }
}
