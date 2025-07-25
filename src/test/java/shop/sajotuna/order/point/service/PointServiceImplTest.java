package shop.sajotuna.order.point.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import shop.sajotuna.order.point.service.dto.event.PointEarnRequest;
import shop.sajotuna.order.point.domain.UserGrade;
import shop.sajotuna.order.point.domain.GradePointPolicy;
import shop.sajotuna.order.point.domain.PointPolicy;
import shop.sajotuna.order.point.exception.UserGradeNotFoundException;
import shop.sajotuna.order.point.repository.UserGradeRepository;
import shop.sajotuna.order.common.domain.Money;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointServiceImplTest{

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    @Mock
    private UserPointRepository userPointRepository;

    @Mock
    private PointPolicyService pointPolicyService;

    @Mock
    private UserGradeRepository userGradeRepository;

    @InjectMocks
    private PointServiceImpl pointService;

    private static final String REDEEM_MESSAGE = "포인트 사용";
    private static final String RETURN_MESSAGE = "포인트 반환";

    @Test
    @DisplayName("getPointsById - 모든 포인트 기록 조회")
    void getPointsByUserId_shouldGetAllPointHistories() {
        // given
        Long userId = 1L;
        PointHistory pointHistory1 = PointHistory.createEarnHistory(userId, Money.of(1000), PointPolicyType.REVIEW.getDescription());
        PointHistory pointHistory2 = PointHistory.createRedeemHistory(userId, Money.of(500), REDEEM_MESSAGE);
        when(pointHistoryRepository.getPointHistoriesByUserId(userId)).thenReturn(List.of(pointHistory1, pointHistory2));

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
        when(pointHistoryRepository.save(Mockito.any(PointHistory.class))).thenReturn(pointHistory);

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

        verify(pointHistoryRepository, never()).save(Mockito.any(PointHistory.class));
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

    @Test
    @DisplayName("사용자 ID로 포인트 이력을 조회하면 PointHistoryResponse로 변환하여 반환한다")
    void getPointsByUserId_Success() {
        // Given
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        // Mock 데이터 생성
        List<PointHistory> histories = List.of(
                createPointHistory( userId, 1000, PointPolicyType.PURCHASE),
                createPointHistory( userId, 100, PointPolicyType.REVIEW)
        );
        Page<PointHistory> historyPage = new PageImpl<>(histories, pageable, 2);

        when(pointHistoryRepository.getPointHistoriesByUserIdOrderByCreatedAtDesc(userId, pageable))
                .thenReturn(historyPage);

        // When
        Page<PointHistoryResponse> result = pointService.getPointsByUserId(userId, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);

        // 첫 번째 응답 검증
        PointHistoryResponse firstResponse = result.getContent().get(0);
        assertThat(firstResponse.getUserId()).isEqualTo(userId);
        assertThat(firstResponse.getAmount()).isEqualTo(1000);

        // Repository 호출 검증
        verify(pointHistoryRepository).getPointHistoriesByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    @Test
    @DisplayName("포인트 이력이 없으면 빈 페이지를 반환한다")
    void getPointsByUserId_EmptyResult() {
        // Given
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        Page<PointHistory> emptyPage = Page.empty(pageable);

        when(pointHistoryRepository.getPointHistoriesByUserIdOrderByCreatedAtDesc(userId, pageable))
                .thenReturn(emptyPage);

        // When
        Page<PointHistoryResponse> result = pointService.getPointsByUserId(userId, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }

    @Test
    @DisplayName("페이징 정보가 올바르게 전달된다")
    void getPointsByUserId_PagingInfo() {
        // Given
        Long userId = 1L;
        int page = 1;
        int size = 5;
        Pageable pageable = PageRequest.of(page, size);

        List<PointHistory> histories = List.of(
                createPointHistory( userId, 1000, PointPolicyType.PURCHASE),
                createPointHistory( userId, 100, PointPolicyType.REVIEW),
                createPointHistory( userId, 100, PointPolicyType.REVIEW),
                createPointHistory( userId, 100, PointPolicyType.REVIEW),
                createPointHistory( userId, 100, PointPolicyType.REVIEW)
        );
        Page<PointHistory> historyPage = new PageImpl<>(histories, pageable, 15); // 전체 15개

        when(pointHistoryRepository.getPointHistoriesByUserIdOrderByCreatedAtDesc(userId, pageable))
                .thenReturn(historyPage);

        // When
        Page<PointHistoryResponse> result = pointService.getPointsByUserId(userId, pageable);

        // Then
        assertThat(result.getNumber()).isEqualTo(page);
        assertThat(result.getSize()).isEqualTo(size);
        assertThat(result.getTotalPages()).isEqualTo(3); // 15/5 = 3
        assertThat(result.getTotalElements()).isEqualTo(15);
    }

    @Test
    @DisplayName("포인트 반환 성공")
    void returnPoints_success() {
        // given
        Long userId = 1L;
        Money pointAmount = Money.of(500);
        UserPoint userPoint = new UserPoint(1L, userId, Money.of(1000), null);
        
        when(userPointRepository.findByUserId(userId)).thenReturn(Optional.of(userPoint));

        // when
        pointService.returnPoints(userId, pointAmount);

        // then
        verify(userPointRepository).findByUserId(userId);
        verify(pointHistoryRepository).save(any(PointHistory.class));
    }

    @Test
    @DisplayName("포인트 반환 실패 - 사용자를 찾을 수 없음")
    void returnPoints_userNotFound() {
        // given
        Long userId = 1L;
        Money pointAmount = Money.of(500);
        
        when(userPointRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> pointService.returnPoints(userId, pointAmount))
                .isInstanceOf(UserPointNotFoundException.class);
                
        verify(userPointRepository).findByUserId(userId);
        verify(pointHistoryRepository, never()).save(any(PointHistory.class));
    }

    @Test
    @DisplayName("포인트 적립 계산 성공")
    void earnPoints_success() {
        // given
        Long userId = 1L;
        PointPolicyType type = PointPolicyType.PURCHASE;
        Money pointAmount = Money.of(10000);
        
        PointPolicy pointPolicy = mock(PointPolicy.class);
        UserGrade userGrade = mock(UserGrade.class);
        GradePointPolicy gradePointPolicy = mock(GradePointPolicy.class);
        
        when(pointPolicyService.getPointPolicy(type)).thenReturn(pointPolicy);
        when(pointPolicy.calculatePoint(pointAmount)).thenReturn(Money.of(1000));
        when(userGradeRepository.findByUserId(userId)).thenReturn(Optional.of(userGrade));
        when(userGrade.getGrade()).thenReturn(gradePointPolicy);
        when(gradePointPolicy.calculatePoint(pointAmount)).thenReturn(Money.of(200));

        // when
        PointEarnRequest result = pointService.earnPoints(userId, type, pointAmount);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getType()).isEqualTo(type);
        assertThat(result.getPointAmount()).isEqualTo(Money.of(1200)); // 1000 + 200
        
        verify(pointPolicyService).getPointPolicy(type);
        verify(userGradeRepository).findByUserId(userId);
    }

    @Test
    @DisplayName("포인트 적립 계산 실패 - 사용자 등급을 찾을 수 없음")
    void earnPoints_userGradeNotFound() {
        // given
        Long userId = 1L;
        PointPolicyType type = PointPolicyType.PURCHASE;
        Money pointAmount = Money.of(10000);
        
        PointPolicy pointPolicy = mock(PointPolicy.class);
        
        when(pointPolicyService.getPointPolicy(type)).thenReturn(pointPolicy);
        when(pointPolicy.calculatePoint(pointAmount)).thenReturn(Money.of(1000));
        when(userGradeRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> pointService.earnPoints(userId, type, pointAmount))
                .isInstanceOf(UserGradeNotFoundException.class);
                
        verify(pointPolicyService).getPointPolicy(type);
        verify(userGradeRepository).findByUserId(userId);
    }

    @Test
    @DisplayName("포인트 적립 계산 성공 - 리뷰 포인트")
    void earnPoints_reviewPoints() {
        // given
        Long userId = 1L;
        PointPolicyType type = PointPolicyType.REVIEW;
        Money pointAmount = Money.of(5000);
        
        PointPolicy pointPolicy = mock(PointPolicy.class);
        UserGrade userGrade = mock(UserGrade.class);
        GradePointPolicy gradePointPolicy = mock(GradePointPolicy.class);
        
        when(pointPolicyService.getPointPolicy(type)).thenReturn(pointPolicy);
        when(pointPolicy.calculatePoint(pointAmount)).thenReturn(Money.of(100));
        when(userGradeRepository.findByUserId(userId)).thenReturn(Optional.of(userGrade));
        when(userGrade.getGrade()).thenReturn(gradePointPolicy);
        when(gradePointPolicy.calculatePoint(pointAmount)).thenReturn(Money.of(50));

        // when
        PointEarnRequest result = pointService.earnPoints(userId, type, pointAmount);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getType()).isEqualTo(type);
        assertThat(result.getPointAmount()).isEqualTo(Money.of(150)); // 100 + 50
        
        verify(pointPolicyService).getPointPolicy(type);
        verify(userGradeRepository).findByUserId(userId);
    }

    // 헬퍼 메서드
    private PointHistory createPointHistory(Long userId, int amount, PointPolicyType type) {
        return PointHistory.createEarnHistory(
                userId,
                Money.of(amount),
                type.getDescription()
        );
    }
}