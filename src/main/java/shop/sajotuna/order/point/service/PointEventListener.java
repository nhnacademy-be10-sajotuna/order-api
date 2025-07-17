package shop.sajotuna.order.point.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;
import shop.sajotuna.order.point.service.dto.event.PointEarnRequest;

@Slf4j
@Component
@RequiredArgsConstructor
public class PointEventListener {

    private final PointQueueService pointQueueService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePointEarnEvent(PointEarnRequest event) {
        log.info("Handling point earn event after transaction commit: {}", event);
        pointQueueService.sendEarnPointsMessage(event);
    }
}