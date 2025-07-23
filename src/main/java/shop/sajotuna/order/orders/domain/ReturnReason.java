package shop.sajotuna.order.orders.domain;

import lombok.Getter;
import shop.sajotuna.order.orders.exception.TimeOutException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Getter
public enum ReturnReason {

    // TODO: 반품 사유 세분화? 일단 임시로 지정
    UNUSED(10, true),
    DAMAGED(30, false),
    DEFECTIVE(30, false);

    private final int maxDays;
    private final boolean deductShippingFee;

    ReturnReason(int maxDays, boolean deductShippingFee) {
        this.maxDays = maxDays;
        this.deductShippingFee = deductShippingFee;
    }

    public void validateReturnPeriod(LocalDateTime shippingDate, LocalDateTime returnRequestDate) {
        long daysPassed = ChronoUnit.DAYS.between(shippingDate, returnRequestDate);
        if (daysPassed > maxDays) {
            throw new TimeOutException();
        }
    }
}
