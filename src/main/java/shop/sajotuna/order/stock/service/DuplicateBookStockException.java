package shop.sajotuna.order.stock.service;

import org.springframework.http.HttpStatus;
import shop.sajotuna.order.common.exception.ApiException;

public class DuplicateBookStockException extends ApiException {

    private static final String MESSAGE = "이미 존재하는 책 재고입니다.";

    public DuplicateBookStockException() {
        super(HttpStatus.BAD_REQUEST.value(), MESSAGE);
    }
}
