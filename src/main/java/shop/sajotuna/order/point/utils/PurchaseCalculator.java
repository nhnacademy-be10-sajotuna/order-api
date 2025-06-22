package shop.sajotuna.order.point.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shop.sajotuna.order.point.controller.request.PointEvent;
import shop.sajotuna.order.point.domain.PointPolicyType;
import shop.sajotuna.order.point.service.PointPolicyService;

@Component
@RequiredArgsConstructor
public class PurchaseCalculator implements PointCalculator {

    private final PointPolicyService policyService;

    @Override
    public boolean supports(PointPolicyType type) {
        return type == PointPolicyType.PURCHASE;
    }

    @Override
    public int calculate(PointEvent event) {
        return policyService.getPointPolicy(event.getType())
                .calculatePoint(event.getTotalPrice());
    }
}

