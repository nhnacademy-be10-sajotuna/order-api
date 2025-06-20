package shop.sajotuna.order.point.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import shop.sajotuna.order.point.controller.request.PointEarnRequest;
import shop.sajotuna.order.point.controller.response.PointHistoryResponse;
import shop.sajotuna.order.point.domain.*;
import shop.sajotuna.order.point.exception.InsufficientPointException;
import shop.sajotuna.order.point.exception.UserPointNotFoundException;
import shop.sajotuna.order.point.repository.PointHistoryRepository;
import shop.sajotuna.order.point.repository.UserPointRepository;
import shop.sajotuna.order.point.service.impl.PointServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PointServiceImplTest {

    private static final Long USER_ID = 1L;
    private static final int PURCHASE_AMOUNT = 10000;

    @InjectMocks
    private PointServiceImpl pointService;

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    @Mock
    private PointPolicyService pointPolicyService;

    @Mock
    private UserPointRepository userPointRepository;

    @Mock
    private PointQueueService pointQueueService;

    private UserPoint userPoint;

    @BeforeEach
    void setup() {
        userPoint = UserPoint.create(USER_ID);
    }

    @Test
    void earnPointsForPurchase_shouldSendQueueMessage() {
        // given
        PointPolicy policy = mock(PointPolicy.class);
        when(pointPolicyService.getPointPolicy(PointPolicyType.PURCHASE)).thenReturn(policy);
        when(policy.calculatePoint(PURCHASE_AMOUNT)).thenReturn(300);

        // when
        pointService.earnPointsForPurchase(USER_ID, PURCHASE_AMOUNT);

        // then
        verify(pointQueueService).sendEarnPointsMessage(
                argThat(req -> req.getUserId().equals(USER_ID) && req.getPointAmount() == 300));
    }

    @Test
    void earnPointsByType_register_shouldCreateUserPointAndSendQueueMessage() {
        // given
        when(pointPolicyService.getPointPolicy(PointPolicyType.REGISTER))
                .thenReturn(PointPolicy.builder().fixedPoint(500).type(PointPolicyType.REGISTER).build());

        // when
        pointService.earnPointsByType(USER_ID, PointPolicyType.REGISTER);

        // then
        verify(userPointRepository).save(any(UserPoint.class));
        verify(pointQueueService).sendEarnPointsMessage(
                argThat(req -> req.getUserId().equals(USER_ID) && req.getPointAmount() == 500));
    }

    @Test
    void earnPointsByType_review_shouldSendQueueMessageOnly() {
        // given
        when(pointPolicyService.getPointPolicy(PointPolicyType.REVIEW))
                .thenReturn(PointPolicy.builder().fixedPoint(100).type(PointPolicyType.REVIEW).build());
        when(userPointRepository.findByUserId(USER_ID)).thenReturn(Optional.of(userPoint));

        // when
        pointService.earnPointsByType(USER_ID, PointPolicyType.REVIEW);

        // then
        verify(pointQueueService).sendEarnPointsMessage(
                argThat(req -> req.getUserId().equals(USER_ID) && req.getPointAmount() == 100));
    }

    @Test
    void earnPointsByReturned_shouldSendQueueMessage() {
        // given
        PointEarnRequest request = new PointEarnRequest(USER_ID, 150);

        // when
        pointService.earnPointsByReturned(request);

        // then
        verify(pointQueueService).sendEarnPointsMessage(request);
    }

    @Test
    void redeemPoints_shouldUpdateUserPointAndSaveHistory() {
        // given
        userPoint.earnPoint(500);
        when(userPointRepository.findByUserId(USER_ID)).thenReturn(Optional.of(userPoint));
        PointHistory dummyHistory = PointHistory.createRedeemHistory(USER_ID, 200);
        when(pointHistoryRepository.save(any())).thenReturn(dummyHistory);

        // when
        PointHistoryResponse resp = pointService.redeemPoints(USER_ID, 200);

        // then
        assertThat(resp.getAmount()).isEqualTo(200);
        assertThat(resp.getType()).isEqualTo(PointType.REDEEMED);
        assertThat(userPoint.getRemainPoint()).isEqualTo(300);
    }

    @Test
    void redeemPoints_whenInsufficient_shouldThrowException() {
        // given
        when(userPointRepository.findByUserId(USER_ID)).thenReturn(Optional.of(userPoint));

        // then
        assertThatThrownBy(() -> pointService.redeemPoints(USER_ID, 100))
                .isInstanceOf(InsufficientPointException.class);
    }

    @Test
    void redeemPoints_whenUserNotFound_shouldThrowException() {
        when(userPointRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pointService.redeemPoints(USER_ID, 100))
                .isInstanceOf(UserPointNotFoundException.class);
    }

    @Test
    void getPointsByUserId_shouldMapToResponses() {
        // given
        PointHistory history = PointHistory.createEarnHistory(USER_ID, 100);
        when(pointHistoryRepository.getPointHistoriesByUserId(USER_ID))
                .thenReturn(List.of(history));

        // when
        List<PointHistoryResponse> responses = pointService.getPointsByUserId(USER_ID);

        // then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getAmount()).isEqualTo(100);
        assertThat(responses.get(0).getType()).isEqualTo(PointType.EARNED);
    }
}
