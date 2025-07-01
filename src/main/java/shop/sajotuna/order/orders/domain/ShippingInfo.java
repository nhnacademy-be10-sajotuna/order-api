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

    private LocalDateTime expectedDeliveryDate;
    private LocalDateTime shippingStartDate;
    private LocalDateTime shippingEndDate;

    private ShippingInfo(String recipientName, String recipientPhoneNumber, String recipientEmail, String recipientAddress, LocalDateTime expectedDeliveryDate) {
        setRecipientName(recipientName);
        setRecipientPhoneNumber(recipientPhoneNumber);
        setRecipientEmail(recipientEmail);
        setRecipientAddress(recipientAddress);
        setExpectedDeliveryDate(expectedDeliveryDate);
        this.shippingStartDate = null;
        this.shippingEndDate = null;
    }

    public static ShippingInfo create(String recipientName, String recipientPhoneNumber, String recipientEmail, String recipientAddress, LocalDateTime expectedDeliveryDate) {
        return new ShippingInfo(recipientName, recipientPhoneNumber, recipientEmail, recipientAddress, expectedDeliveryDate);
    }

    protected void startShipping() {
        this.shippingStartDate = LocalDateTime.now();
    }

    protected void endShipping() {
        this.shippingEndDate = LocalDateTime.now();
    }

    private void setRecipientName(String recipientName) {
        OrderValidationUtils.validateName(recipientName);
        this.recipientName = recipientName;
    }

    private void setRecipientPhoneNumber(String recipientPhoneNumber) {
        OrderValidationUtils.validatePhoneNumber(recipientPhoneNumber);
        this.recipientPhoneNumber = recipientPhoneNumber;
    }

    private void setRecipientEmail(String recipientEmail) {
        OrderValidationUtils.validateEmail(recipientEmail);
        this.recipientEmail = recipientEmail;
    }

    private void setRecipientAddress(String recipientAddress) {
        if (recipientAddress == null || recipientAddress.trim().isEmpty()) {
            throw new NullValueException("배송 주소를 입력하세요.");
        }
        if (recipientAddress.trim().length() > 200) {
            throw new IllegalArgumentException("배송 주소는 200자를 초과할 수 없습니다.");
        }
        this.recipientAddress = recipientAddress;
    }

    // TODO: 배송 날짜 지정 로직 고도화
    private void setExpectedDeliveryDate(LocalDateTime expectedDeliveryDate) {
        if (expectedDeliveryDate == null) {
            expectedDeliveryDate = LocalDateTime.now().plusDays(2);
        } else if (expectedDeliveryDate.isBefore(LocalDateTime.now().plusDays(2))) {
            throw new IllegalArgumentException("배송 날짜는 최소 2일 이후로 설정해야 합니다.");
        }
        this.expectedDeliveryDate = expectedDeliveryDate;
    }
}