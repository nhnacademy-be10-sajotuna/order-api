package shop.sajotuna.order.coupon.exception;

import org.springframework.http.HttpStatus;
import shop.sajotuna.order.common.exception.ApiException;

public class NotUsedCouponException extends ApiException {

    private static final String MESSAGE = "쿠폰이 사용되지 않았습니다. 쿠폰 ID: %d";

    public NotUsedCouponException(Long id) {
        super(HttpStatus.CONFLICT.value(), String.format(MESSAGE, id));
    }
}
