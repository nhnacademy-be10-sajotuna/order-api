package shop.sajotuna.order.orders.domain;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import shop.sajotuna.order.common.exception.NullValueException;
import shop.sajotuna.order.orders.validation.validator.OrderValidationUtils;

import java.time.LocalDateTime;

@Embeddable
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@ToString
@EqualsAndHashCode
public class ShippingInfo {

    private String recipientName;
    private String recipientPhoneNumber;
    private String recipientEmail;
    private String recipientAddress;

    private LocalDateTime shippingDate;

    private ShippingInfo(String recipientName, String recipientPhoneNumber, String recipientEmail, String recipientAddress, LocalDateTime shippingDate) {
        this.recipientName = recipientName;
        this.recipientPhoneNumber = recipientPhoneNumber;
        this.recipientEmail = recipientEmail;
        this.recipientAddress = recipientAddress;
        this.shippingDate = shippingDate;
    }

    public static ShippingInfo create(String recipientName, String recipientPhoneNumber, String recipientEmail, String recipientAddress, LocalDateTime shippingDate) {
        validateRecipientName(recipientName);
        validateRecipientPhoneNumber(recipientPhoneNumber);
        validateRecipientEmail(recipientEmail);
        validateRecipientAddress(recipientAddress);
        validateShippingDate(shippingDate);

        return new ShippingInfo(recipientName, recipientPhoneNumber, recipientEmail, recipientAddress, shippingDate);
    }

    public void startShipping() {
        this.shippingDate = LocalDateTime.now();
    }

    private static void validateRecipientName(String recipientName) {
        OrderValidationUtils.validateName(recipientName);
    }

    private static void validateRecipientPhoneNumber(String recipientPhoneNumber) {
        OrderValidationUtils.validatePhoneNumber(recipientPhoneNumber);
    }

    private static void validateRecipientEmail(String recipientEmail) {
        OrderValidationUtils.validateEmail(recipientEmail);
    }

    private static void validateRecipientAddress(String recipientAddress) {
        if (recipientAddress == null || recipientAddress.trim().isEmpty()) {
            throw new NullValueException("배송 주소를 입력하세요.");
        }
        if (recipientAddress.trim().length() > 200) {
            throw new IllegalArgumentException("배송 주소는 200자를 초과할 수 없습니다.");
        }
    }

    private static void validateShippingDate(LocalDateTime shippingDate) {
        if (shippingDate == null) {
            throw new NullValueException("배송 날짜를 입력하세요.");
        }
        if (shippingDate.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("배송 날짜는 현재 시간 이후여야 합니다.");
        }
    }
}