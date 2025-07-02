package shop.sajotuna.order.stock.exception;

import org.springframework.http.HttpStatus;
import shop.sajotuna.order.common.exception.ApiException;

public class BookStockNotFoundException extends ApiException {
    private static final String MESSAGE = "책 재고를 찾을 수 없습니다.";

    public BookStockNotFoundException() {
        super(HttpStatus.NOT_FOUND.value(), MESSAGE);
    }
}
