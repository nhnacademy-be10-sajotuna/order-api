package shop.sajotuna.order.point.repository;

import shop.sajotuna.order.point.domain.GradePointPolicy;

public interface GradePointPolicyQueryRepository {
    GradePointPolicy findApplicablePolicy(int totalAmount);
}
