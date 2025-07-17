package shop.sajotuna.order.point.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import shop.sajotuna.order.point.rabbitmq.PointFatalMessageLog;
import shop.sajotuna.order.point.rabbitmq.PointFatalMessageRepository;
import shop.sajotuna.order.point.rabbitmq.PointRabbitProperties;
import org.junit.jupiter.api.Test;
import org.awaitility.Awaitility;
import shop.sajotuna.order.point.service.dto.event.PointEarnRequest;
import shop.sajotuna.order.point.domain.PointPolicyType;
import shop.sajotuna.order.common.domain.Money;

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
    private PointFatalMessageRepository fatalMessageRepository;

    @Test
    void whenFatalException_throwsImmediateAcknowledgeAmqpExceptionAndSaveLog() {
        PointEarnRequest bad = new PointEarnRequest(null, PointPolicyType.PURCHASE, Money.zero());
        pointQueueService.sendEarnPointsMessage(bad);

        Awaitility.await()
                .atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> {
                    List<PointFatalMessageLog> logs = fatalMessageRepository.findAll();
                    assertFalse(logs.isEmpty());
                });
    }
}