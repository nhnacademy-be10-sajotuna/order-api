package shop.sajotuna.order.coupon.exception;

import org.springframework.http.HttpStatus;
import shop.sajotuna.order.common.exception.ApiException;

public class CouponNotFoundException extends ApiException {
    private static final String MESSAGE = "Coupon not found: %s";

    public CouponNotFoundException(Long couponId) {
        super(HttpStatus.NOT_FOUND.value(), String.format(MESSAGE, couponId));
    }

    public CouponNotFoundException(String couponName) {
        super(HttpStatus.NOT_FOUND.value(), String.format(MESSAGE, couponName));
    }
}
