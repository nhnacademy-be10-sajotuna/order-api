package shop.sajotuna.order.orders.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.common.exception.NullValueException;
import shop.sajotuna.order.coupon.domain.UserCoupon;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@DisplayName("OrderProduct 도메인 테스트")
class OrderProductTest {

    @Nested
    @DisplayName("주문 상품 생성 테스트")
    class CreateOrderProductTest {

        @Test
        @DisplayName("유효한 정보로 주문 상품을 생성한다")
        void create_success() {
            // given
            Order order = mock(Order.class);
            String isbn = "9788901234567";
            OrderPackaging orderPackaging = createMockOrderPackaging(Money.of(1500));
            Integer qty = 2;
            Money amount = Money.of(10000);
            Boolean packagingRequest = true;
            UserCoupon coupon = null;

            // when
            OrderProduct orderProduct = OrderProduct.create(order, isbn, orderPackaging, qty, amount, packagingRequest, coupon);

            // then
            assertThat(orderProduct).isNotNull();
            assertThat(orderProduct.getOrder()).isEqualTo(order);
            assertThat(orderProduct.getIsbn()).isEqualTo(isbn);
            assertThat(orderProduct.getOrderPackaging()).isEqualTo(orderPackaging);
            assertThat(orderProduct.getQty()).isEqualTo(qty);
            assertThat(orderProduct.getAmount()).isEqualTo(amount);
            assertThat(orderProduct.getPackagingRequest()).isEqualTo(packagingRequest);
            assertThat(orderProduct.getDiscountAmount()).isEqualTo(Money.zero());
            assertThat(orderProduct.getFinalAmount()).isEqualTo(Money.of(20000)); // 10000 * 2
            assertThat(orderProduct.getAppliedCoupon()).isNull();
        }

        @Test
        @DisplayName("포장재 없이 주문 상품을 생성한다")
        void create_withoutPackaging_success() {
            // given
            Order order = mock(Order.class);
            String isbn = "9788901234567";
            OrderPackaging orderPackaging = null;
            Integer qty = 1;
            Money amount = Money.of(15000);
            Boolean packagingRequest = false;
            UserCoupon coupon = null;

            // when
            OrderProduct orderProduct = OrderProduct.create(order, isbn, orderPackaging, qty, amount, packagingRequest, coupon);

            // then
            assertThat(orderProduct).isNotNull();
            assertThat(orderProduct.getOrderPackaging()).isNull();
            assertThat(orderProduct.getPackagingRequest()).isFalse();
            assertThat(orderProduct.getFinalAmount()).isEqualTo(Money.of(15000)); // 15000 * 1
        }

        @Test
        @DisplayName("쿠폰과 함께 주문 상품을 생성한다")
        void create_withCoupon_success() {
            // given
            Order order = mock(Order.class);
            String isbn = "9788901234567";
            OrderPackaging orderPackaging = createMockOrderPackaging(Money.of(1000));
            Integer qty = 1;
            Money amount = Money.of(20000);
            Boolean packagingRequest = true;
            UserCoupon coupon = mock(UserCoupon.class);

            // when
            OrderProduct orderProduct = OrderProduct.create(order, isbn, orderPackaging, qty, amount, packagingRequest, coupon);

            // then
            assertThat(orderProduct).isNotNull();
            assertThat(orderProduct.getAppliedCoupon()).isEqualTo(coupon);
        }
    }

    @Nested
    @DisplayName("주문 설정 테스트")
    class SetOrderTest {

        @Test
        @DisplayName("주문을 설정한다")
        void setOrder_success() {
            // given
            OrderProduct orderProduct = createTestOrderProduct();
            Order newOrder = mock(Order.class);

            // when
            orderProduct.setOrder(newOrder);

            // then
            assertThat(orderProduct.getOrder()).isEqualTo(newOrder);
        }

        @Test
        @DisplayName("null 주문 설정 시 예외가 발생한다")
        void setOrder_withNull_throwsException() {
            // given
            OrderProduct orderProduct = createTestOrderProduct();

            // when & then
            assertThatThrownBy(() -> orderProduct.setOrder(null))
                    .isInstanceOf(NullValueException.class)
                    .hasMessage("주문은 null일 수 없습니다.");
        }
    }

    @Nested
    @DisplayName("가격 계산 테스트")
    class PriceCalculationTest {

        @Test
        @DisplayName("총 가격을 계산한다 - 단일 수량")
        void getTotalPrice_singleQuantity() {
            // given
            OrderProduct orderProduct = OrderProduct.builder()
                    .amount(Money.of(10000))
                    .qty(1)
                    .build();

            // when
            Money totalPrice = orderProduct.getTotalPrice();

            // then
            assertThat(totalPrice).isEqualTo(Money.of(10000));
        }

        @Test
        @DisplayName("총 가격을 계산한다 - 복수 수량")
        void getTotalPrice_multipleQuantity() {
            // given
            OrderProduct orderProduct = OrderProduct.builder()
                    .amount(Money.of(15000))
                    .qty(3)
                    .build();

            // when
            Money totalPrice = orderProduct.getTotalPrice();

            // then
            assertThat(totalPrice).isEqualTo(Money.of(45000));
        }

        @Test
        @DisplayName("포장비를 계산한다 - 포장재 있음")
        void getPackagingPrice_withPackaging() {
            // given
            OrderPackaging orderPackaging = createMockOrderPackaging(Money.of(2000));
            OrderProduct orderProduct = OrderProduct.builder()
                    .orderPackaging(orderPackaging)
                    .qty(2)
                    .build();

            // when
            Money packagingPrice = orderProduct.getPackagingPrice();

            // then
            assertThat(packagingPrice).isEqualTo(Money.of(4000)); // 2000 * 2
        }

        @Test
        @DisplayName("포장비를 계산한다 - 포장재 없음")
        void getPackagingPrice_withoutPackaging() {
            // given
            OrderProduct orderProduct = OrderProduct.builder()
                    .orderPackaging(null)
                    .qty(2)
                    .build();

            // when
            Money packagingPrice = orderProduct.getPackagingPrice();

            // then
            assertThat(packagingPrice).isEqualTo(Money.zero());
        }
    }

    @Nested
    @DisplayName("할인 적용 테스트")
    class DiscountApplicationTest {

        @Test
        @DisplayName("할인을 적용한다")
        void applyDiscount_success() {
            // given
            OrderProduct orderProduct = OrderProduct.builder()
                    .amount(Money.of(10000))
                    .qty(2)
                    .discountAmount(Money.zero())
                    .finalAmount(Money.of(20000))
                    .build();
            Money discountAmount = Money.of(3000);

            // when
            orderProduct.applyDiscount(discountAmount);

            // then
            assertThat(orderProduct.getDiscountAmount()).isEqualTo(Money.of(3000));
            assertThat(orderProduct.getFinalAmount()).isEqualTo(Money.of(17000)); // 20000 - 3000
        }

        @Test
        @DisplayName("null 할인 금액 적용 시 예외가 발생한다")
        void applyDiscount_withNull_throwsException() {
            // given
            OrderProduct orderProduct = createTestOrderProduct();

            // when & then
            assertThatThrownBy(() -> orderProduct.applyDiscount(null))
                    .isInstanceOf(NullValueException.class)
                    .hasMessage("할인 금액은 null일 수 없습니다.");
        }

        @Test
        @DisplayName("0원 할인을 적용한다")
        void applyDiscount_zeroDiscount() {
            // given
            OrderProduct orderProduct = OrderProduct.builder()
                    .amount(Money.of(10000))
                    .qty(1)
                    .discountAmount(Money.zero())
                    .finalAmount(Money.of(10000))
                    .build();
            Money discountAmount = Money.zero();

            // when
            orderProduct.applyDiscount(discountAmount);

            // then
            assertThat(orderProduct.getDiscountAmount()).isEqualTo(Money.zero());
            assertThat(orderProduct.getFinalAmount()).isEqualTo(Money.of(10000));
        }
    }

    @Nested
    @DisplayName("쿠폰 할인 적용 테스트")
    class CouponDiscountApplicationTest {

        @Test
        @DisplayName("쿠폰 할인을 적용한다")
        void applyCouponDiscount_withCoupon_success() {
            // given
            UserCoupon coupon = mock(UserCoupon.class);
            Money discountAmount = Money.of(2000);
            when(coupon.applyCoupon(Money.of(10000))).thenReturn(discountAmount);

            OrderProduct orderProduct = OrderProduct.builder()
                    .amount(Money.of(10000))
                    .qty(1)
                    .appliedCoupon(coupon)
                    .discountAmount(Money.zero())
                    .finalAmount(Money.of(10000))
                    .build();

            // when
            Money appliedDiscount = orderProduct.applyCouponDiscount();

            // then
            assertThat(appliedDiscount).isEqualTo(Money.of(2000));
            assertThat(orderProduct.getDiscountAmount()).isEqualTo(Money.of(2000));
            assertThat(orderProduct.getFinalAmount()).isEqualTo(Money.of(8000)); // 10000 - 2000
            verify(coupon).applyCoupon(Money.of(10000));
        }

        @Test
        @DisplayName("쿠폰이 없는 경우 할인 금액이 0원이다")
        void applyCouponDiscount_withoutCoupon_returnsZero() {
            // given
            OrderProduct orderProduct = OrderProduct.builder()
                    .amount(Money.of(10000))
                    .qty(1)
                    .appliedCoupon(null)
                    .discountAmount(Money.zero())
                    .finalAmount(Money.of(10000))
                    .build();

            // when
            Money appliedDiscount = orderProduct.applyCouponDiscount();

            // then
            assertThat(appliedDiscount).isEqualTo(Money.zero());
            assertThat(orderProduct.getDiscountAmount()).isEqualTo(Money.zero());
            assertThat(orderProduct.getFinalAmount()).isEqualTo(Money.of(10000));
        }

        @Test
        @DisplayName("쿠폰 할인 금액이 0원인 경우")
        void applyCouponDiscount_zeroCouponDiscount() {
            // given
            UserCoupon coupon = mock(UserCoupon.class);
            when(coupon.applyCoupon(Money.of(5000))).thenReturn(Money.zero());

            OrderProduct orderProduct = OrderProduct.builder()
                    .amount(Money.of(5000))
                    .qty(1)
                    .appliedCoupon(coupon)
                    .discountAmount(Money.zero())
                    .finalAmount(Money.of(5000))
                    .build();

            // when
            Money appliedDiscount = orderProduct.applyCouponDiscount();

            // then
            assertThat(appliedDiscount).isEqualTo(Money.zero());
            assertThat(orderProduct.getDiscountAmount()).isEqualTo(Money.zero());
            assertThat(orderProduct.getFinalAmount()).isEqualTo(Money.of(5000));
            verify(coupon).applyCoupon(Money.of(5000));
        }

        @Test
        @DisplayName("복수 수량 상품에 쿠폰 할인을 적용한다")
        void applyCouponDiscount_multipleQuantity() {
            // given
            UserCoupon coupon = mock(UserCoupon.class);
            Money discountAmount = Money.of(3000);
            when(coupon.applyCoupon(Money.of(8000))).thenReturn(discountAmount);

            OrderProduct orderProduct = OrderProduct.builder()
                    .amount(Money.of(8000))
                    .qty(3)
                    .appliedCoupon(coupon)
                    .discountAmount(Money.zero())
                    .finalAmount(Money.of(24000)) // 8000 * 3
                    .build();

            // when
            Money appliedDiscount = orderProduct.applyCouponDiscount();

            // then
            assertThat(appliedDiscount).isEqualTo(Money.of(3000));
            assertThat(orderProduct.getDiscountAmount()).isEqualTo(Money.of(3000));
            assertThat(orderProduct.getFinalAmount()).isEqualTo(Money.of(21000)); // 24000 - 3000
            verify(coupon).applyCoupon(Money.of(8000)); // 단가로 쿠폰 적용
        }
    }

    @Nested
    @DisplayName("복합 시나리오 테스트")
    class ComplexScenarioTest {

        @Test
        @DisplayName("포장재와 쿠폰이 모두 있는 주문 상품")
        void complexScenario_withPackagingAndCoupon() {
            // given
            Order order = mock(Order.class);
            OrderPackaging orderPackaging = createMockOrderPackaging(Money.of(1500));
            UserCoupon coupon = mock(UserCoupon.class);
            when(coupon.applyCoupon(Money.of(12000))).thenReturn(Money.of(2000));

            // when
            OrderProduct orderProduct = OrderProduct.create(
                    order, "9788901234567", orderPackaging, 2, Money.of(12000), true, coupon
            );

            // then
            assertThat(orderProduct.getTotalPrice()).isEqualTo(Money.of(24000)); // 12000 * 2
            assertThat(orderProduct.getPackagingPrice()).isEqualTo(Money.of(3000)); // 1500 * 2
            
            // 쿠폰 할인 적용
            Money discount = orderProduct.applyCouponDiscount();
            assertThat(discount).isEqualTo(Money.of(2000));
            assertThat(orderProduct.getFinalAmount()).isEqualTo(Money.of(22000)); // 24000 - 2000
        }

        @Test
        @DisplayName("수량이 많은 고가 상품의 할인 적용")
        void complexScenario_highValueProductWithDiscount() {
            // given
            OrderProduct orderProduct = OrderProduct.builder()
                    .amount(Money.of(50000))
                    .qty(5)
                    .discountAmount(Money.zero())
                    .finalAmount(Money.of(250000)) // 50000 * 5
                    .build();

            // when
            orderProduct.applyDiscount(Money.of(25000));

            // then
            assertThat(orderProduct.getTotalPrice()).isEqualTo(Money.of(250000));
            assertThat(orderProduct.getDiscountAmount()).isEqualTo(Money.of(25000));
            assertThat(orderProduct.getFinalAmount()).isEqualTo(Money.of(225000));
        }
    }

    // 테스트 헬퍼 메소드들
    private OrderProduct createTestOrderProduct() {
        return OrderProduct.builder()
                .order(mock(Order.class))
                .isbn("9788901234567")
                .orderPackaging(createMockOrderPackaging(Money.of(1000)))
                .qty(1)
                .amount(Money.of(10000))
                .packagingRequest(true)
                .discountAmount(Money.zero())
                .finalAmount(Money.of(10000))
                .appliedCoupon(null)
                .build();
    }

    private OrderPackaging createMockOrderPackaging(Money price) {
        OrderPackaging orderPackaging = mock(OrderPackaging.class);
        when(orderPackaging.getPrice()).thenReturn(price);
        return orderPackaging;
    }
}