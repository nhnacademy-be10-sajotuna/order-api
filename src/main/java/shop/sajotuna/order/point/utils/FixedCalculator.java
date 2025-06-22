package shop.sajotuna.order.point.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shop.sajotuna.order.point.controller.request.PointEvent;
import shop.sajotuna.order.point.domain.PointPolicyType;
import shop.sajotuna.order.point.service.PointPolicyService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FixedCalculator implements PointCalculator {

    private final PointPolicyService policyService;

    private final static List<PointPolicyType> SUPPORTED_TYPES = List.of(
            PointPolicyType.REVIEW,
            PointPolicyType.REVIEW_WITH_IMAGE,
            PointPolicyType.REGISTER);

    @Override
    public boolean supports(PointPolicyType type) {
        return SUPPORTED_TYPES.contains(type);
    }

    @Override
    public int calculate(PointEvent event) {
        return policyService.getPointPolicy(event.getType()).getFixedPoint();
    }
}
