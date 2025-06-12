package shop.sajotuna.common.exception;

import org.springframework.http.HttpStatus;

public class UserPointNotFoundException extends ApiException {

    private static final String MESSAGE = "User point not found.";

    public UserPointNotFoundException() {
        super(HttpStatus.NOT_FOUND.value(), MESSAGE);
    }
}
