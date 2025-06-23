package shop.sajotuna.order.point.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import shop.sajotuna.order.point.domain.PointPolicyType;

@AllArgsConstructor
@Getter
@ToString
public class PointEvent {
    Long userId;
    PointPolicyType type;
    Integer totalPrice;
}
