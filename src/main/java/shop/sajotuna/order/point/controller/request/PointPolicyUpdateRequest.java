package shop.sajotuna.order.point.controller.request;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PointPolicyUpdateRequest {

    @Min(1)
    private int value;
}
