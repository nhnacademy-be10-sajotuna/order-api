package shop.sajotuna.order.point.exception;

import org.springframework.http.HttpStatus;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.common.exception.ApiException;

public class InsufficientPointException extends ApiException {

    private static final String MESSAGE = "포인트가 부족합니다 잔여 포인트 : %s, 필요 포인트 : %s";

    public InsufficientPointException(Money remainPoint, Money requiredPoint) {
        super(HttpStatus.BAD_REQUEST.value(), String.format(MESSAGE, remainPoint, requiredPoint));

    }
}
