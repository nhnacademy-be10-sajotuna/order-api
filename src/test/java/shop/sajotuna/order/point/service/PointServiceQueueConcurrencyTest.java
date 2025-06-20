package shop.sajotuna.order.point.service;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import shop.sajotuna.order.common.rabbitmq.PointRabbitProperties;
import shop.sajotuna.order.point.controller.request.PointEarnRequest;
import shop.sajotuna.order.point.domain.UserPoint;
import shop.sajotuna.order.point.repository.UserPointRepository;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
public class PointServiceQueueConcurrencyTest {

    private static final int THREADS = 50;
    private static final Long USER_ID = 42L;
    private static final int POINT_AMOUNT = 10_000;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private UserPointRepository userPointRepository;

    @Autowired
    private PointRabbitProperties pointRabbitProperties;

    @BeforeEach
    void setUp() {
        rabbitTemplate.execute(ch -> {
            ch.queuePurge(pointRabbitProperties.getQueue());
            return null;
        });
        userPointRepository.deleteAll();
        userPointRepository.save(UserPoint.create(USER_ID));
    }

    @Test
    void concurrentEarningViaQueue_shouldAccumulateWithoutLostUpdates() throws InterruptedException {

        ExecutorService executor = Executors.newFixedThreadPool(THREADS);
        CountDownLatch latch = new CountDownLatch(THREADS);

        String exchange = pointRabbitProperties.getExchange();
        String routingKey = pointRabbitProperties.getRoutingKey();

        for (int i = 0; i < THREADS; i++) {
            executor.submit(() -> {
                try {
                    rabbitTemplate.convertAndSend(
                            exchange,
                            routingKey,
                            new PointEarnRequest(USER_ID, POINT_AMOUNT)
                    );
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        Awaitility.await()
                .atMost(Duration.ofSeconds(10))
                .untilAsserted(() -> {
                    UserPoint up = userPointRepository.findByUserId(USER_ID)
                            .orElseThrow();
                    assertEquals(POINT_AMOUNT * THREADS, up.getRemainPoint());
                    assertEquals(THREADS, up.getVersion());
                });

        executor.shutdown();
    }
}
