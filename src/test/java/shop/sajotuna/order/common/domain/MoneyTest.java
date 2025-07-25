package shop.sajotuna.order.common.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import shop.sajotuna.order.common.exception.MoneyException;
import shop.sajotuna.order.common.exception.NullValueException;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.assertj.core.api.Assertions.*;

class MoneyTest {

    @Test
    @DisplayName("Money 생성 - 정상 금액")
    void createMoney_ValidAmount() {
        Money money = Money.of(1000);
        
        assertThat(money.getAmount()).isEqualTo(1000);
    }

    @Test
    @DisplayName("Money 생성 - 0원")
    void createMoney_ZeroAmount() {
        Money money = Money.of(0);
        
        assertThat(money.getAmount()).isEqualTo(0);
        assertThat(money.isZero()).isTrue();
    }

    @Test
    @DisplayName("Money.zero() 정적 팩토리 메서드")
    void createMoney_ZeroFactory() {
        Money money = Money.zero();
        
        assertThat(money.getAmount()).isEqualTo(0);
        assertThat(money.isZero()).isTrue();
    }

    @Test
    @DisplayName("Money 생성 - 음수 금액으로 예외 발생")
    void createMoney_NegativeAmount_ThrowsException() {
        assertThatThrownBy(() -> Money.of(-1000))
                .isInstanceOf(MoneyException.InvalidAmountException.class)
                .hasMessage("금액은 0 이상이어야 합니다.");
    }

    @Test
    @DisplayName("Money 덧셈 - 정상")
    void plus_ValidMoney() {
        Money money1 = Money.of(1000);
        Money money2 = Money.of(2000);
        
        Money result = money1.plus(money2);
        
        assertThat(result.getAmount()).isEqualTo(3000);
    }

    @Test
    @DisplayName("Money 덧셈 - null로 예외 발생")
    void plus_NullMoney_ThrowsException() {
        Money money = Money.of(1000);
        
        assertThatThrownBy(() -> money.plus(null))
                .isInstanceOf(NullValueException.class)
                .hasMessage("더할 금액이 null입니다.");
    }

    @Test
    @DisplayName("Money 뺄셈 - 정상")
    void minus_ValidMoney() {
        Money money1 = Money.of(3000);
        Money money2 = Money.of(1000);
        
        Money result = money1.minus(money2);
        
        assertThat(result.getAmount()).isEqualTo(2000);
    }

    @Test
    @DisplayName("Money 뺄셈 - null로 예외 발생")
    void minus_NullMoney_ThrowsException() {
        Money money = Money.of(1000);
        
        assertThatThrownBy(() -> money.minus(null))
                .isInstanceOf(NullValueException.class)
                .hasMessage("뺄 금액이 null입니다.");
    }

    @Test
    @DisplayName("Money 뺄셈 - 부족한 금액으로 예외 발생")
    void minus_InsufficientAmount_ThrowsException() {
        Money money1 = Money.of(1000);
        Money money2 = Money.of(2000);
        
        assertThatThrownBy(() -> money1.minus(money2))
                .isInstanceOf(MoneyException.InsufficientAmountException.class)
                .hasMessage("차감할 금액이 현재 금액보다 큽니다.");
    }

    @Test
    @DisplayName("Money 곱셈 - 정상")
    void multiply_ValidMultiplier() {
        Money money = Money.of(1000);
        
        Money result = money.multiply(3);
        
        assertThat(result.getAmount()).isEqualTo(3000);
    }

    @Test
    @DisplayName("Money 곱셈 - 0으로 곱하기")
    void multiply_ZeroMultiplier() {
        Money money = Money.of(1000);
        
        Money result = money.multiply(0);
        
        assertThat(result.getAmount()).isEqualTo(0);
        assertThat(result.isZero()).isTrue();
    }

    @Test
    @DisplayName("Money 곱셈 - 음수로 예외 발생")
    void multiply_NegativeMultiplier_ThrowsException() {
        Money money = Money.of(1000);
        
        assertThatThrownBy(() -> money.multiply(-2))
                .isInstanceOf(MoneyException.InvalidOperandException.class)
                .hasMessage("곱셈 계수는 0 이상이어야 합니다.");
    }

    @Test
    @DisplayName("Money 나눗셈 - 정상")
    void divide_ValidDivisor() {
        Money money = Money.of(3000);
        
        Money result = money.divide(3);
        
        assertThat(result.getAmount()).isEqualTo(1000);
    }

    @Test
    @DisplayName("Money 나눗셈 - 0으로 나누기 예외")
    void divide_ZeroDivisor_ThrowsException() {
        Money money = Money.of(1000);
        
        assertThatThrownBy(() -> money.divide(0))
                .isInstanceOf(MoneyException.InvalidOperandException.class)
                .hasMessage("나눗셈 값은 0보다 커야 합니다.");
    }

    @Test
    @DisplayName("Money 나눗셈 - 음수로 나누기 예외")
    void divide_NegativeDivisor_ThrowsException() {
        Money money = Money.of(1000);
        
        assertThatThrownBy(() -> money.divide(-2))
                .isInstanceOf(MoneyException.InvalidOperandException.class)
                .hasMessage("나눗셈 값은 0보다 커야 합니다.");
    }

    @Test
    @DisplayName("isZero() 메서드 테스트")
    void isZero_Test() {
        Money zeroMoney = Money.of(0);
        Money nonZeroMoney = Money.of(1000);
        
        assertThat(zeroMoney.isZero()).isTrue();
        assertThat(nonZeroMoney.isZero()).isFalse();
    }

    @Test
    @DisplayName("isPositive() 메서드 테스트")
    void isPositive_Test() {
        Money zeroMoney = Money.of(0);
        Money positiveMoney = Money.of(1000);
        
        assertThat(zeroMoney.isPositive()).isFalse();
        assertThat(positiveMoney.isPositive()).isTrue();
    }

    @Test
    @DisplayName("isGreaterThan() 메서드 테스트")
    void isGreaterThan_Test() {
        Money money1 = Money.of(2000);
        Money money2 = Money.of(1000);
        Money money3 = Money.of(2000);
        
        assertThat(money1.isGreaterThan(money2)).isTrue();
        assertThat(money2.isGreaterThan(money1)).isFalse();
        assertThat(money1.isGreaterThan(money3)).isFalse();
    }

    @Test
    @DisplayName("isGreaterThan() - null로 예외 발생")
    void isGreaterThan_NullMoney_ThrowsException() {
        Money money = Money.of(1000);
        
        assertThatThrownBy(() -> money.isGreaterThan(null))
                .isInstanceOf(NullValueException.class)
                .hasMessage("비교할 금액이 null입니다.");
    }

    @Test
    @DisplayName("isGreaterThanOrEqual() 메서드 테스트")
    void isGreaterThanOrEqual_Test() {
        Money money1 = Money.of(2000);
        Money money2 = Money.of(1000);
        Money money3 = Money.of(2000);
        
        assertThat(money1.isGreaterThanOrEqual(money2)).isTrue();
        assertThat(money1.isGreaterThanOrEqual(money3)).isTrue();
        assertThat(money2.isGreaterThanOrEqual(money1)).isFalse();
    }

    @Test
    @DisplayName("isGreaterThanOrEqual() - null로 예외 발생")
    void isGreaterThanOrEqual_NullMoney_ThrowsException() {
        Money money = Money.of(1000);
        
        assertThatThrownBy(() -> money.isGreaterThanOrEqual(null))
                .isInstanceOf(NullValueException.class)
                .hasMessage("비교할 금액이 null입니다.");
    }

    @Test
    @DisplayName("isLessThan() 메서드 테스트")
    void isLessThan_Test() {
        Money money1 = Money.of(1000);
        Money money2 = Money.of(2000);
        Money money3 = Money.of(1000);
        
        assertThat(money1.isLessThan(money2)).isTrue();
        assertThat(money2.isLessThan(money1)).isFalse();
        assertThat(money1.isLessThan(money3)).isFalse();
    }

    @Test
    @DisplayName("isLessThan() - null로 예외 발생")
    void isLessThan_NullMoney_ThrowsException() {
        Money money = Money.of(1000);
        
        assertThatThrownBy(() -> money.isLessThan(null))
                .isInstanceOf(NullValueException.class)
                .hasMessage("비교할 금액이 null입니다.");
    }

    @Test
    @DisplayName("isLessThanOrEqual() 메서드 테스트")
    void isLessThanOrEqual_Test() {
        Money money1 = Money.of(1000);
        Money money2 = Money.of(2000);
        Money money3 = Money.of(1000);
        
        assertThat(money1.isLessThanOrEqual(money2)).isTrue();
        assertThat(money1.isLessThanOrEqual(money3)).isTrue();
        assertThat(money2.isLessThanOrEqual(money1)).isFalse();
    }

    @Test
    @DisplayName("isLessThanOrEqual() - null로 예외 발생")
    void isLessThanOrEqual_NullMoney_ThrowsException() {
        Money money = Money.of(1000);
        
        assertThatThrownBy(() -> money.isLessThanOrEqual(null))
                .isInstanceOf(NullValueException.class)
                .hasMessage("비교할 금액이 null입니다.");
    }

    @Test
    @DisplayName("퍼센트 계산 - int 버전")
    void percentage_Int_Test() {
        Money money = Money.of(10000);
        
        Money result = money.percentage(150, RoundingMode.HALF_UP);
        
        assertThat(result.getAmount()).isEqualTo(1500);
    }

    @Test
    @DisplayName("퍼센트 계산 - 음수 퍼센트로 예외 발생")
    void percentage_NegativePercentage_ThrowsException() {
        Money money = Money.of(1000);
        
        assertThatThrownBy(() -> money.percentage(-10, RoundingMode.HALF_UP))
                .isInstanceOf(MoneyException.InvalidOperandException.class)
                .hasMessage("퍼센트 값은 0 이상이어야 합니다.");
    }

    @Test
    @DisplayName("negate() - 0원 테스트")
    void negate_ZeroMoney_Test() {
        Money money = Money.of(0);
        
        Money result = money.negate();
        
        assertThat(result.getAmount()).isEqualTo(0);
    }

    @Test
    @DisplayName("negate() - 양수에서 예외 발생 (음수 불허)")
    void negate_PositiveMoney_ThrowsException() {
        Money money = Money.of(1000);
        
        assertThatThrownBy(() -> money.negate())
                .isInstanceOf(MoneyException.InvalidAmountException.class)
                .hasMessage("금액은 0 이상이어야 합니다.");
    }

    @Test
    @DisplayName("equals() 및 hashCode() 테스트")
    void equals_HashCode_Test() {
        Money money1 = Money.of(1000);
        Money money2 = Money.of(1000);
        Money money3 = Money.of(2000);
        
        assertThat(money1).isEqualTo(money2);
        assertThat(money1).isNotEqualTo(money3);
        assertThat(money1.hashCode()).isEqualTo(money2.hashCode());
        assertThat(money1.hashCode()).isNotEqualTo(money3.hashCode());
    }

    @Test
    @DisplayName("toString() 테스트")
    void toString_Test() {
        Money money = Money.of(1000);
        
        String result = money.toString();
        
        assertThat(result).contains("1000");
    }

    @Test
    @DisplayName("퍼센트 계산 - 0% 테스트")
    void percentage_ZeroPercent_Test() {
        Money money = Money.of(10000);
        
        Money result = money.percentage(0, RoundingMode.HALF_UP);
        
        assertThat(result.getAmount()).isEqualTo(0);
    }

    @Test
    @DisplayName("퍼센트 계산 - 반올림 테스트")
    void percentage_RoundingTest() {
        Money money = Money.of(1000);
        
        Money resultUp = money.percentage(155, RoundingMode.HALF_UP);
        Money resultDown = money.percentage(155, RoundingMode.HALF_DOWN);
        
        assertThat(resultUp.getAmount()).isEqualTo(155);
        assertThat(resultDown.getAmount()).isEqualTo(155);
    }

    @Test
    @DisplayName("나눗셈 - 몫만 반환 테스트")
    void divide_IntegerDivision_Test() {
        Money money = Money.of(1000);
        
        Money result = money.divide(3);
        
        assertThat(result.getAmount()).isEqualTo(333); // 1000 / 3 = 333 (몫만)
    }

    @Test
    @DisplayName("퍼센트 계산 - 다양한 라운딩 모드 테스트")
    void percentage_DifferentRoundingModes_Test() {
        Money money = Money.of(1000);
        
        Money resultUp = money.percentage(155, RoundingMode.UP);
        Money resultDown = money.percentage(155, RoundingMode.DOWN);
        Money resultCeiling = money.percentage(155, RoundingMode.CEILING);
        Money resultFloor = money.percentage(155, RoundingMode.FLOOR);
        
        assertThat(resultUp.getAmount()).isEqualTo(155);
        assertThat(resultDown.getAmount()).isEqualTo(155);
        assertThat(resultCeiling.getAmount()).isEqualTo(155);
        assertThat(resultFloor.getAmount()).isEqualTo(155);
    }

    @Test
    @DisplayName("복합 연산 테스트 - 덧셈과 곱셈")
    void complexOperation_PlusAndMultiply_Test() {
        Money money1 = Money.of(1000);
        Money money2 = Money.of(500);
        
        Money result = money1.plus(money2).multiply(2);
        
        assertThat(result.getAmount()).isEqualTo(3000); // (1000 + 500) * 2 = 3000
    }

    @Test
    @DisplayName("복합 연산 테스트 - 뺄셈과 나눗셈")
    void complexOperation_MinusAndDivide_Test() {
        Money money1 = Money.of(2000);
        Money money2 = Money.of(500);
        
        Money result = money1.minus(money2).divide(3);
        
        assertThat(result.getAmount()).isEqualTo(500); // (2000 - 500) / 3 = 500
    }

    @Test
    @DisplayName("극값 테스트 - 매우 큰 값")
    void extremeValue_LargeAmount_Test() {
        Money money = Money.of(Integer.MAX_VALUE - 1);
        
        assertThat(money.getAmount()).isEqualTo(Integer.MAX_VALUE - 1);
        assertThat(money.isPositive()).isTrue();
    }

    @Test
    @DisplayName("경계값 테스트 - 0에서의 연산")
    void boundaryValue_ZeroOperations_Test() {
        Money zero = Money.zero();
        Money hundred = Money.of(100);
        
        assertThat(zero.plus(hundred).getAmount()).isEqualTo(100);
        assertThat(hundred.minus(hundred).isZero()).isTrue();
        assertThat(zero.multiply(5).isZero()).isTrue();
    }
}