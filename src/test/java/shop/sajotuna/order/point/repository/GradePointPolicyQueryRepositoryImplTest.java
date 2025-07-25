package shop.sajotuna.order.point.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.point.domain.Grade;
import shop.sajotuna.order.point.domain.GradePointPolicy;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class GradePointPolicyQueryRepositoryImplTest {

    @Autowired
    private GradePointPolicyQueryRepository gradePointPolicyQueryRepository;

    @Autowired
    private GradePointPolicyRepository gradePointPolicyRepository;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 생성
        createGradePointPolicies();
    }

    @Test
    @DisplayName("적용 가능한 정책 조회 성공 - GENERAL 등급")
    void findApplicablePolicy_general() {
        // given
        int totalAmount = 50000; // 5만원

        // when
        GradePointPolicy result = gradePointPolicyQueryRepository.findApplicablePolicy(totalAmount);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getGrade()).isEqualTo(Grade.GENERAL);
        assertThat(result.getMinTotalOrderPrice().getAmount()).isLessThanOrEqualTo(totalAmount);
    }

    @Test
    @DisplayName("적용 가능한 정책 조회 성공 - ROYAL 등급")
    void findApplicablePolicy_royal() {
        // given
        int totalAmount = 150000; // 15만원

        // when
        GradePointPolicy result = gradePointPolicyQueryRepository.findApplicablePolicy(totalAmount);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getGrade()).isEqualTo(Grade.ROYAL);
        assertThat(result.getMinTotalOrderPrice().getAmount()).isLessThanOrEqualTo(totalAmount);
    }

    @Test
    @DisplayName("적용 가능한 정책 조회 성공 - GOLD 등급")
    void findApplicablePolicy_gold() {
        // given
        int totalAmount = 200000;

        // when
        GradePointPolicy result = gradePointPolicyQueryRepository.findApplicablePolicy(totalAmount);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getGrade()).isEqualTo(Grade.GOLD);
        assertThat(result.getMinTotalOrderPrice().getAmount()).isLessThanOrEqualTo(totalAmount);
    }

    @Test
    @DisplayName("적용 가능한 정책 조회 성공 - PLATINUM 등급")
    void findApplicablePolicy_platinum() {
        // given
        int totalAmount = 600000; // 60만원

        // when
        GradePointPolicy result = gradePointPolicyQueryRepository.findApplicablePolicy(totalAmount);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getGrade()).isEqualTo(Grade.PLATINUM);
        assertThat(result.getMinTotalOrderPrice().getAmount()).isLessThanOrEqualTo(totalAmount);
    }

    @Test
    @DisplayName("적용 가능한 정책 조회 성공 - 경계값 테스트 (정확히 일치)")
    void findApplicablePolicy_exactBoundary() {
        // given
        int totalAmount = 100000; // 정확히 10만원 (ROYAL 등급 최소 금액)

        // when
        GradePointPolicy result = gradePointPolicyQueryRepository.findApplicablePolicy(totalAmount);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getGrade()).isEqualTo(Grade.ROYAL);
        assertThat(result.getMinTotalOrderPrice().getAmount()).isEqualTo(totalAmount);
    }

    @Test
    @DisplayName("적용 가능한 정책 조회 성공 - 최고 등급보다 높은 금액")
    void findApplicablePolicy_aboveHighestTier() {
        // given
        int totalAmount = 1000000; // 100만원 (PLATINUM보다 높음)

        // when
        GradePointPolicy result = gradePointPolicyQueryRepository.findApplicablePolicy(totalAmount);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getGrade()).isEqualTo(Grade.PLATINUM);
        assertThat(result.getMinTotalOrderPrice().getAmount()).isLessThanOrEqualTo(totalAmount);
    }

    @Test
    @DisplayName("적용 가능한 정책 조회 - 가장 높은 등급 선택 확인")
    void findApplicablePolicy_selectsHighestApplicableTier() {
        // given
        int totalAmount = 400000; // 40만원 (GENERAL, ROYAL, GOLD 모두 적용 가능)

        // when
        GradePointPolicy result = gradePointPolicyQueryRepository.findApplicablePolicy(totalAmount);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getGrade()).isEqualTo(Grade.PLATINUM); // 가장 높은 적용 가능한 등급
        assertThat(result.getMinTotalOrderPrice().getAmount()).isEqualTo(300000);
    }

    @Test
    @DisplayName("적용 가능한 정책 조회 성공 - 경계값 테스트 (1원 차이)")
    void findApplicablePolicy_boundaryPlusOne() {
        // given
        int totalAmount = 100001; // 10만 1원 (ROYAL 등급보다 1원 많음)

        // when
        GradePointPolicy result = gradePointPolicyQueryRepository.findApplicablePolicy(totalAmount);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getGrade()).isEqualTo(Grade.ROYAL);
        assertThat(result.getMinTotalOrderPrice().getAmount()).isLessThanOrEqualTo(totalAmount);
    }

    @Test
    @DisplayName("적용 가능한 정책 조회 실패 - 경계값 테스트 (1원 부족)")
    void findApplicablePolicy_boundaryMinusOne() {
        // given
        int totalAmount = 99999; // 9만 9999원 (ROYAL 등급보다 1원 적음)

        // when
        GradePointPolicy result = gradePointPolicyQueryRepository.findApplicablePolicy(totalAmount);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getGrade()).isEqualTo(Grade.GENERAL);
        assertThat(result.getMinTotalOrderPrice().getAmount()).isEqualTo(50000);
    }

    private void createGradePointPolicies() {
        // GENERAL 등급: 5만원 이상, 포인트율 1%
        GradePointPolicy generalPolicy = new GradePointPolicy(
                null,
                Grade.GENERAL,
                Money.of(50000),
                Money.of(99999),
                1
        );

        // ROYAL 등급: 10만원 이상, 포인트율 2%
        GradePointPolicy royalPolicy = new GradePointPolicy(
                null,
                Grade.ROYAL,
                Money.of(100000),
                Money.of(299999),
                2
        );

        // GOLD 등급: 30만원 이상, 포인트율 3%
        GradePointPolicy goldPolicy = new GradePointPolicy(
                null,
                Grade.GOLD,
                Money.of(300000),
                Money.of(499999),
                3
        );

        // PLATINUM 등급: 50만원 이상, 포인트율 5%
        GradePointPolicy platinumPolicy = new GradePointPolicy(
                null,
                Grade.PLATINUM,
                Money.of(500000),
                Money.of(Integer.MAX_VALUE),
                5
        );

        // 데이터 저장
        gradePointPolicyRepository.save(generalPolicy);
        gradePointPolicyRepository.save(royalPolicy);
        gradePointPolicyRepository.save(goldPolicy);
        gradePointPolicyRepository.save(platinumPolicy);
    }
}