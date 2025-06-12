package shop.sajotuna.order.point.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.sajotuna.order.point.domain.PointPolicy;
import shop.sajotuna.order.point.domain.PointPolicyType;
import shop.sajotuna.order.point.exception.PointPolicyNotFoundException;
import shop.sajotuna.order.point.repository.PointPolicyRepository;

@Service
@RequiredArgsConstructor
public class PointPolicyServiceImpl implements PointPolicyService {

    private final PointPolicyRepository pointPolicyRepository;

    @Override
    public PointPolicy getPointPolicy(PointPolicyType type) {
        return pointPolicyRepository.findByType(type).orElseThrow(() -> new PointPolicyNotFoundException(type));
    }

}
