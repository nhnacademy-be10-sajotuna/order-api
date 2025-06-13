package shop.sajotuna.order.common.exception;

import org.springframework.http.HttpStatus;

public class NegativePointException extends ApiException {
    private static final String MESSAGE = "포인트는 음수로 설정할 수 없습니다.";

    public NegativePointException() {
        super(HttpStatus.BAD_REQUEST.value(), MESSAGE);
    }
}
