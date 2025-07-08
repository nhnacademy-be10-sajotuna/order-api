package shop.sajotuna.order.point.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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

    public GradePointPolicyResponse findAndUpdateGrade(Long userId) {
        // 회원 등급 정보가 없다면 기본 등급 지정
        UserGrade userGrade = userGradeRepository.findByUserId(userId)
                .orElse(UserGrade.createForRegisterUser(userId, gradePointPolicyRepository.findByGrade(Grade.GENERAL)));

        Money totalOrderAmount = orderTotalPriceService.calculateTotalOrderAmount(userId);

        GradePointPolicy applicablePolicy = gradePointPolicyQueryRepository.findApplicablePolicy(totalOrderAmount.getAmount());

        userGrade.updateGrade(applicablePolicy);
        UserGrade save = userGradeRepository.save(userGrade);

        return GradePointPolicyResponse.from(save.getGrade());
    }
}
