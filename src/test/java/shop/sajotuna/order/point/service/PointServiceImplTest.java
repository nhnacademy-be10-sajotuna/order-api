package shop.sajotuna.order.point.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import shop.sajotuna.order.point.controller.response.PointHistoryResponse;
import shop.sajotuna.order.point.domain.PointPolicyType;
import shop.sajotuna.order.point.domain.PointType;
import shop.sajotuna.order.point.domain.UserPoint;
import shop.sajotuna.order.point.exception.InsufficientPointException;
import shop.sajotuna.order.point.exception.UserPointNotFoundException;
import shop.sajotuna.order.point.repository.PointHistoryRepository;
import shop.sajotuna.order.point.repository.UserPointRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PointServiceImplTest {

    private static final Long USER_ID = 100L;
    private static final int PURCHASE_AMOUNT = 5000;

    @Autowired
    private PointService pointService;

    @Autowired
    private PointPolicyService pointPolicyService;

    @Autowired
    private UserPointRepository userPointRepository;

    @Autowired
    private PointHistoryRepository pointHistoryRepository;

    @BeforeEach
    void setUp() {
        pointHistoryRepository.deleteAll();
        userPointRepository.deleteAll();

        userPointRepository.save(UserPoint.create(USER_ID));
    }

    @Test
    void earnPointsForPurchase_shouldIncreaseRemainAndCreateHistory() {
        // given
        int expectedEarn = pointPolicyService
                .getPointPolicy(PointPolicyType.PURCHASE)
                .calculatePoint(PURCHASE_AMOUNT);

        // when: 두 번 연속 호출
        pointService.earnPointsForPurchase(USER_ID, PURCHASE_AMOUNT);
        pointService.earnPointsForPurchase(USER_ID, PURCHASE_AMOUNT);

        // then: remainPoint, version, 히스토리 레코드 확인
        UserPoint up = userPointRepository.findByUserId(USER_ID).orElseThrow();
        assertThat(up.getRemainPoint()).isEqualTo(expectedEarn * 2L);
        assertThat(up.getVersion()).isEqualTo(2);

        List<PointHistoryResponse> histories = pointService.getPointsByUserId(USER_ID);
        assertThat(histories).hasSize(2)
                .allMatch(h -> h.getAmount() == expectedEarn)
                .extracting(PointHistoryResponse::getType)
                .allMatch(t -> t == PointType.EARNED);
    }

    @Test
    void earnPointsByType_register_shouldCreateNewAndHistory() {
        // given
        userPointRepository.deleteAll();

        int expected = pointPolicyService
                .getPointPolicy(PointPolicyType.REGISTER)
                .getFixedPoint();

        // when
        pointService.earnPointsByType(USER_ID, PointPolicyType.REGISTER);

        // then
        UserPoint up = userPointRepository.findByUserId(USER_ID).orElseThrow();
        assertThat(up.getRemainPoint()).isEqualTo(expected);
        assertThat(up.getVersion()).isEqualTo(1);

        List<PointHistoryResponse> histories = pointService.getPointsByUserId(USER_ID);
        assertThat(histories).hasSize(1)
                .first()
                .satisfies(history -> {
                    assertThat(history.getAmount()).isEqualTo(expected);
                    assertThat(history.getType()).isEqualTo(PointType.EARNED);
                });
    }

    @Test
    void earnPointsByType_otherType_shouldAccumulate() {
        // given
        Long before = userPointRepository.findByUserId(USER_ID).get().getRemainPoint();
        int expected = pointPolicyService
                .getPointPolicy(PointPolicyType.REVIEW)
                .getFixedPoint();

        // when
        pointService.earnPointsByType(USER_ID, PointPolicyType.REVIEW);

        // then
        UserPoint up = userPointRepository.findByUserId(USER_ID).get();
        assertThat(up.getRemainPoint()).isEqualTo(before + expected);
        assertThat(up.getVersion()).isEqualTo(1);

        List<PointHistoryResponse> histories = pointService.getPointsByUserId(USER_ID);
        assertThat(histories).hasSize(1)
                .first()
                .satisfies(h -> {
                    assertThat(h.getAmount()).isEqualTo(expected);
                    assertThat(h.getType()).isEqualTo(PointType.EARNED);
                });
    }

    @Test
    void redeemPoints_withSufficientRemain_shouldDeductAndCreateHistory() {
        pointService.earnPointsForPurchase(USER_ID, PURCHASE_AMOUNT);
        int toRedeem = pointPolicyService
                .getPointPolicy(PointPolicyType.PURCHASE)
                .calculatePoint(PURCHASE_AMOUNT);

        // when
        PointHistoryResponse resp = pointService.redeemPoints(USER_ID, toRedeem);

        // then
        UserPoint up = userPointRepository.findByUserId(USER_ID).get();
        assertThat(up.getRemainPoint()).isZero();
        assertThat(up.getVersion()).isEqualTo(2);

        List<PointHistoryResponse> histories = pointService.getPointsByUserId(USER_ID);
        assertThat(histories).hasSize(2);
        assertThat(histories.get(1).getType()).isEqualTo(PointType.REDEEMED);
        assertThat(histories.get(1).getAmount()).isEqualTo(toRedeem);
    }

    @Test
    void redeemPoints_whenInsufficient_shouldThrow() {
        assertThatThrownBy(() -> pointService.redeemPoints(USER_ID, 1))
                .isInstanceOf(InsufficientPointException.class);
    }

    @Test
    void operations_onNonexistentUser_shouldThrow() {
        assertThatThrownBy(() -> pointService.earnPointsForPurchase(999L, 1000))
                .isInstanceOf(UserPointNotFoundException.class);
        assertThatThrownBy(() -> pointService.redeemPoints(999L, 1))
                .isInstanceOf(UserPointNotFoundException.class);
    }
}
