package shop.sajotuna.order.orders.domain;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import shop.sajotuna.order.orders.validation.validator.OrderValidationUtils;

@Embeddable
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
@ToString
@EqualsAndHashCode
public class Orderer {

    private Long userId;
    private String ordererName;
    private String ordererPhoneNumber;
    private String ordererEmail;

    public Orderer(Long userId, String ordererName, String ordererPhoneNumber, String ordererEmail) {
        validate(userId, ordererName, ordererPhoneNumber, ordererEmail);
        this.userId = userId;
        this.ordererName = ordererName;
        this.ordererPhoneNumber = ordererPhoneNumber;
        this.ordererEmail = ordererEmail;
    }

    private void validate(Long userId, String ordererName, String ordererPhoneNumber, String ordererEmail) {
        if (userId != null) {
            validateUserId(userId);
        }
        validateOrdererName(ordererName);
        validateOrdererPhoneNumber(ordererPhoneNumber);
        validateOrdererEmail(ordererEmail);
    }

    private void validateUserId(Long userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("주문자 ID는 양수여야 합니다.");
        }
    }

    private void validateOrdererName(String ordererName) {
        OrderValidationUtils.validateName(ordererName);
    }

    private void validateOrdererPhoneNumber(String ordererPhoneNumber) {
        OrderValidationUtils.validatePhoneNumber(ordererPhoneNumber);
    }

    private void validateOrdererEmail(String ordererEmail) {
        OrderValidationUtils.validateEmail(ordererEmail);
    }

    public static Orderer createGuestOrderer(String ordererName, String ordererPhoneNumber, String ordererEmail) {
        return new Orderer(null, ordererName, ordererPhoneNumber, ordererEmail);
    }

    public static Orderer createUserOrderer(Long userId, String ordererName, String ordererPhoneNumber, String ordererEmail) {
        return new Orderer(userId, ordererName, ordererPhoneNumber, ordererEmail);
    }
}