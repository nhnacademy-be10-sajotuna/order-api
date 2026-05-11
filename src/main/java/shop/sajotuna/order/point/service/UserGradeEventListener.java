package shop.sajotuna.order.point.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import shop.sajotuna.order.point.service.dto.event.UserGradeRefreshEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserGradeEventListener {

    private final UserGradeService userGradeService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserGradeRefreshEvent(UserGradeRefreshEvent event) {
        log.info("Handling user grade refresh event after transaction commit: {}", event);
        userGradeService.updateGrade(event.getUserId());
    }
}
