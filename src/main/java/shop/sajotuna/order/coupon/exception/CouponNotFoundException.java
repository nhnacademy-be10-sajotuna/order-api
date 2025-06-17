package shop.sajotuna.order.coupon.exception;

import org.springframework.http.HttpStatus;
import shop.sajotuna.order.common.exception.ApiException;

public class CouponNotFoundException extends ApiException {
    private static final String MESSAGE = "Coupon not found.";

    public CouponNotFoundException() {
    super(HttpStatus.NOT_FOUND.value(), MESSAGE);
  }
}
