package shop.sajotuna.order.coupon.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import shop.sajotuna.order.coupon.rabbitmq.CouponRabbitProperties;
import shop.sajotuna.order.coupon.service.dto.event.CouponEvent;

@Service
@RequiredArgsConstructor
public class CouponQueueService {
    private final CouponRabbitProperties couponRabbitProperties;
    private final RabbitTemplate rabbitTemplate;

    public void sendIssueCouponMessage(CouponEvent couponEvent) {
        rabbitTemplate.convertAndSend(
                couponRabbitProperties.getExchange(),
                couponRabbitProperties.getRoutingKey(),
                couponEvent
        );
    }
}