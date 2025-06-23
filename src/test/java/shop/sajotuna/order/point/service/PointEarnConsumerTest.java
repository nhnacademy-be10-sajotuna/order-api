package shop.sajotuna.order.point.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

import shop.sajotuna.order.point.controller.request.PointEvent;
import shop.sajotuna.order.point.domain.PointType;
import shop.sajotuna.order.point.domain.UserPoint;
import shop.sajotuna.order.point.repository.UserPointRepository;
import shop.sajotuna.order.point.repository.PointHistoryRepository;

@ExtendWith(MockitoExtension.class)
class PointEarnConsumerTest {

    @Mock
    private UserPointRepository userPointRepo;

    @Mock
    private PointHistoryRepository historyRepo;

    @Mock
    private PointCalculationService pointCalculationService;

    @InjectMocks
    private PointEarnConsumer consumer;

    @Test
    void whenExistingUser_thenEarnsAndSavesHistory() {
        Long userId = 42L;
        PointEvent event = mock(PointEvent.class);
        when(event.getUserId()).thenReturn(userId);

        UserPoint existingUserPoint = UserPoint.create(userId);
        when(userPointRepo.findByUserId(userId)).thenReturn(Optional.of(existingUserPoint));

        int earned = 100;
        when(pointCalculationService.calculatePoint(event)).thenReturn(earned);

        consumer.onMessage(event);

        assertEquals(earned, existingUserPoint.getRemainPoint());

        verify(historyRepo).save(argThat(history ->
                history.getUserId().equals(userId) &&
                        history.getAmount() == earned &&
                        history.getType() == PointType.EARNED
        ));

        verify(userPointRepo, never()).save(any(UserPoint.class));
    }

    @Test
    void whenNewUser_thenCreatesUserPointAndSavesBoth() {
        Long userId = 99L;
        PointEvent event = mock(PointEvent.class);
        when(event.getUserId()).thenReturn(userId);

        when(userPointRepo.findByUserId(userId)).thenReturn(Optional.empty());
        UserPoint newUserPoint = UserPoint.create(userId);
        when(userPointRepo.save(any(UserPoint.class))).thenReturn(newUserPoint);

        int earned = 50;
        when(pointCalculationService.calculatePoint(event)).thenReturn(earned);

        consumer.onMessage(event);

        verify(userPointRepo).save(argThat(up -> up.getUserId().equals(userId)));

        verify(historyRepo).save(argThat(history ->
                history.getUserId().equals(userId) &&
                        history.getAmount() == earned
        ));
    }
}
