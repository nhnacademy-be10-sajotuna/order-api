package shop.sajotuna.order.point.service;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import shop.sajotuna.order.point.service.dto.event.PointEarnRequest;
import shop.sajotuna.order.point.domain.PointPolicy;
import shop.sajotuna.order.point.domain.PointPolicyType;
import shop.sajotuna.order.point.domain.UserPoint;
import shop.sajotuna.order.point.repository.UserPointRepository;
import shop.sajotuna.order.common.domain.Money;

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
    private UserPointRepository userPointRepository;

    @Autowired
    private PointPolicyService pointPolicyService;

    @Autowired
    private PointQueueService pointQueueService;

    @BeforeEach
    void setUp() {
        userPointRepository.deleteAll();
        userPointRepository.save(UserPoint.create(USER_ID));
    }

    @Test
    void concurrentEarningViaQueue_shouldAccumulateWithoutLostUpdates() throws InterruptedException {

        PointEarnRequest event = new PointEarnRequest(USER_ID, PointPolicyType.PURCHASE, Money.of(POINT_AMOUNT));
        PointPolicy pointPolicy = pointPolicyService.getPointPolicy(event.getType());
        Money earnPoint = pointPolicy.calculatePoint(Money.of(POINT_AMOUNT));
        ExecutorService executor = Executors.newFixedThreadPool(THREADS);
        CountDownLatch latch = new CountDownLatch(THREADS);

        for (int i = 0; i < THREADS; i++) {
            executor.submit(() -> {
                try {
                    pointQueueService.sendEarnPointsMessage(event);
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
                    assertEquals(earnPoint.multiply(THREADS), up.getRemainPoint());
                    assertEquals(THREADS, up.getVersion());
                });

        executor.shutdown();
    }
}
