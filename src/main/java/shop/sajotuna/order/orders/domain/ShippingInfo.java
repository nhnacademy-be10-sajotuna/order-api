package shop.sajotuna.order.orders.domain;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.cglib.core.Local;
import shop.sajotuna.order.common.exception.NullValueException;
import shop.sajotuna.order.orders.validation.validator.OrderValidationUtils;

import java.time.LocalDateTime;
import java.util.Date;

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

    private LocalDateTime expectedDeliveryDate;
    private LocalDateTime shippingStartDate;
    private LocalDateTime shippingEndDate;

    private ShippingInfo(String recipientName, String recipientPhoneNumber, String recipientEmail, String recipientAddress, LocalDateTime expectedDeliveryDate) {
        this.recipientName = recipientName;
        this.recipientPhoneNumber = recipientPhoneNumber;
        this.recipientEmail = recipientEmail;
        this.recipientAddress = recipientAddress;
        this.expectedDeliveryDate = expectedDeliveryDate;
        this.shippingStartDate = null;
        this.shippingEndDate = null;
    }

    public static ShippingInfo create(String recipientName, String recipientPhoneNumber, String recipientEmail, String recipientAddress, LocalDateTime expectedDeliveryDate) {
        validateRecipientName(recipientName);
        validateRecipientPhoneNumber(recipientPhoneNumber);
        validateRecipientEmail(recipientEmail);
        validateRecipientAddress(recipientAddress);
        validateExpectedDeliveryDate(expectedDeliveryDate);

        return new ShippingInfo(recipientName, recipientPhoneNumber, recipientEmail, recipientAddress, expectedDeliveryDate);
    }

    public void startShipping() {
        this.shippingStartDate = LocalDateTime.now();
    }

    public void endShipping() {
        this.shippingEndDate = LocalDateTime.now();
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
    // TODO: 배송 날짜 지정 로직 고도화

    private static void validateExpectedDeliveryDate(LocalDateTime shippingDate) {
        if (shippingDate == null) {
            shippingDate = LocalDateTime.now().plusDays(2);
        }
        if (shippingDate.isBefore(LocalDateTime.now().plusDays(2))) {
            throw new IllegalArgumentException("배송 날짜는 최소 2일 이후로 설정해야 합니다.");
        }
    }
}