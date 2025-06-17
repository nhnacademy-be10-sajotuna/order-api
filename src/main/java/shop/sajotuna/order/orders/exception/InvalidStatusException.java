package shop.sajotuna.order.orders.exception;

import org.springframework.http.HttpStatus;
import shop.sajotuna.order.common.exception.ApiException;

public class InvalidStatusException extends ApiException {

    private static final String MESSAGE = "주문 취소는 배송전 주문만 가능합니다.";
    public InvalidStatusException() {
        super(HttpStatus.BAD_REQUEST.value(), MESSAGE);
    }
}
