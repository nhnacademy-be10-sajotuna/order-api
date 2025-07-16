package shop.sajotuna.order.coupon.rabbitmq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CouponDlqConsumer {
    private static final String RETRY_COUNT_HEADER = "x-retry-count";
    private static final int MAX_RETRY_COUNT = 10;

    private final RabbitTemplate rabbitTemplate;
    private final CouponRabbitProperties properties;
    private final CouponRabbitProperties couponRabbitProperties;

    @RabbitListener(queues = "${rabbitmq.coupon.dlx-queue}")
    public void consumeDlqMessage(Message message) {
        log.info("Coupon DLQ: received message from DLQ: {}", message);
        MessageProperties props = message.getMessageProperties();
        Object header = props.getHeaders().get(RETRY_COUNT_HEADER);
        int retires = (header instanceof Integer ? (Integer) header : 0);

        if (retires < MAX_RETRY_COUNT) {
            Message retry = MessageBuilder
                    .withBody(message.getBody())
                    .copyHeaders(props.getHeaders())
                    .setHeader(RETRY_COUNT_HEADER, retires + 1)
                    .build();
            rabbitTemplate.send(properties.getExchange(), properties.getRoutingKey(), retry);
        } else {
            log.warn("Coupon DLQ: message permanently failed after {} retries, header={}", retires, props.getHeaders());
            rabbitTemplate.send(couponRabbitProperties.getParkingLotExchange(), couponRabbitProperties.getParkingLotRoutingKey(), message);
        }
    }
}
