package shop.sajotuna.order.point.domain;

import org.springframework.http.HttpStatus;
import shop.sajotuna.common.exception.ApiException;

public class InsufficientPointException extends ApiException {

    private static final String MESSAGE = "포인트가 부족합니다 잔여 포인트 : %d, 필요 포인트 : %d";

    public InsufficientPointException(long remainPoint, long requiredPoint) {
        super(HttpStatus.BAD_REQUEST.value(), String.format(MESSAGE, remainPoint, requiredPoint));

    }
}
