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

import shop.sajotuna.order.point.domain.*;
import shop.sajotuna.order.point.service.dto.event.PointEarnRequest;
import shop.sajotuna.order.point.repository.UserPointRepository;

@ExtendWith(MockitoExtension.class)
class PointEarnConsumerTest {

    @Mock
    private UserPointRepository userPointRepo;

    @Mock
    private PointHistoryWriter pointHistoryWriter;

    @Mock
    private PointPolicyService pointPolicyService;

    @InjectMocks
    private PointEarnConsumer consumer;

    @Test
    void whenExistingUser_thenEarnsAndSavesHistory() {
        Long userId = 42L;
        PointEarnRequest event = mock(PointEarnRequest.class);
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

        assertEquals(earned, existingUserPoint.getRemainPoint().getAmount());

        verify(pointHistoryWriter).savePointEarnHistory(any(), any(), any());

        verify(userPointRepo, never()).save(any(UserPoint.class));
    }

    @Test
    void whenNewUser_thenCreatesUserPointAndSavesBoth() {
        Long userId = 99L;
        PointEarnRequest event = mock(PointEarnRequest.class);
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

        verify(pointHistoryWriter).savePointEarnHistory(any(), any(), any());
    }
}
