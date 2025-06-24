package shop.sajotuna.order.point.service;

import jakarta.validation.Valid;
import shop.sajotuna.order.point.controller.request.PointPolicyUpdateRequest;
import shop.sajotuna.order.point.controller.response.PointPolicyResponse;
import shop.sajotuna.order.point.domain.PointPolicy;
import shop.sajotuna.order.point.domain.PointPolicyType;

import java.util.List;

public interface PointPolicyService {
    PointPolicy getPointPolicy(PointPolicyType type);

    List<PointPolicyResponse> getAllPointPolicies();

    void updatePointPolicy(Long policyId, @Valid PointPolicyUpdateRequest pointPolicyUpdateRequest);
}
