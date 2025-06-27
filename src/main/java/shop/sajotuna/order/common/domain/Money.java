package shop.sajotuna.order.common.domain;

import jakarta.persistence.Embeddable;
import lombok.*;
import shop.sajotuna.order.common.exception.MoneyException;
import shop.sajotuna.order.common.exception.NullValueException;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
@EqualsAndHashCode
public class Money {
    private int amount;

    private Money(int amount) {
        validateAmount(amount);
        this.amount = amount;
    }

    private void validateAmount(int amount) {
        if (amount < 0) {
            throw new MoneyException.InvalidAmountException("금액은 0 이상이어야 합니다.");
        }
    }

    public static Money of(int amount) {
        return new Money(amount);
    }

    public static Money zero() {
        return new Money(0);
    }

    public Money plus(Money other) {
        if (other == null) {
            throw new NullValueException("더할 금액이 null입니다.");
        }
        return new Money(this.amount + other.amount);
    }

    public Money minus(Money other) {
        if (other == null) {
            throw new NullValueException("뺄 금액이 null입니다.");
        }
        if (this.amount < other.amount) {
            throw new MoneyException.InsufficientAmountException("차감할 금액이 현재 금액보다 큽니다.");
        }
        return new Money(this.amount - other.amount);
    }

    public Money multiply(int multiplier) {
        if (multiplier < 0) {
            throw new MoneyException.InvalidOperandException("곱셈 계수는 0 이상이어야 합니다.");
        }
        return new Money(this.amount * multiplier);
    }

    public Money divide(int divisor) {
        if (divisor <= 0) {
            throw new MoneyException.InvalidOperandException("나눗셈 값은 0보다 커야 합니다.");
        }
        return new Money(this.amount / divisor);
    }

    public boolean isZero() {
        return this.amount == 0;
    }

    public boolean isPositive() {
        return this.amount > 0;
    }

    public boolean isGreaterThan(Money other) {
        if (other == null) {
            throw new NullValueException("비교할 금액이 null입니다.");
        }
        return this.amount > other.amount;
    }

    public boolean isGreaterThanOrEqual(Money other) {
        if (other == null) {
            throw new NullValueException("비교할 금액이 null입니다.");
        }
        return this.amount >= other.amount;
    }

    public boolean isLessThan(Money other) {
        if (other == null) {
            throw new NullValueException("비교할 금액이 null입니다.");
        }
        return this.amount < other.amount;
    }

    public boolean isLessThanOrEqual(Money other) {
        if (other == null) {
            throw new NullValueException("비교할 금액이 null입니다.");
        }
        return this.amount <= other.amount;
    }

    /**
     *  퍼센트 계산을 위한 메서드
     *  현재 서비스에서는 퍼센트는 소수점 1자리까지만 지원합니다.
     */
    private Money percentage(BigDecimal percentage, RoundingMode roundingMode) {
        if (percentage == null) {
            throw new NullValueException("퍼센트 값이 null입니다.");
        }
        if (percentage.compareTo(BigDecimal.ZERO) < 0) {
            throw new MoneyException.InvalidOperandException("퍼센트 값은 0 이상이어야 합니다.");
        }
        
        BigDecimal result = BigDecimal.valueOf(this.amount)
                .multiply(percentage)
                .divide(BigDecimal.valueOf(1000), roundingMode);

        return new Money(result.intValue());
    }

    public Money percentage(int percentage, RoundingMode roundingMode) {
        return percentage(BigDecimal.valueOf(percentage), roundingMode);
    }
}

