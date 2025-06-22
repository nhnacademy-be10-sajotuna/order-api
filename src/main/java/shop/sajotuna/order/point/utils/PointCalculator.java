package shop.sajotuna.order.point.utils;

import shop.sajotuna.order.point.controller.request.PointEvent;
import shop.sajotuna.order.point.domain.PointPolicyType;

public interface PointCalculator {
    boolean supports(PointPolicyType type);

    int calculate(PointEvent event);
}
