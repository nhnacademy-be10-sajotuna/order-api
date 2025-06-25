package shop.sajotuna.order.point.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import shop.sajotuna.order.point.domain.CalculationMode;
import shop.sajotuna.order.point.domain.PointPolicy;
import shop.sajotuna.order.point.domain.PointPolicyType;

@AllArgsConstructor
@Getter
public class PointPolicyResponse {
    private Long id;

    private PointPolicyType type;

    private int value;

    private CalculationMode calculationMode;

    public static PointPolicyResponse from(PointPolicy pointPolicy) {
        return new PointPolicyResponse(
                pointPolicy.getId(),
                pointPolicy.getType(),
                pointPolicy.getValue(),
                pointPolicy.getCalculationMode()
        );
    }
}
