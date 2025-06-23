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
import shop.sajotuna.order.point.domain.*;
import shop.sajotuna.order.point.repository.UserPointRepository;
import shop.sajotuna.order.point.repository.PointHistoryRepository;

@ExtendWith(MockitoExtension.class)
class PointEarnConsumerTest {

    @Mock
    private UserPointRepository userPointRepo;

    @Mock
    private PointHistoryRepository historyRepo;

    @Mock
    private PointPolicyService pointPolicyService;

    @InjectMocks
    private PointEarnConsumer consumer;

    @Test
    void whenExistingUser_thenEarnsAndSavesHistory() {
        Long userId = 42L;
        PointEvent event = mock(PointEvent.class);
        when(event.getUserId()).thenReturn(userId);
        when(event.getType()).thenReturn(PointPolicyType.REVIEW);

        UserPoint existingUserPoint = UserPoint.create(userId);
        when(userPointRepo.findByUserId(userId)).thenReturn(Optional.of(existingUserPoint));

        int earned = 100;
        PointPolicy pointPolicy = PointPolicy.builder()
                .calculationMode(CalculationMode.FIXED)
                .value(earned)
                .build();
        when(pointPolicyService.getPointPolicy(any())).thenReturn(pointPolicy);

        consumer.onMessage(event);

        assertEquals(earned, existingUserPoint.getRemainPoint());

        verify(historyRepo).save(argThat(history ->
                history.getUserId().equals(userId) &&
                        history.getAmount() == earned &&
                        history.getType() == PointHistoryType.EARNED
        ));

        verify(userPointRepo, never()).save(any(UserPoint.class));
    }

    @Test
    void whenNewUser_thenCreatesUserPointAndSavesBoth() {
        Long userId = 99L;
        PointEvent event = mock(PointEvent.class);
        when(event.getUserId()).thenReturn(userId);
        when(event.getType()).thenReturn(PointPolicyType.REGISTER);

        when(userPointRepo.findByUserId(userId)).thenReturn(Optional.empty());
        UserPoint newUserPoint = UserPoint.create(userId);
        when(userPointRepo.save(any(UserPoint.class))).thenReturn(newUserPoint);

        int earned = 50;
        PointPolicy pointPolicy = PointPolicy.builder()
                .calculationMode(CalculationMode.FIXED)
                .value(earned)
                .build();
        when(pointPolicyService.getPointPolicy(any())).thenReturn(pointPolicy);

        consumer.onMessage(event);

        verify(userPointRepo).save(argThat(up -> up.getUserId().equals(userId)));

        verify(historyRepo).save(argThat(history ->
                history.getUserId().equals(userId) &&
                        history.getAmount() == earned
        ));
    }
}
