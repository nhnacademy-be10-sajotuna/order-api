package shop.sajotuna.order.orders.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.common.exception.NullValueException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("OrderPrice 도메인 테스트")
class OrderPriceTest {

    @Nested
    @DisplayName("주문 가격 생성 테스트")
    class CreateOrderPriceTest {

        @Test
        @DisplayName("유효한 가격 정보로 주문 가격을 생성한다")
        void create_success() {
            // given
            Money totalProductPrice = Money.of(20000);
            Money packagingPrice = Money.of(3000);
            Money deliveryPrice = Money.of(5000);

            // when
            OrderPrice orderPrice = OrderPrice.create(totalProductPrice, packagingPrice, deliveryPrice);

            // then
            assertThat(orderPrice).isNotNull();
            assertThat(orderPrice.getTotalProductPrice()).isEqualTo(totalProductPrice);
            assertThat(orderPrice.getPackagingPrice()).isEqualTo(packagingPrice);
            assertThat(orderPrice.getDeliveryPrice()).isEqualTo(deliveryPrice);
        }

        @Test
        @DisplayName("0원 포장비로 주문 가격을 생성한다")
        void create_withZeroPackagingPrice_success() {
            // given
            Money totalProductPrice = Money.of(20000);
            Money packagingPrice = Money.of(0);
            Money deliveryPrice = Money.of(5000);

            // when
            OrderPrice orderPrice = OrderPrice.create(totalProductPrice, packagingPrice, deliveryPrice);

            // then
            assertThat(orderPrice).isNotNull();
            assertThat(orderPrice.getPackagingPrice()).isEqualTo(Money.of(0));
        }

        @Test
        @DisplayName("0원 배송비로 주문 가격을 생성한다")
        void create_withZeroDeliveryPrice_success() {
            // given
            Money totalProductPrice = Money.of(20000);
            Money packagingPrice = Money.of(3000);
            Money deliveryPrice = Money.of(0);

            // when
            OrderPrice orderPrice = OrderPrice.create(totalProductPrice, packagingPrice, deliveryPrice);

            // then
            assertThat(orderPrice).isNotNull();
            assertThat(orderPrice.getDeliveryPrice()).isEqualTo(Money.of(0));
        }
    }

    @Nested
    @DisplayName("주문 가격 검증 테스트")
    class ValidateOrderPriceTest {

        @Test
        @DisplayName("상품 총액이 null인 경우 예외가 발생한다")
        void create_withNullTotalProductPrice_throwsException() {
            // given
            Money totalProductPrice = null;
            Money packagingPrice = Money.of(3000);
            Money deliveryPrice = Money.of(5000);

            // when & then
            assertThatThrownBy(() -> OrderPrice.create(totalProductPrice, packagingPrice, deliveryPrice))
                    .isInstanceOf(NullValueException.class)
                    .hasMessage("상품 총액은 필수입니다.");
        }

        @Test
        @DisplayName("포장비가 null인 경우 예외가 발생한다")
        void create_withNullPackagingPrice_throwsException() {
            // given
            Money totalProductPrice = Money.of(20000);
            Money packagingPrice = null;
            Money deliveryPrice = Money.of(5000);

            // when & then
            assertThatThrownBy(() -> OrderPrice.create(totalProductPrice, packagingPrice, deliveryPrice))
                    .isInstanceOf(NullValueException.class)
                    .hasMessage("포장비는 필수입니다.");
        }

        @Test
        @DisplayName("배송비가 null인 경우 예외가 발생한다")
        void create_withNullDeliveryPrice_throwsException() {
            // given
            Money totalProductPrice = Money.of(20000);
            Money packagingPrice = Money.of(3000);
            Money deliveryPrice = null;

            // when & then
            assertThatThrownBy(() -> OrderPrice.create(totalProductPrice, packagingPrice, deliveryPrice))
                    .isInstanceOf(NullValueException.class)
                    .hasMessage("배송비는 필수입니다.");
        }

        @Test
        @DisplayName("상품 총액이 0원인 경우 예외가 발생한다")
        void create_withZeroTotalProductPrice_throwsException() {
            // given
            Money totalProductPrice = Money.of(0);
            Money packagingPrice = Money.of(3000);
            Money deliveryPrice = Money.of(5000);

            // when & then
            assertThatThrownBy(() -> OrderPrice.create(totalProductPrice, packagingPrice, deliveryPrice))
                    .isInstanceOf(NullValueException.class)
                    .hasMessage("상품 총액은 0원보다 커야 합니다.");
        }
    }

    @Nested
    @DisplayName("총 가격 계산 테스트")
    class TotalPriceCalculationTest {

        @Test
        @DisplayName("모든 가격을 합산하여 총 가격을 계산한다")
        void getTotalPrice_success() {
            // given
            Money totalProductPrice = Money.of(20000);
            Money packagingPrice = Money.of(3000);
            Money deliveryPrice = Money.of(5000);
            OrderPrice orderPrice = OrderPrice.create(totalProductPrice, packagingPrice, deliveryPrice);

            // when
            Money totalPrice = orderPrice.getTotalPrice();

            // then
            assertThat(totalPrice).isEqualTo(Money.of(28000));
        }

        @Test
        @DisplayName("포장비가 0원인 경우 총 가격을 계산한다")
        void getTotalPrice_withZeroPackagingPrice_success() {
            // given
            Money totalProductPrice = Money.of(20000);
            Money packagingPrice = Money.of(0);
            Money deliveryPrice = Money.of(5000);
            OrderPrice orderPrice = OrderPrice.create(totalProductPrice, packagingPrice, deliveryPrice);

            // when
            Money totalPrice = orderPrice.getTotalPrice();

            // then
            assertThat(totalPrice).isEqualTo(Money.of(25000));
        }

        @Test
        @DisplayName("배송비가 0원인 경우 총 가격을 계산한다")
        void getTotalPrice_withZeroDeliveryPrice_success() {
            // given
            Money totalProductPrice = Money.of(20000);
            Money packagingPrice = Money.of(3000);
            Money deliveryPrice = Money.of(0);
            OrderPrice orderPrice = OrderPrice.create(totalProductPrice, packagingPrice, deliveryPrice);

            // when
            Money totalPrice = orderPrice.getTotalPrice();

            // then
            assertThat(totalPrice).isEqualTo(Money.of(23000));
        }

        @Test
        @DisplayName("포장비와 배송비가 모두 0원인 경우 총 가격을 계산한다")
        void getTotalPrice_withZeroPackagingAndDeliveryPrice_success() {
            // given
            Money totalProductPrice = Money.of(20000);
            Money packagingPrice = Money.of(0);
            Money deliveryPrice = Money.of(0);
            OrderPrice orderPrice = OrderPrice.create(totalProductPrice, packagingPrice, deliveryPrice);

            // when
            Money totalPrice = orderPrice.getTotalPrice();

            // then
            assertThat(totalPrice).isEqualTo(Money.of(20000));
        }

        @Test
        @DisplayName("최소 금액으로 총 가격을 계산한다")
        void getTotalPrice_withMinimumAmount_success() {
            // given
            Money totalProductPrice = Money.of(1);
            Money packagingPrice = Money.of(0);
            Money deliveryPrice = Money.of(0);
            OrderPrice orderPrice = OrderPrice.create(totalProductPrice, packagingPrice, deliveryPrice);

            // when
            Money totalPrice = orderPrice.getTotalPrice();

            // then
            assertThat(totalPrice).isEqualTo(Money.of(1));
        }

        @Test
        @DisplayName("고액 주문의 총 가격을 계산한다")
        void getTotalPrice_withHighAmount_success() {
            // given
            Money totalProductPrice = Money.of(1000000);
            Money packagingPrice = Money.of(50000);
            Money deliveryPrice = Money.of(30000);
            OrderPrice orderPrice = OrderPrice.create(totalProductPrice, packagingPrice, deliveryPrice);

            // when
            Money totalPrice = orderPrice.getTotalPrice();

            // then
            assertThat(totalPrice).isEqualTo(Money.of(1080000));
        }
    }

    @Nested
    @DisplayName("동등성 및 해시코드 테스트")
    class EqualsAndHashCodeTest {

        @Test
        @DisplayName("같은 가격 정보를 가진 OrderPrice는 동등하다")
        void equals_withSameValues_returnsTrue() {
            // given
            Money totalProductPrice = Money.of(20000);
            Money packagingPrice = Money.of(3000);
            Money deliveryPrice = Money.of(5000);

            OrderPrice orderPrice1 = OrderPrice.create(totalProductPrice, packagingPrice, deliveryPrice);
            OrderPrice orderPrice2 = OrderPrice.create(totalProductPrice, packagingPrice, deliveryPrice);

            // when & then
            assertThat(orderPrice1).isEqualTo(orderPrice2);
            assertThat(orderPrice1.hashCode()).isEqualTo(orderPrice2.hashCode());
        }

        @Test
        @DisplayName("다른 가격 정보를 가진 OrderPrice는 동등하지 않다")
        void equals_withDifferentValues_returnsFalse() {
            // given
            OrderPrice orderPrice1 = OrderPrice.create(Money.of(20000), Money.of(3000), Money.of(5000));
            OrderPrice orderPrice2 = OrderPrice.create(Money.of(25000), Money.of(3000), Money.of(5000));

            // when & then
            assertThat(orderPrice1).isNotEqualTo(orderPrice2);
        }
    }

    @Nested
    @DisplayName("toString 테스트")
    class ToStringTest {

        @Test
        @DisplayName("OrderPrice의 문자열 표현을 반환한다")
        void toString_success() {
            // given
            Money totalProductPrice = Money.of(20000);
            Money packagingPrice = Money.of(3000);
            Money deliveryPrice = Money.of(5000);
            OrderPrice orderPrice = OrderPrice.create(totalProductPrice, packagingPrice, deliveryPrice);

            // when
            String result = orderPrice.toString();

            // then
            assertThat(result).contains("totalProductPrice");
            assertThat(result).contains("packagingPrice");
            assertThat(result).contains("deliveryPrice");
        }
    }
}