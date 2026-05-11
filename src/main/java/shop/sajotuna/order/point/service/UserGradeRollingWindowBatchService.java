package shop.sajotuna.order.point.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import shop.sajotuna.order.orders.domain.OrderStatus;
import shop.sajotuna.order.orders.repository.OrderRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserGradeRollingWindowBatchService {

    private static final String DAILY_GRADE_REFRESH_CRON = "0 0 3 * * *";
    private static final List<OrderStatus> GRADE_TARGET_STATUSES = List.of(
            OrderStatus.PENDING,
            OrderStatus.SHIPPED,
            OrderStatus.DELIVERED
    );

    private final OrderRepository orderRepository;
    private final UserGradeService userGradeService;

    @Scheduled(cron = DAILY_GRADE_REFRESH_CRON)
    public void refreshExpiredWindowUserGrades() {
        refreshExpiredWindowUserGrades(LocalDate.now());
    }

    public void refreshExpiredWindowUserGrades(LocalDate today) {
        LocalDateTime from = today.minusDays(1).minusMonths(3).atStartOfDay();
        LocalDateTime to = today.minusMonths(3).atStartOfDay();

        List<Long> userIds = orderRepository.findUserIdsWithOrdersExpiringFromGradeWindow(
                from,
                to,
                GRADE_TARGET_STATUSES
        );

        userIds.forEach(this::updateGradeSafely);
        log.info("Refreshed user grades for expired rolling window. targetCount={}", userIds.size());
    }

    private void updateGradeSafely(Long userId) {
        try {
            userGradeService.updateGrade(userId);
        } catch (Exception e) {
            log.error("Failed to refresh user grade. userId={}", userId, e);
        }
    }
}
