package shop.sajotuna.order.point.exception;

import org.springframework.http.HttpStatus;
import shop.sajotuna.order.common.exception.ApiException;

public class UserGradeNotFoundException extends ApiException {

    private static final String MESSAGE = "사용자 등급을 찾을 수 없습니다.";

    public UserGradeNotFoundException() {
        super(HttpStatus.NOT_FOUND.value(), MESSAGE);
    }
}
