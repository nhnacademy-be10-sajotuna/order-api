package shop.sajotuna.order.orders.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.common.exception.NullValueException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Discounts 도메인 테스트")
class DiscountsTest {

    @Nested
    @DisplayName("할인 정보 생성 테스트")
    class CreateDiscountsTest {

        @Test
        @DisplayName("유효한 정보로 할인 정보를 생성한다")
        void create_validInfo_success() {
            // given
            Money couponDiscountAmount = Money.of(3000);
            Money usedPoint = Money.of(2000);
            Long usedCouponId = 1L;

            // when
            Discounts discounts = new Discounts(couponDiscountAmount, usedPoint, usedCouponId);

            // then
            assertThat(discounts).isNotNull();
            assertThat(discounts.getCouponDiscountAmount()).isEqualTo(couponDiscountAmount);
            assertThat(discounts.getUsedPoint()).isEqualTo(usedPoint);
            assertThat(discounts.getUsedCouponId()).isEqualTo(usedCouponId);
            assertThat(discounts.getEarnedPoint()).isNull();
        }

        @Test
        @DisplayName("쿠폰 ID 없이 할인 정보를 생성한다")
        void create_withoutCouponId_success() {
            // given
            Money couponDiscountAmount = Money.of(0);
            Money usedPoint = Money.of(1000);
            Long usedCouponId = null;

            // when
            Discounts discounts = new Discounts(couponDiscountAmount, usedPoint, usedCouponId);

            // then
            assertThat(discounts).isNotNull();
            assertThat(discounts.getCouponDiscountAmount()).isEqualTo(Money.of(0));
            assertThat(discounts.getUsedPoint()).isEqualTo(Money.of(1000));
            assertThat(discounts.getUsedCouponId()).isNull();
        }

        @Test
        @DisplayName("할인과 포인트가 모두 0원인 경우")
        void create_allZeroDiscount_success() {
            // given
            Money couponDiscountAmount = Money.of(0);
            Money usedPoint = Money.of(0);

            // when
            Discounts discounts = new Discounts(couponDiscountAmount, usedPoint, null);

            // then
            assertThat(discounts).isNotNull();
            assertThat(discounts.getCouponDiscountAmount()).isEqualTo(Money.of(0));
            assertThat(discounts.getUsedPoint()).isEqualTo(Money.of(0));
            assertThat(discounts.getTotalDiscountAmount()).isEqualTo(Money.of(0));
        }
    }

    @Nested
    @DisplayName("할인 정보 검증 테스트")
    class ValidateDiscountsTest {

        @Test
        @DisplayName("null 쿠폰 할인 금액으로 생성 시 예외가 발생한다")
        void create_nullCouponDiscountAmount_throwsException() {
            // given
            Money usedPoint = Money.of(2000);
            Long usedCouponId = 1L;

            // when & then
            assertThatThrownBy(() -> new Discounts(null, usedPoint, usedCouponId))
                    .isInstanceOf(NullValueException.class)
                    .hasMessage("쿠폰 할인 금액은 필수입니다.");
        }

        @Test
        @DisplayName("null 사용 포인트로 생성 시 예외가 발생한다")
        void create_nullUsedPoint_throwsException() {
            // given
            Money couponDiscountAmount = Money.of(3000);
            Money usedPoint = null;
            Long usedCouponId = 1L;

            // when & then
            assertThatThrownBy(() -> new Discounts(couponDiscountAmount, usedPoint, usedCouponId))
                    .isInstanceOf(NullValueException.class)
                    .hasMessage("사용한 포인트는 필수입니다.");
        }

        @Test
        @DisplayName("쿠폰 할인 금액과 사용 포인트가 모두 null인 경우 예외가 발생한다")
        void create_bothNull_throwsException() {
            // given
            Long usedCouponId = 1L;

            // when & then
            assertThatThrownBy(() -> new Discounts(null, null, usedCouponId))
                    .isInstanceOf(NullValueException.class)
                    .hasMessage("쿠폰 할인 금액은 필수입니다.");
        }
    }

    @Nested
    @DisplayName("총 할인 금액 계산 테스트")
    class TotalDiscountAmountTest {

        @Test
        @DisplayName("쿠폰 할인과 포인트 할인을 합산한다")
        void getTotalDiscountAmount_withBothDiscounts_success() {
            // given
            Money couponDiscountAmount = Money.of(3000);
            Money usedPoint = Money.of(2000);
            Discounts discounts = new Discounts(couponDiscountAmount, usedPoint, 1L);

            // when
            Money totalDiscountAmount = discounts.getTotalDiscountAmount();

            // then
            assertThat(totalDiscountAmount).isEqualTo(Money.of(5000));
        }

        @Test
        @DisplayName("쿠폰 할인만 있는 경우 총 할인 금액을 계산한다")
        void getTotalDiscountAmount_onlyCouponDiscount_success() {
            // given
            Money couponDiscountAmount = Money.of(5000);
            Money usedPoint = Money.of(0);
            Discounts discounts = new Discounts(couponDiscountAmount, usedPoint, 1L);

            // when
            Money totalDiscountAmount = discounts.getTotalDiscountAmount();

            // then
            assertThat(totalDiscountAmount).isEqualTo(Money.of(5000));
        }

        @Test
        @DisplayName("포인트 할인만 있는 경우 총 할인 금액을 계산한다")
        void getTotalDiscountAmount_onlyPointDiscount_success() {
            // given
            Money couponDiscountAmount = Money.of(0);
            Money usedPoint = Money.of(3000);
            Discounts discounts = new Discounts(couponDiscountAmount, usedPoint, null);

            // when
            Money totalDiscountAmount = discounts.getTotalDiscountAmount();

            // then
            assertThat(totalDiscountAmount).isEqualTo(Money.of(3000));
        }

        @Test
        @DisplayName("할인이 없는 경우 총 할인 금액이 0이다")
        void getTotalDiscountAmount_noDiscount_returnsZero() {
            // given
            Money couponDiscountAmount = Money.of(0);
            Money usedPoint = Money.of(0);
            Discounts discounts = new Discounts(couponDiscountAmount, usedPoint, null);

            // when
            Money totalDiscountAmount = discounts.getTotalDiscountAmount();

            // then
            assertThat(totalDiscountAmount).isEqualTo(Money.of(0));
        }

        @Test
        @DisplayName("고액 할인의 총 할인 금액을 계산한다")
        void getTotalDiscountAmount_highAmount_success() {
            // given
            Money couponDiscountAmount = Money.of(50000);
            Money usedPoint = Money.of(30000);
            Discounts discounts = new Discounts(couponDiscountAmount, usedPoint, 1L);

            // when
            Money totalDiscountAmount = discounts.getTotalDiscountAmount();

            // then
            assertThat(totalDiscountAmount).isEqualTo(Money.of(80000));
        }
    }

    @Nested
    @DisplayName("적립 포인트 설정 테스트")
    class SetEarnedPointTest {

        @Test
        @DisplayName("적립 포인트를 설정한다")
        void setEarnedPoint_validAmount_success() {
            // given
            Discounts discounts = createTestDiscounts();
            Money earnedPoint = Money.of(1000);

            // when
            discounts.setEarnedPoint(earnedPoint);

            // then
            assertThat(discounts.getEarnedPoint()).isEqualTo(earnedPoint);
        }

        @Test
        @DisplayName("0원 적립 포인트를 설정한다")
        void setEarnedPoint_zeroAmount_success() {
            // given
            Discounts discounts = createTestDiscounts();
            Money earnedPoint = Money.of(0);

            // when
            discounts.setEarnedPoint(earnedPoint);

            // then
            assertThat(discounts.getEarnedPoint()).isEqualTo(Money.of(0));
        }

        @Test
        @DisplayName("적립 포인트를 여러 번 설정한다")
        void setEarnedPoint_multiple_times() {
            // given
            Discounts discounts = createTestDiscounts();

            // when
            discounts.setEarnedPoint(Money.of(500));
            assertThat(discounts.getEarnedPoint()).isEqualTo(Money.of(500));

            discounts.setEarnedPoint(Money.of(1500));
            assertThat(discounts.getEarnedPoint()).isEqualTo(Money.of(1500));

            discounts.setEarnedPoint(Money.of(0));
            assertThat(discounts.getEarnedPoint()).isEqualTo(Money.of(0));
        }

        @Test
        @DisplayName("null 적립 포인트 설정 시 예외가 발생한다")
        void setEarnedPoint_nullAmount_throwsException() {
            // given
            Discounts discounts = createTestDiscounts();

            // when & then
            assertThatThrownBy(() -> discounts.setEarnedPoint(null))
                    .isInstanceOf(NullValueException.class)
                    .hasMessage("적립된 포인트는 필수입니다.");
        }

        @Test
        @DisplayName("고액 적립 포인트를 설정한다")
        void setEarnedPoint_highAmount_success() {
            // given
            Discounts discounts = createTestDiscounts();
            Money earnedPoint = Money.of(100000);

            // when
            discounts.setEarnedPoint(earnedPoint);

            // then
            assertThat(discounts.getEarnedPoint()).isEqualTo(Money.of(100000));
        }
    }

    @Nested
    @DisplayName("할인 정보 조회 테스트")
    class GetDiscountsInfoTest {

        @Test
        @DisplayName("쿠폰 할인 금액을 조회한다")
        void getCouponDiscountAmount_success() {
            // given
            Money couponDiscountAmount = Money.of(7000);
            Discounts discounts = new Discounts(couponDiscountAmount, Money.of(3000), 1L);

            // when & then
            assertThat(discounts.getCouponDiscountAmount()).isEqualTo(couponDiscountAmount);
        }

        @Test
        @DisplayName("사용 포인트를 조회한다")
        void getUsedPoint_success() {
            // given
            Money usedPoint = Money.of(4000);
            Discounts discounts = new Discounts(Money.of(2000), usedPoint, 1L);

            // when & then
            assertThat(discounts.getUsedPoint()).isEqualTo(usedPoint);
        }

        @Test
        @DisplayName("사용 쿠폰 ID를 조회한다")
        void getUsedCouponId_success() {
            // given
            Long usedCouponId = 123L;
            Discounts discounts = new Discounts(Money.of(2000), Money.of(1000), usedCouponId);

            // when & then
            assertThat(discounts.getUsedCouponId()).isEqualTo(usedCouponId);
        }

        @Test
        @DisplayName("사용 쿠폰 ID가 null인 경우를 조회한다")
        void getUsedCouponId_null_success() {
            // given
            Discounts discounts = new Discounts(Money.of(0), Money.of(1000), null);

            // when & then
            assertThat(discounts.getUsedCouponId()).isNull();
        }

        @Test
        @DisplayName("적립 포인트가 설정되지 않은 경우 null을 반환한다")
        void getEarnedPoint_notSet_returnsNull() {
            // given
            Discounts discounts = createTestDiscounts();

            // when & then
            assertThat(discounts.getEarnedPoint()).isNull();
        }

        @Test
        @DisplayName("적립 포인트가 설정된 경우를 조회한다")
        void getEarnedPoint_set_success() {
            // given
            Discounts discounts = createTestDiscounts();
            Money earnedPoint = Money.of(800);
            discounts.setEarnedPoint(earnedPoint);

            // when & then
            assertThat(discounts.getEarnedPoint()).isEqualTo(earnedPoint);
        }
    }

    @Nested
    @DisplayName("복합 시나리오 테스트")
    class ComplexScenarioTest {

        @Test
        @DisplayName("쿠폰과 포인트를 모두 사용하고 포인트를 적립하는 시나리오")
        void fullDiscountScenario() {
            // given
            Money couponDiscount = Money.of(5000);
            Money usedPoint = Money.of(3000);
            Long couponId = 1L;

            // when
            Discounts discounts = new Discounts(couponDiscount, usedPoint, couponId);
            discounts.setEarnedPoint(Money.of(1200));

            // then
            assertThat(discounts.getCouponDiscountAmount()).isEqualTo(Money.of(5000));
            assertThat(discounts.getUsedPoint()).isEqualTo(Money.of(3000));
            assertThat(discounts.getUsedCouponId()).isEqualTo(1L);
            assertThat(discounts.getEarnedPoint()).isEqualTo(Money.of(1200));
            assertThat(discounts.getTotalDiscountAmount()).isEqualTo(Money.of(8000));
        }

        @Test
        @DisplayName("쿠폰 할인만 사용하는 시나리오")
        void couponOnlyScenario() {
            // given
            Money couponDiscount = Money.of(10000);
            Money usedPoint = Money.of(0);
            Long couponId = 2L;

            // when
            Discounts discounts = new Discounts(couponDiscount, usedPoint, couponId);
            discounts.setEarnedPoint(Money.of(500));

            // then
            assertThat(discounts.getCouponDiscountAmount()).isEqualTo(Money.of(10000));
            assertThat(discounts.getUsedPoint()).isEqualTo(Money.of(0));
            assertThat(discounts.getUsedCouponId()).isEqualTo(2L);
            assertThat(discounts.getEarnedPoint()).isEqualTo(Money.of(500));
            assertThat(discounts.getTotalDiscountAmount()).isEqualTo(Money.of(10000));
        }

        @Test
        @DisplayName("포인트 할인만 사용하는 시나리오")
        void pointOnlyScenario() {
            // given
            Money couponDiscount = Money.of(0);
            Money usedPoint = Money.of(7000);
            Long couponId = null;

            // when
            Discounts discounts = new Discounts(couponDiscount, usedPoint, couponId);
            discounts.setEarnedPoint(Money.of(350));

            // then
            assertThat(discounts.getCouponDiscountAmount()).isEqualTo(Money.of(0));
            assertThat(discounts.getUsedPoint()).isEqualTo(Money.of(7000));
            assertThat(discounts.getUsedCouponId()).isNull();
            assertThat(discounts.getEarnedPoint()).isEqualTo(Money.of(350));
            assertThat(discounts.getTotalDiscountAmount()).isEqualTo(Money.of(7000));
        }

        @Test
        @DisplayName("할인 없이 포인트만 적립하는 시나리오")
        void earnPointOnlyScenario() {
            // given
            Money couponDiscount = Money.of(0);
            Money usedPoint = Money.of(0);
            Long couponId = null;

            // when
            Discounts discounts = new Discounts(couponDiscount, usedPoint, couponId);
            discounts.setEarnedPoint(Money.of(2000));

            // then
            assertThat(discounts.getCouponDiscountAmount()).isEqualTo(Money.of(0));
            assertThat(discounts.getUsedPoint()).isEqualTo(Money.of(0));
            assertThat(discounts.getUsedCouponId()).isNull();
            assertThat(discounts.getEarnedPoint()).isEqualTo(Money.of(2000));
            assertThat(discounts.getTotalDiscountAmount()).isEqualTo(Money.of(0));
        }

        @Test
        @DisplayName("대량 할인 시나리오")
        void largeDiscountScenario() {
            // given
            Money couponDiscount = Money.of(100000);
            Money usedPoint = Money.of(50000);
            Long couponId = 999L;

            // when
            Discounts discounts = new Discounts(couponDiscount, usedPoint, couponId);
            discounts.setEarnedPoint(Money.of(5000));

            // then
            assertThat(discounts.getTotalDiscountAmount()).isEqualTo(Money.of(150000));
            assertThat(discounts.getEarnedPoint()).isEqualTo(Money.of(5000));
        }
    }

    // 테스트 헬퍼 메소드
    private Discounts createTestDiscounts() {
        return new Discounts(Money.of(3000), Money.of(2000), 1L);
    }
}