package shop.sajotuna.order.point.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.sajotuna.order.point.domain.PointPolicyType;
import shop.sajotuna.order.point.repository.PointPolicyRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class PointPolicyServiceImpl implements PointPolicyService {

    private final PointPolicyRepository pointPolicyRepository;

    @Override
    public int getPurchasePoint(int totalPrice) {
        BigDecimal rate = pointPolicyRepository.findByType(PointPolicyType.PURCHASE).getRate();
        BigDecimal price = BigDecimal.valueOf(totalPrice);

        return price.multiply(rate).setScale(0, RoundingMode.DOWN).intValue();
    }

    @Override
    public int getReviewPoint() {
        return pointPolicyRepository.findByType(PointPolicyType.REVIEW).getFixedPoint();
    }

    @Override
    public int getRegisterPoint() {
        return pointPolicyRepository.findByType(PointPolicyType.REGISTER).getFixedPoint();
    }

}
