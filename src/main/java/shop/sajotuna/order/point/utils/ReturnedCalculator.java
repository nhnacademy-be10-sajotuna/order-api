package shop.sajotuna.order.point.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shop.sajotuna.order.point.controller.request.PointEvent;
import shop.sajotuna.order.point.domain.PointPolicyType;

@Component
@RequiredArgsConstructor
public class ReturnedCalculator implements PointCalculator {

    @Override
    public boolean supports(PointPolicyType type) {
        return type == PointPolicyType.RETURNED;
    }

    @Override
    public int calculate(PointEvent event) {
        return event.getTotalPrice();
    }
}
