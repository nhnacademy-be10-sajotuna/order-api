package shop.sajotuna.order.common.initializer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;
import shop.sajotuna.order.point.domain.CalculationMode;
import shop.sajotuna.order.point.domain.PointPolicy;
import shop.sajotuna.order.point.domain.PointPolicyType;
import shop.sajotuna.order.point.repository.PointPolicyRepository;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointPolicyInitializerTest {

    @Mock
    private PointPolicyRepository policyRepository;

    @Mock
    private ApplicationArguments applicationArguments;

    @InjectMocks
    private PointPolicyInitializer pointPolicyInitializer;

    @Test
    @DisplayName("포인트 정책이 없을 때 기본 정책들을 생성한다")
    void createDefaultPointPoliciesWhenEmpty() {
        // given
        when(policyRepository.count()).thenReturn(0L);

        // when
        pointPolicyInitializer.run(applicationArguments);

        // then
        verify(policyRepository).saveAll(argThat((List<PointPolicy> policies) -> 
            policies.size() == 4 &&
            policies.stream().anyMatch(policy -> 
                policy.getType() == PointPolicyType.PURCHASE &&
                policy.getCalculationMode() == CalculationMode.RATE &&
                policy.getValue() == 100
            ) &&
            policies.stream().anyMatch(policy -> 
                policy.getType() == PointPolicyType.REVIEW &&
                policy.getCalculationMode() == CalculationMode.FIXED &&
                policy.getValue() == 200
            ) &&
            policies.stream().anyMatch(policy -> 
                policy.getType() == PointPolicyType.REVIEW_WITH_IMAGE &&
                policy.getCalculationMode() == CalculationMode.FIXED &&
                policy.getValue() == 500
            ) &&
            policies.stream().anyMatch(policy -> 
                policy.getType() == PointPolicyType.REGISTER &&
                policy.getCalculationMode() == CalculationMode.FIXED &&
                policy.getValue() == 5000
            )
        ));
    }

    @Test
    @DisplayName("포인트 정책이 이미 존재할 때 추가로 생성하지 않는다")
    void doNotCreateWhenPointPoliciesExist() {
        // given
        when(policyRepository.count()).thenReturn(1L);

        // when
        pointPolicyInitializer.run(applicationArguments);

        // then
        verify(policyRepository, never()).saveAll(any(List.class));
    }
}