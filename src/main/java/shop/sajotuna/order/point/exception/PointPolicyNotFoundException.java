package shop.sajotuna.order.point.exception;

import org.springframework.http.HttpStatus;
import shop.sajotuna.order.common.exception.ApiException;

public class PointPolicyNotFoundException extends ApiException {

    private static final String MESSAGE = "Point policy not found";

    public PointPolicyNotFoundException() {
        super(HttpStatus.NOT_FOUND.value(), MESSAGE);
    }
}
