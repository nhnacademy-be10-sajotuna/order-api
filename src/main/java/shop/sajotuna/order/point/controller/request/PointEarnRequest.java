package shop.sajotuna.order.point.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import shop.sajotuna.order.point.domain.PointPolicyType;

@AllArgsConstructor
@Getter
public class PointEarnRequest {
    private Long userId;
    private int totalPrice;
    private PointPolicyType type;
}
