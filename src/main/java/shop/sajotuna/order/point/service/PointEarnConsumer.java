package shop.sajotuna.order.point.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import shop.sajotuna.order.point.controller.request.PointEvent;
import shop.sajotuna.order.point.domain.*;
import shop.sajotuna.order.point.repository.PointHistoryRepository;
import shop.sajotuna.order.point.repository.UserPointRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class PointEarnConsumer {

    private final UserPointRepository userPointRepo;
    private final PointHistoryRepository historyRepo;
    private final PointCalculationService pointCalculationService;

    @RabbitListener(queues = "${rabbitmq.point.queue}")
    @Transactional
    public void onMessage(PointEvent event) {
        log.info("Point Earned Event Received: {}", event);
        UserPoint userPoint = userPointRepo.findByUserId(event.getUserId())
                .orElseGet(() -> userPointRepo.save(UserPoint.create(event.getUserId())));

        int amount = pointCalculationService.calculatePoint(event);
        userPoint.earnPoint(amount);
        historyRepo.save(PointHistory.createEarnHistory(event.getUserId(), amount));
    }
}
