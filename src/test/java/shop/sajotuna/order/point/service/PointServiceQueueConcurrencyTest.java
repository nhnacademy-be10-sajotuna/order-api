package shop.sajotuna.order.point.service;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import shop.sajotuna.order.point.controller.request.PointEarnRequest;
import shop.sajotuna.order.point.domain.PointPolicyType;
import shop.sajotuna.order.point.domain.UserPoint;
import shop.sajotuna.order.point.repository.UserPointRepository;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
public class PointServiceQueueConcurrencyTest {

    private static final int THREADS         = 50;
    private static final Long USER_ID        = 42L;
    private static final int PURCHASE_AMOUNT = 10_000;

    @Container
    static RabbitMQContainer rabbitmq =
            new RabbitMQContainer("rabbitmq:3-management-alpine")
                    .withExposedPorts(5672)
                    .waitingFor(Wait.forListeningPort())
                    .withCreateContainerCmdModifier(cmd ->
                            cmd.getHostConfig()
                                    .withPortBindings(
                                            new PortBinding(
                                                    Ports.Binding.bindPort(5672),
                                                    new ExposedPort(5672)
                                            )
                                    )
                    );

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private UserPointRepository userPointRepository;

    @Autowired
    private PointPolicyService pointPolicyService;

    @BeforeEach
    void setUp() {
        rabbitTemplate.execute(ch -> {
            ch.queuePurge("point-queue");
            return null;
        });
        userPointRepository.deleteAll();
        userPointRepository.save(UserPoint.create(USER_ID));
    }

    @Test
    void concurrentEarningViaQueue_shouldAccumulateWithoutLostUpdates() throws InterruptedException {
        int perEarn = pointPolicyService
                .getPointPolicy(PointPolicyType.PURCHASE)
                .calculatePoint(PURCHASE_AMOUNT);

        ExecutorService executor = Executors.newFixedThreadPool(THREADS);
        CountDownLatch latch = new CountDownLatch(THREADS);

        String exchange   = "point-exchange";
        String routingKey = "point.earn";

        for (int i = 0; i < THREADS; i++) {
            executor.submit(() -> {
                try {
                    rabbitTemplate.convertAndSend(
                            exchange,
                            routingKey,
                            new PointEarnRequest(USER_ID, PURCHASE_AMOUNT, PointPolicyType.PURCHASE)
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
                    assertEquals(perEarn * THREADS, up.getRemainPoint());
                    assertEquals(THREADS, up.getVersion());
                });

        executor.shutdown();
    }
}
