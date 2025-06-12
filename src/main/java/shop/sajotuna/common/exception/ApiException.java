package shop.sajotuna.common.exception;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {
    private final int status;
    private final String message;

    public ApiException(int status, String message) {
        super(message);
        this.status = status;
        this.message = message;
    }
}