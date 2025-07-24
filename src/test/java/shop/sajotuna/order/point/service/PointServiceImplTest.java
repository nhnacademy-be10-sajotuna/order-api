package shop.sajotuna.order.point.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.sajotuna.order.point.controller.response.PointHistoryResponse;
import shop.sajotuna.order.point.domain.PointHistory;
import shop.sajotuna.order.point.domain.PointHistoryType;
import shop.sajotuna.order.point.domain.PointPolicyType;
import shop.sajotuna.order.point.domain.UserPoint;
import shop.sajotuna.order.point.exception.InsufficientPointException;
import shop.sajotuna.order.point.exception.UserPointNotFoundException;
import shop.sajotuna.order.point.repository.PointHistoryRepository;
import shop.sajotuna.order.point.repository.UserPointRepository;
import shop.sajotuna.order.point.service.impl.PointServiceImpl;
import shop.sajotuna.order.common.domain.Money;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointServiceImplTest{

    @Mock
    private PointHistoryRepository historyRepository;

    @Mock
    private UserPointRepository userPointRepository;

    @InjectMocks
    private PointServiceImpl pointService;

    private static final String REDEEM_MESSAGE = "포인트 사용";

    @Test
    @DisplayName("getPointsById - 모든 포인트 기록 조회")
    void getPointsByUserId_shouldGetAllPointHistories() {
        // given
        Long userId = 1L;
        PointHistory pointHistory1 = PointHistory.createEarnHistory(userId, Money.of(1000), PointPolicyType.REVIEW.getDescription());
        PointHistory pointHistory2 = PointHistory.createRedeemHistory(userId, Money.of(500), REDEEM_MESSAGE);
        when(historyRepository.getPointHistoriesByUserId(userId)).thenReturn(List.of(pointHistory1, pointHistory2));

        // when
        List<PointHistoryResponse> pointsByUserId = pointService.getPointsByUserId(userId);

        // then
        assertThat(pointsByUserId).hasSize(2)
                .extracting("userId", "amount", "type")
                .contains(tuple(userId, 1000, PointHistoryType.EARNED),
                        tuple(userId, 500, PointHistoryType.REDEEMED));
    }

    @Test
    @DisplayName("redeemPoints - 포인트 사용 성공")
    void redeemPoints_success() {
        // given
        Long userId = 1L;
        int pointAmount = 500;
        UserPoint userPoint = new UserPoint(1L, userId, Money.of(1000), null);
        when(userPointRepository.findByUserId(userId)).thenReturn(Optional.of(userPoint));
        PointHistory pointHistory = PointHistory.createRedeemHistory(userId, Money.of(pointAmount), REDEEM_MESSAGE);
        when(historyRepository.save(Mockito.any(PointHistory.class))).thenReturn(pointHistory);

        // when
        PointHistoryResponse response = pointService.redeemPoints(userId, Money.of(pointAmount));

        // then
        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getAmount()).isEqualTo(pointAmount);
        assertThat(response.getType()).isEqualTo(PointHistoryType.REDEEMED);
        verify(userPointRepository).findByUserId(userId);
    }

    @Test
    @DisplayName("redeemPoints - 포인트 부족 시 예외 발생")
    void redeemPoints_insufficientPoints() {
        // given
        Long userId = 1L;
        int pointAmount = 500;
        UserPoint userPoint = new UserPoint(1L, userId, Money.of(300), null);
        when(userPointRepository.findByUserId(userId)).thenReturn(Optional.of(userPoint));

        // when & then
        assertThatThrownBy(() -> pointService.redeemPoints(userId, Money.of(pointAmount)))
                .isInstanceOf(InsufficientPointException.class);

        verify(historyRepository, never()).save(Mockito.any(PointHistory.class));
    }

    @Test
    @DisplayName("redeemPoints - 유저 포인트 정보가 없을 시 예외 발생")
    void redeemPoints_userPointNotFound() {
        // given
        Long userId = 1L;
        int pointAmount = 500;
        when(userPointRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> pointService.redeemPoints(userId, Money.of(pointAmount)))
                .isInstanceOf(UserPointNotFoundException.class);
    }
}