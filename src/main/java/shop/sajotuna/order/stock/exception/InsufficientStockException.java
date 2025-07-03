package shop.sajotuna.order.stock.exception;

import org.springframework.http.HttpStatus;
import shop.sajotuna.order.common.exception.ApiException;

public class InsufficientStockException extends ApiException {

    private static final String MESSAGE = "재고가 부족합니다.";
    public InsufficientStockException() {
        super(HttpStatus.BAD_REQUEST.value(), MESSAGE);
    }
}
