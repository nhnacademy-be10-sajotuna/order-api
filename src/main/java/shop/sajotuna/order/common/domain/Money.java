package shop.sajotuna.order.common.domain;

import jakarta.persistence.Embeddable;
import lombok.*;

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
            throw new IllegalArgumentException("금액은 0 이상이어야 합니다.");
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
            throw new IllegalArgumentException("더할 금액이 null입니다.");
        }
        return new Money(this.amount + other.amount);
    }

    public Money minus(Money other) {
        if (other == null) {
            throw new IllegalArgumentException("뺄 금액이 null입니다.");
        }
        if (this.amount < other.amount) {
            throw new IllegalArgumentException("차감할 금액이 현재 금액보다 큽니다.");
        }
        return new Money(this.amount - other.amount);
    }

    public Money multiply(int multiplier) {
        if (multiplier < 0) {
            throw new IllegalArgumentException("곱셈 계수는 0 이상이어야 합니다.");
        }
        return new Money(this.amount * multiplier);
    }

    public Money divide(int divisor) {
        if (divisor <= 0) {
            throw new IllegalArgumentException("나눗셈 값은 0보다 커야 합니다.");
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
            throw new IllegalArgumentException("비교할 금액이 null입니다.");
        }
        return this.amount > other.amount;
    }

    public boolean isGreaterThanOrEqual(Money other) {
        if (other == null) {
            throw new IllegalArgumentException("비교할 금액이 null입니다.");
        }
        return this.amount >= other.amount;
    }

    public boolean isLessThan(Money other) {
        if (other == null) {
            throw new IllegalArgumentException("비교할 금액이 null입니다.");
        }
        return this.amount < other.amount;
    }

    public boolean isLessThanOrEqual(Money other) {
        if (other == null) {
            throw new IllegalArgumentException("비교할 금액이 null입니다.");
        }
        return this.amount <= other.amount;
    }
}

