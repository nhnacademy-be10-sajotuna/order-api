package shop.sajotuna.order.point.service;

import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import shop.sajotuna.order.common.rabbitmq.FatalMessageLog;
import shop.sajotuna.order.common.rabbitmq.FatalMessageRepository;
import shop.sajotuna.order.common.rabbitmq.PointRabbitProperties;
import shop.sajotuna.order.point.controller.request.PointEarnRequest;
import org.junit.jupiter.api.Test;
import org.awaitility.Awaitility;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class RabbitMqDlqRetryTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitAdmin rabbitAdmin;

    @Autowired
    private PointRabbitProperties pointRabbitProperties;

    @Autowired
    private FatalMessageRepository fatalMessageRepository;

    @Test
    void whenFatalException_throwsImmediateAcknowledgeAmqpExceptionAndSaveLog() {
     PointEarnRequest bad = new PointEarnRequest(-1L, 0);
        rabbitTemplate.convertAndSend(pointRabbitProperties.getExchange(), pointRabbitProperties.getRoutingKey(), bad);

        Awaitility.await()
                .atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> {
                    List<FatalMessageLog> logs = fatalMessageRepository.findAll();
                    assertFalse(logs.isEmpty());
                    assertTrue(logs.get(0).getExceptionType().contains("UserPointNotFoundException"));
                });
    }
}