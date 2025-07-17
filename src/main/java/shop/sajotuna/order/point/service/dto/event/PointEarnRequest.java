package shop.sajotuna.order.point.service.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.point.domain.PointPolicyType;

@AllArgsConstructor
@Getter
@ToString
public class PointEarnRequest {
    Long userId;
    PointPolicyType type;
    Money pointAmount;
}
