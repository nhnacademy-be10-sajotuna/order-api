package shop.sajotuna.order.orders.exception;

import org.springframework.http.HttpStatus;
import shop.sajotuna.order.common.exception.ApiException;

public class TimeOutException extends ApiException {

  private static final String MESSAGE = "시간이 지나 반품이 불가능합니다.";
  public TimeOutException() {
    super(HttpStatus.BAD_REQUEST.value(), MESSAGE);
  }
}