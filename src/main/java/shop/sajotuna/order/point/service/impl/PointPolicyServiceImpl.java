package shop.sajotuna.order.point.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.sajotuna.order.point.controller.request.PointPolicyUpdateRequest;
import shop.sajotuna.order.point.controller.response.PointPolicyResponse;
import shop.sajotuna.order.point.domain.PointPolicy;
import shop.sajotuna.order.point.domain.PointPolicyType;
import shop.sajotuna.order.point.exception.PointPolicyNotFoundException;
import shop.sajotuna.order.point.repository.PointPolicyRepository;
import shop.sajotuna.order.point.service.PointPolicyService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointPolicyServiceImpl implements PointPolicyService {

    private final PointPolicyRepository pointPolicyRepository;

    @Override
    public PointPolicy getPointPolicy(PointPolicyType type) {
        return pointPolicyRepository.findByType(type).orElseThrow(PointPolicyNotFoundException::new);
    }

    @Override
    public List<PointPolicyResponse> getAllPointPolicies() {
        return pointPolicyRepository.findAll()
                .stream()
                .map(PointPolicyResponse::from)
                .toList();
    }

    @Override
    @Transactional
    public void updatePointPolicy(Long policyId, PointPolicyUpdateRequest pointPolicyUpdateRequest) {
        PointPolicy pointPolicy = pointPolicyRepository.findById(policyId)
                .orElseThrow(PointPolicyNotFoundException::new);
        pointPolicy.update(pointPolicyUpdateRequest.getValue());
    }

}
