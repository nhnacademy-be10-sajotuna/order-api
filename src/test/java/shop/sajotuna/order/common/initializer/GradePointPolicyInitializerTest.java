package shop.sajotuna.order.common.initializer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.point.domain.Grade;
import shop.sajotuna.order.point.domain.GradePointPolicy;
import shop.sajotuna.order.point.repository.GradePointPolicyRepository;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GradePointPolicyInitializerTest {

    @Mock
    private GradePointPolicyRepository gradePointPolicyRepository;

    @Mock
    private ApplicationArguments applicationArguments;

    @InjectMocks
    private GradePointPolicyInitializer gradePointPolicyInitializer;

    @Test
    @DisplayName("등급별 포인트 정책이 없을 때 기본 정책들을 생성한다")
    void createDefaultGradePointPoliciesWhenEmpty() {
        // given
        when(gradePointPolicyRepository.count()).thenReturn(0L);

        // when
        gradePointPolicyInitializer.run(applicationArguments);

        // then
        verify(gradePointPolicyRepository).saveAll(argThat((List<GradePointPolicy> policies) -> 
            policies.size() == 4 &&
            policies.stream().anyMatch(policy -> 
                policy.getGrade() == Grade.GENERAL &&
                policy.getMinTotalOrderPrice().equals(Money.of(0)) &&
                policy.getMaxTotalOrderPrice().equals(Money.of(100000)) &&
                policy.getPointRate() == 1
            ) &&
            policies.stream().anyMatch(policy -> 
                policy.getGrade() == Grade.ROYAL &&
                policy.getMinTotalOrderPrice().equals(Money.of(100000)) &&
                policy.getMaxTotalOrderPrice().equals(Money.of(200000)) &&
                policy.getPointRate() == 2
            ) &&
            policies.stream().anyMatch(policy -> 
                policy.getGrade() == Grade.GOLD &&
                policy.getMinTotalOrderPrice().equals(Money.of(200000)) &&
                policy.getMaxTotalOrderPrice().equals(Money.of(300000)) &&
                policy.getPointRate() == 3
            ) &&
            policies.stream().anyMatch(policy -> 
                policy.getGrade() == Grade.PLATINUM &&
                policy.getMinTotalOrderPrice().equals(Money.of(300000)) &&
                policy.getMaxTotalOrderPrice().equals(Money.of(Integer.MAX_VALUE)) &&
                policy.getPointRate() == 5
            )
        ));
    }

    @Test
    @DisplayName("등급별 포인트 정책이 이미 존재할 때 추가로 생성하지 않는다")
    void doNotCreateWhenGradePointPoliciesExist() {
        // given
        when(gradePointPolicyRepository.count()).thenReturn(1L);

        // when
        gradePointPolicyInitializer.run(applicationArguments);

        // then
        verify(gradePointPolicyRepository, never()).saveAll(any(List.class));
    }
}