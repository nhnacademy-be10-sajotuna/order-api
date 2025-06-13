package shop.sajotuna.order.common.initializer;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import shop.sajotuna.order.point.domain.PointPolicy;
import shop.sajotuna.order.point.domain.PointPolicyType;
import shop.sajotuna.order.point.repository.PointPolicyRepository;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PointPolicyInitializer implements ApplicationRunner {

    private final PointPolicyRepository policyRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (policyRepository.count() == 0) {
            List<PointPolicy> defaults = List.of(
                    PointPolicy.builder()
                            .type(PointPolicyType.PURCHASE)
                            .rate(BigDecimal.valueOf(0.10))
                            .build(),
                    PointPolicy.builder()
                            .type(PointPolicyType.REVIEW)
                            .fixedPoint(200)
                            .build(),
                    PointPolicy.builder()
                            .type(PointPolicyType.REVIEW_WITH_IMAGE)
                            .fixedPoint(500)
                            .build(),
                    PointPolicy.builder()
                            .type(PointPolicyType.REGISTER)
                            .fixedPoint(5000)
                            .build()
            );
            policyRepository.saveAll(defaults);
        }
    }
}
