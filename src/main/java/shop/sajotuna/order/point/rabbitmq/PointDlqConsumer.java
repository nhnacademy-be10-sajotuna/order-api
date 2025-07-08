package shop.sajotuna.order.point.rabbitmq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class PointDlqConsumer {

    private static final String RETRY_COUNT_HEADER = "x-retries_count";
    private static final int MAX_RETRY_COUNT = 10;

    private final RabbitTemplate rabbitTemplate;
    private final PointRabbitProperties pointRabbitProperties;
    private final PointFatalMessageLogger fatalMessageLogger;

    @RabbitListener(queues = "${rabbitmq.point.dlx-queue}")
    public void consumeDlqMessage(Message dlqMessage) {
        log.info("POINT DLQ: received message from DLQ: {}", dlqMessage);
        MessageProperties props = dlqMessage.getMessageProperties();
        Object header = props.getHeaders().get(RETRY_COUNT_HEADER);
        int retries = (header instanceof Integer) ? (Integer) header : 0;

        if (retries < MAX_RETRY_COUNT) {
            Message retry = MessageBuilder
                    .withBody(dlqMessage.getBody())
                    .copyHeaders(props.getHeaders())
                    .setHeader(RETRY_COUNT_HEADER, retries + 1)
                    .build();

            rabbitTemplate.send(pointRabbitProperties.getExchange(), pointRabbitProperties.getRoutingKey(), retry);
        } else {
            log.warn("POINT DLQ: message permanently failed after {} retries, headers={}", retries, props.getHeaders());
            rabbitTemplate.send(pointRabbitProperties.getParkingLotExchange(), pointRabbitProperties.getParkingLotRoutingKey(), dlqMessage);
            fatalMessageLogger.logParkingLotMessage(dlqMessage);
        }
    }
}
