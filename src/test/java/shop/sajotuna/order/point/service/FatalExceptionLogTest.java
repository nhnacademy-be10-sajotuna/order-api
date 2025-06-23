package shop.sajotuna.order.point.service;

import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import shop.sajotuna.order.common.rabbitmq.FatalMessageLog;
import shop.sajotuna.order.common.rabbitmq.FatalMessageRepository;
import shop.sajotuna.order.common.rabbitmq.PointRabbitProperties;
import org.junit.jupiter.api.Test;
import org.awaitility.Awaitility;
import shop.sajotuna.order.point.controller.request.PointEvent;
import shop.sajotuna.order.point.domain.PointPolicyType;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class FatalExceptionLogTest {

    @Autowired
    private PointQueueService pointQueueService;

    @Autowired
    private PointRabbitProperties pointRabbitProperties;

    @Autowired
    private FatalMessageRepository fatalMessageRepository;

    @Test
    void whenFatalException_throwsImmediateAcknowledgeAmqpExceptionAndSaveLog() {
        PointEvent bad = new PointEvent(null, PointPolicyType.PURCHASE, 0);
        pointQueueService.sendEarnPointsMessage(bad);

        Awaitility.await()
                .atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> {
                    List<FatalMessageLog> logs = fatalMessageRepository.findAll();
                    assertFalse(logs.isEmpty());
                });
    }
}