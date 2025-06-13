package shop.sajotuna.order.point.service;

import shop.sajotuna.order.point.domain.PointPolicy;
import shop.sajotuna.order.point.domain.PointPolicyType;

public interface PointPolicyService {
    PointPolicy getPointPolicy(PointPolicyType type);
}
