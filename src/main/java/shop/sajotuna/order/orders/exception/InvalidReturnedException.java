package shop.sajotuna.order.orders.exception;

import org.springframework.http.HttpStatus;
import shop.sajotuna.order.common.exception.ApiException;

public class InvalidReturnedException extends ApiException {

    private static final String MESSAGE = "주문 반품은 배송 된 상품만 가능합니다.";
    public InvalidReturnedException() {
        super(HttpStatus.BAD_REQUEST.value(), MESSAGE);
    }
}
