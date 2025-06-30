package shop.sajotuna.order.orders.validation.validator;

import shop.sajotuna.order.common.exception.NullValueException;

import java.util.regex.Pattern;

public final class OrderValidationUtils {

    private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile("^010-\\d{4}-\\d{4}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private OrderValidationUtils() {}

    public static void validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new NullValueException("전화번호는 필수입니다.");
        }

        String trimmedPhone = phoneNumber.trim();
        if (!PHONE_NUMBER_PATTERN.matcher(trimmedPhone).matches()) {
            throw new IllegalArgumentException("전화번호는 010-1234-5678 형식이어야 합니다.");
        }
    }

    public static void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new NullValueException("이메일은 필수입니다.");
        }

        String trimmedEmail = email.trim();
        if (trimmedEmail.length() > 100) {
            throw new IllegalArgumentException("이메일은 100자를 초과할 수 없습니다.");
        }

        if (!EMAIL_PATTERN.matcher(trimmedEmail).matches()) {
            throw new IllegalArgumentException("올바른 이메일 형식이 아닙니다.");
        }
    }

    public static void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new NullValueException("이름은 필수입니다.");
        }

        String trimmedName = name.trim();
        if (trimmedName.length() < 2) {
            throw new IllegalArgumentException("이름은 최소 2자 이상이어야 합니다.");
        }

        if (trimmedName.length() > 50) {
            throw new IllegalArgumentException("이름은 50자를 초과할 수 없습니다.");
        }

        if (!trimmedName.matches("^[가-힣a-zA-Z\\s]+$")) {
            throw new IllegalArgumentException("이름은 한글, 영문, 공백만 입력 가능합니다.");
        }
    }
}