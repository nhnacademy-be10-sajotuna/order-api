package shop.sajotuna.order.coupon.exception;

import org.springframework.http.HttpStatus;
import shop.sajotuna.order.common.exception.ApiException;

public class ExpiredCouponException extends ApiException {

    private static final String MESSAGE = "This coupon is expired.";

    public ExpiredCouponException() {
        super(HttpStatus.BAD_REQUEST.value(), MESSAGE);
    }
}
