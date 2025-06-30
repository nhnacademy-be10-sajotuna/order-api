package shop.sajotuna.order.common.initializer;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.point.domain.Grade;
import shop.sajotuna.order.point.domain.GradePointPolicy;
import shop.sajotuna.order.point.repository.GradePointPolicyRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GradePointPolicyInitializer implements ApplicationRunner {

    private final GradePointPolicyRepository gradePointPolicyRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (gradePointPolicyRepository.count() == 0) {
            List<GradePointPolicy> defaults = List.of(
                    new GradePointPolicy(null, Grade.GENERAL, Money.of(0), Money.of(100000), 1),
                    new GradePointPolicy(null, Grade.ROYAL, Money.of(100000), Money.of(200000), 2),
                    new GradePointPolicy(null, Grade.GOLD, Money.of(200000), Money.of(300000), 3),
                    new GradePointPolicy(null, Grade.PLATINUM, Money.of(300000), Money.of(Integer.MAX_VALUE), 5)
            );
            gradePointPolicyRepository.saveAll(defaults);
        }
    }
}