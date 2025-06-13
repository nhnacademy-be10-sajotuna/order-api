package shop.sajotuna.order.orders.exception;

import org.springframework.http.HttpStatus;
import shop.sajotuna.order.common.exception.ApiException;

public class AlreadyUsedCouponException extends ApiException {
  private static final String MESSAGE = "This coupon already used.";

  public AlreadyUsedCouponException() {
    super(HttpStatus.BAD_REQUEST.value(), MESSAGE);
  }
}