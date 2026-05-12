package shop.sajotuna.order.point.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import shop.sajotuna.order.orders.domain.OrderStatus;
import shop.sajotuna.order.orders.repository.OrderRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserGradeRollingWindowBatchServiceTest {

    private final OrderRepository orderRepository = mock(OrderRepository.class);
    private final UserGradeService userGradeService = mock(UserGradeService.class);
    private final UserGradeRollingWindowBatchService batchService =
            new UserGradeRollingWindowBatchService(orderRepository, userGradeService);

    @Test
    @DisplayName("rolling window에서 제외되는 주문이 있는 회원만 등급을 재계산한다")
    void refreshExpiredWindowUserGrades_updatesOnlyExpiredWindowUsers() {
        LocalDate today = LocalDate.of(2026, 5, 12);
        LocalDateTime from = LocalDateTime.of(2026, 2, 11, 0, 0);
        LocalDateTime to = LocalDateTime.of(2026, 2, 12, 0, 0);
        List<OrderStatus> statuses = List.of(OrderStatus.PENDING, OrderStatus.SHIPPED, OrderStatus.DELIVERED);

        when(orderRepository.findUserIdsWithOrdersExpiringFromGradeWindow(from, to, statuses))
                .thenReturn(List.of(1L, 2L));

        batchService.refreshExpiredWindowUserGrades(today);

        verify(userGradeService).updateGrade(1L);
        verify(userGradeService).updateGrade(2L);
    }

    @Test
    @DisplayName("일부 회원 등급 갱신 실패 시 나머지 회원 갱신은 계속한다")
    void refreshExpiredWindowUserGrades_continuesAfterSingleUserFailure() {
        LocalDate today = LocalDate.of(2026, 5, 12);
        LocalDateTime from = LocalDateTime.of(2026, 2, 11, 0, 0);
        LocalDateTime to = LocalDateTime.of(2026, 2, 12, 0, 0);
        List<OrderStatus> statuses = List.of(OrderStatus.PENDING, OrderStatus.SHIPPED, OrderStatus.DELIVERED);

        when(orderRepository.findUserIdsWithOrdersExpiringFromGradeWindow(from, to, statuses))
                .thenReturn(List.of(1L, 2L));
        doThrow(new RuntimeException("temporary failure")).when(userGradeService).updateGrade(1L);

        batchService.refreshExpiredWindowUserGrades(today);

        verify(userGradeService).updateGrade(1L);
        verify(userGradeService).updateGrade(2L);
    }
}
