package shop.sajotuna.order.point.exception;

import org.springframework.http.HttpStatus;
import shop.sajotuna.order.common.exception.ApiException;
import shop.sajotuna.order.point.domain.PointPolicyType;

public class PointPolicyNotFoundException extends ApiException {

    private static final String MESSAGE = "Point policy not found for type: %s";

    public PointPolicyNotFoundException(PointPolicyType type) {
        super(HttpStatus.NOT_FOUND.value(), String.format(MESSAGE, type));
    }
}
