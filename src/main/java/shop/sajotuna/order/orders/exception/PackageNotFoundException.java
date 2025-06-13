package shop.sajotuna.order.orders.exception;

import org.springframework.http.HttpStatus;
import shop.sajotuna.order.common.exception.ApiException;

public class PackageNotFoundException extends ApiException {
    private static final String MESSAGE = "Package not found.";

    public PackageNotFoundException() {
        super(HttpStatus.NOT_FOUND.value(), MESSAGE);
    }
}
