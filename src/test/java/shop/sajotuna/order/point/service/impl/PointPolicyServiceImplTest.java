package shop.sajotuna.order.point.service.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;
import shop.sajotuna.order.point.controller.request.PointPolicyUpdateRequest;
import shop.sajotuna.order.point.controller.response.PointPolicyResponse;
import shop.sajotuna.order.point.domain.CalculationMode;
import shop.sajotuna.order.point.domain.PointPolicy;
import shop.sajotuna.order.point.domain.PointPolicyType;
import shop.sajotuna.order.point.exception.InvalidPointPolicyValueException;
import shop.sajotuna.order.point.exception.PointPolicyNotFoundException;
import shop.sajotuna.order.point.repository.PointPolicyRepository;
import shop.sajotuna.order.point.service.PointPolicyService;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class PointPolicyServiceImplTest {

    private static final int DEFAULT_VALUE = 100;
    private static final int UPDATED_VALUE = 10;
    private static final int PURCHASE_RATE_VALUE = 1000;
    private static final int ZERO_VALUE = 0;
    private static final int NEGATIVE_VALUE = -100;

    @Autowired
    private PointPolicyService pointPolicyService;

    @Autowired
    private PointPolicyRepository pointPolicyRepository;

    @BeforeEach
    void setUp() {
        pointPolicyRepository.deleteAll(); // 각 테스트 전 데이터 정리
    }

    @Nested
    @DisplayName("포인트 정책 조회")
    class GetPointPolicy {

        @Test
        @DisplayName("단건 조회 성공")
        void getPointPolicy_Success() {
            // given
            PointPolicy pointPolicy = createPointPolicy(PointPolicyType.REVIEW, CalculationMode.FIXED, DEFAULT_VALUE);
            pointPolicyRepository.save(pointPolicy);

            // when
            PointPolicy pointPolicyByType = pointPolicyService.getPointPolicy(PointPolicyType.REVIEW);

            // then
            assertThat(pointPolicyByType).isEqualTo(pointPolicy);
        }

        @Test
        @DisplayName("존재하지 않는 포인트 정책 조회 시 예외 발생")
        void getPointPolicy_NotFound_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> pointPolicyService.getPointPolicy(PointPolicyType.REGISTER))
                    .isInstanceOf(PointPolicyNotFoundException.class);
        }

        @Test
        @DisplayName("전체 조회 성공")
        void getAllPointPolicies_Success() {
            // given
            PointPolicy pointPolicy1 = createPointPolicy(PointPolicyType.REVIEW, CalculationMode.FIXED, DEFAULT_VALUE);
            PointPolicy pointPolicy2 = createPointPolicy(PointPolicyType.PURCHASE, CalculationMode.RATE, PURCHASE_RATE_VALUE);
            PointPolicy pointPolicy3 = createPointPolicy(PointPolicyType.REVIEW_WITH_IMAGE, CalculationMode.FIXED, DEFAULT_VALUE);
            pointPolicyRepository.saveAll(List.of(pointPolicy1, pointPolicy2, pointPolicy3));

            // when
            List<PointPolicyResponse> pointPolicies = pointPolicyService.getAllPointPolicies();

            // then
            assertThat(pointPolicies).hasSize(3)
                    .extracting("type", "calculationMode", "value")
                    .containsExactlyInAnyOrder(
                            tuple(PointPolicyType.REVIEW, CalculationMode.FIXED, DEFAULT_VALUE),
                            tuple(PointPolicyType.PURCHASE, CalculationMode.RATE, PURCHASE_RATE_VALUE),
                            tuple(PointPolicyType.REVIEW_WITH_IMAGE, CalculationMode.FIXED, DEFAULT_VALUE)
                    );
        }
    }

    @Nested
    @DisplayName("포인트 정책 수정")
    class UpdatePointPolicy {

        @Test
        @DisplayName("정상적인 값으로 수정 성공")
        void updatePointPolicy_Success() {
            // given
            PointPolicy pointPolicy = createPointPolicy(PointPolicyType.REVIEW, CalculationMode.FIXED, DEFAULT_VALUE);
            pointPolicyRepository.save(pointPolicy);

            PointPolicyUpdateRequest pointPolicyUpdateRequest = new PointPolicyUpdateRequest(UPDATED_VALUE);

            // when
            pointPolicyService.updatePointPolicy(pointPolicy.getId(), pointPolicyUpdateRequest);

            // then
            PointPolicy updatedPointPolicy = pointPolicyRepository.findById(pointPolicy.getId()).get();
            assertThat(updatedPointPolicy.getValue()).isEqualTo(UPDATED_VALUE);
        }

        @Test
        @DisplayName("0값으로 수정 시 예외 발생")
        void updatePointPolicy_WithZeroValue_ThrowsException() {
            // given
            PointPolicy pointPolicy = createPointPolicy(PointPolicyType.REVIEW, CalculationMode.FIXED, DEFAULT_VALUE);
            pointPolicyRepository.save(pointPolicy);

            PointPolicyUpdateRequest zeroValueRequest = new PointPolicyUpdateRequest(ZERO_VALUE);

            // when & then
            assertThatThrownBy(() -> pointPolicyService.updatePointPolicy(pointPolicy.getId(), zeroValueRequest))
                    .isInstanceOf(InvalidPointPolicyValueException.class);
        }

        @Test
        @DisplayName("음수값으로 수정 시 예외 발생")
        void updatePointPolicy_WithNegativeValue_ThrowsException() {
            // given
            PointPolicy pointPolicy = createPointPolicy(PointPolicyType.REVIEW, CalculationMode.FIXED, DEFAULT_VALUE);
            pointPolicyRepository.save(pointPolicy);

            PointPolicyUpdateRequest negativeValueRequest = new PointPolicyUpdateRequest(NEGATIVE_VALUE);

            // when & then
            assertThatThrownBy(() -> pointPolicyService.updatePointPolicy(pointPolicy.getId(), negativeValueRequest))
                    .isInstanceOf(InvalidPointPolicyValueException.class);
        }

        @Test
        @DisplayName("존재하지 않는 정책 수정 시 예외 발생")
        void updatePointPolicy_NotFoundPolicy_ThrowsException() {
            // given
            Long nonExistentId = 999L;
            PointPolicyUpdateRequest updateRequest = new PointPolicyUpdateRequest(UPDATED_VALUE);

            // when & then
            assertThatThrownBy(() -> pointPolicyService.updatePointPolicy(nonExistentId, updateRequest))
                    .isInstanceOf(PointPolicyNotFoundException.class);
        }
    }

    private PointPolicy createPointPolicy(PointPolicyType type, CalculationMode mode, int value) {
        return PointPolicy.builder()
                .type(type)
                .calculationMode(mode)
                .value(value)
                .build();
    }
}