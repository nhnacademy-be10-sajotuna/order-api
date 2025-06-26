package shop.sajotuna.order.point.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.point.service.dto.event.PointEvent;
import shop.sajotuna.order.point.domain.*;
import shop.sajotuna.order.point.repository.UserPointRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class PointEarnConsumer {

    private final UserPointRepository userPointRepository;
    private final PointHistoryWriter pointHistoryWriter;
    private final PointPolicyService pointPolicyService;

    @RabbitListener(queues = "${rabbitmq.point.queue}")
    @Transactional
    public void onMessage(PointEvent event) {
        log.info("Point Earned Event Received: {}", event);
        UserPoint userPoint = userPointRepository.findByUserId(event.getUserId())
                .orElseGet(() -> userPointRepository.save(UserPoint.create(event.getUserId())));

        Money amount;
        if (event.getType() == PointPolicyType.RETURNED) {
            amount = event.getTotalPrice();
        } else {
            amount = pointPolicyService.getPointPolicy(event.getType()).calculatePoint(event.getTotalPrice());
        }
        userPoint.earnPoint(amount);
        pointHistoryWriter.savePointEarnHistory(event.getUserId(), amount, event.getType().getDescription());
    }
}
