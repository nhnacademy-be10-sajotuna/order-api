package shop.sajotuna.order.point.controller.response;

import lombok.Builder;
import lombok.Getter;
import shop.sajotuna.order.point.domain.CalculationMode;
import shop.sajotuna.order.point.domain.PointPolicy;
import shop.sajotuna.order.point.domain.PointPolicyType;

@Builder
@Getter
public class PointPolicyResponse {
    private Long id;

    private PointPolicyType type;

    private int value;

    private CalculationMode calculationMode;

    public static PointPolicyResponse from(PointPolicy pointPolicy) {
        return PointPolicyResponse.builder()
                .id(pointPolicy.getId())
                .type(pointPolicy.getType())
                .value(pointPolicy.getValue())
                .calculationMode(pointPolicy.getCalculationMode())
                .build();
    }
}
