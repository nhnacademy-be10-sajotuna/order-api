package shop.sajotuna.order.point.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PointEarnRequest {
    private Long userId;

    private int pointAmount;
}
