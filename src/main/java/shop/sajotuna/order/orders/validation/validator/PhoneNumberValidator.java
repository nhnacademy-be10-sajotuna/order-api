package shop.sajotuna.order.orders.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import shop.sajotuna.order.orders.validation.annotation.PhoneNumber;

public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {

    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext context) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }

        String cleanNumber = phoneNumber.replaceAll("-", "");

        if (cleanNumber.length() < 10 || cleanNumber.length() > 11) {
            return false;
        }

        // 숫자만 포함되어 있는지 확인
        if (!cleanNumber.matches("\\d+")) {
            return false;
        }

        return cleanNumber.startsWith("010") ||
                cleanNumber.startsWith("011") ||
                cleanNumber.startsWith("016") ||
                cleanNumber.startsWith("017") ||
                cleanNumber.startsWith("018") ||
                cleanNumber.startsWith("019");
    }
}
