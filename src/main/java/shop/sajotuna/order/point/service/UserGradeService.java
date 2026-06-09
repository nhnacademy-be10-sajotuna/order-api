package shop.sajotuna.order.point.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.orders.service.pricing.OrderTotalPriceService;
import shop.sajotuna.order.point.controller.response.GradePointPolicyResponse;
import shop.sajotuna.order.point.domain.Grade;
import shop.sajotuna.order.point.domain.GradePointPolicy;
import shop.sajotuna.order.point.domain.UserGrade;
import shop.sajotuna.order.point.repository.GradePointPolicyQueryRepository;
import shop.sajotuna.order.point.repository.GradePointPolicyRepository;
import shop.sajotuna.order.point.repository.UserGradeRepository;

@Service
@RequiredArgsConstructor
public class UserGradeService {

    private final GradePointPolicyRepository gradePointPolicyRepository;
    private final OrderTotalPriceService orderTotalPriceService;
    private final GradePointPolicyQueryRepository gradePointPolicyQueryRepository;
    private final UserGradeRepository userGradeRepository;

    @Transactional(readOnly = true)
    public GradePointPolicyResponse getUserGrade(Long userId) {
        UserGrade userGrade = findOrCreateDefaultGrade(userId);
        return GradePointPolicyResponse.from(userGrade.getGrade());
    }

    @Transactional
    public void updateGrade(Long userId) {
        UserGrade userGrade = findOrCreateDefaultGrade(userId);
        Money totalOrderAmount = orderTotalPriceService.calculateTotalOrderAmount(userId);
        GradePointPolicy applicablePolicy =
                gradePointPolicyQueryRepository.findApplicablePolicy(totalOrderAmount.getAmount());

        userGrade.updateGrade(applicablePolicy);
        userGradeRepository.save(userGrade);
    }

    private UserGrade findOrCreateDefaultGrade(Long userId) {
        return userGradeRepository.findByUserId(userId)
                .orElseGet(() -> UserGrade.createForRegisterUser(
                        userId,
                        gradePointPolicyRepository.findByGrade(Grade.GENERAL)
                ));
    }
}
