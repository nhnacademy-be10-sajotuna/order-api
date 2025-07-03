package shop.sajotuna.order.stock.exception;

import org.springframework.http.HttpStatus;
import shop.sajotuna.order.common.exception.ApiException;

public class StockProcessingFailedException extends ApiException {
    private static final String MESSAGE = "재고 처리 최종 실패: ISBN=%s, 수량=%d";

    public StockProcessingFailedException(String isbn, int quantity) {
        super(HttpStatus.INTERNAL_SERVER_ERROR.value(), String.format(MESSAGE, isbn, quantity));
    }
}
