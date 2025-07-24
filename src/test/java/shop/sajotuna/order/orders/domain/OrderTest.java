package shop.sajotuna.order.orders.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.common.exception.NullValueException;
import shop.sajotuna.order.orders.exception.InvalidStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Order 도메인 테스트")
class OrderTest {

    @Nested
    @DisplayName("주문 생성 테스트")
    class CreateOrderTest {

        @Test
        @DisplayName("유효한 정보로 주문을 생성한다")
        void createOrder_success() {
            // given
            Orderer orderer = createTestOrderer();
            ShippingInfo shippingInfo = createTestShippingInfo();
            OrderPrice orderPrice = createTestOrderPrice();
            Discounts discounts = createTestDiscounts();
            List<OrderProduct> orderProducts = createTestOrderProducts();

            // when
            Order order = Order.createOrder(orderer, shippingInfo, orderPrice, discounts, orderProducts);

            // then
            assertThat(order).isNotNull();
            assertThat(order.getOrderer()).isEqualTo(orderer);
            assertThat(order.getShippingInfo()).isEqualTo(shippingInfo);
            assertThat(order.getOrderPrice()).isEqualTo(orderPrice);
            assertThat(order.getDiscounts()).isEqualTo(discounts);
            assertThat(order.getOrderProducts()).hasSize(2);
            assertThat(order.getStatus()).isEqualTo(OrderStatus.BEFORE_PAYMENT);
            assertThat(order.getOrderNumber()).isNotNull();
            assertThat(order.getOrderNumber()).hasSize(24); // yyyyMMdd + 16자리
            assertThat(order.getCreatedAt()).isNotNull();
            assertThat(order.isUserOrder()).isEqualTo(orderer.isUserOrder());
        }

        @Test
        @DisplayName("null 주문 상품이 포함된 경우 예외가 발생한다")
        void createOrder_withNullOrderProduct_throwsException() {
            // given
            Orderer orderer = createTestOrderer();
            ShippingInfo shippingInfo = createTestShippingInfo();
            OrderPrice orderPrice = createTestOrderPrice();
            Discounts discounts = createTestDiscounts();
            List<OrderProduct> orderProducts = new ArrayList<>();
            orderProducts.add(createTestOrderProduct("9788901234567"));
            orderProducts.add(null);

            // when & then
            assertThatThrownBy(() -> Order.createOrder(orderer, shippingInfo, orderPrice, discounts, orderProducts))
                    .isInstanceOf(NullValueException.class)
                    .hasMessage("주문 상품은 null일 수 없습니다.");
        }
    }

    @Nested
    @DisplayName("주문 번호 생성 테스트")
    class OrderNumberTest {

        @Test
        @DisplayName("주문 번호를 생성한다")
        void getRandomOrderNumber_success() {
            // when
            String orderNumber1 = Order.getRandomOrderNumber();
            String orderNumber2 = Order.getRandomOrderNumber();

            // then
            assertThat(orderNumber1).hasSize(24);
            assertThat(orderNumber2).hasSize(24);
            assertThat(orderNumber1).isNotEqualTo(orderNumber2);
            assertThat(orderNumber1.substring(0, 8)).matches("\\d{8}"); // 날짜 부분
            assertThat(orderNumber1.substring(8)).matches("[a-zA-Z0-9]{16}"); // 랜덤 부분
        }
    }

    @Nested
    @DisplayName("가격 계산 테스트")
    class PriceCalculationTest {

        @Test
        @DisplayName("총 가격을 반환한다")
        void getTotalPrice_success() {
            // given
            Order order = createTestOrder();

            // when
            Money totalPrice = order.getTotalPrice();

            // then
            assertThat(totalPrice).isEqualTo(Money.of(33000)); // 상품 20000 + 포장 3000 + 배송 10000
        }

        @Test
        @DisplayName("할인 적용된 최종 가격을 반환한다")
        void getFinalPrice_success() {
            // given
            Order order = createTestOrder();

            // when
            Money finalPrice = order.getFinalPrice();

            // then
            assertThat(finalPrice).isEqualTo(Money.of(28000)); // 총가격 33000 - 할인 5000
        }

        @Test
        @DisplayName("할인 적용된 최종 상품 가격을 반환한다")
        void getFinalProductPrice_success() {
            // given
            Order order = createTestOrder();

            // when
            Money finalProductPrice = order.getFinalProductPrice();

            // then
            assertThat(finalProductPrice).isEqualTo(Money.of(15000)); // 상품가격 20000 - 할인 5000
        }

        @Test
        @DisplayName("반품 가격을 계산한다 - 배송비 차감하지 않는 경우")
        void getReturnPrice_noDeductShippingFee() {
            // given
            Order order = createTestOrder();
            ReturnReason returnReason = ReturnReason.DAMAGED;

            // when
            Money returnPrice = order.getReturnPrice(returnReason);

            // then
            assertThat(returnPrice).isEqualTo(Money.of(30000)); // 최종가격 28000 + 사용포인트 2000
        }

        @Test
        @DisplayName("반품 가격을 계산한다 - 배송비 차감하는 경우")
        void getReturnPrice_deductShippingFee() {
            // given
            Order order = createTestOrder();
            ReturnReason returnReason = ReturnReason.UNUSED;

            // when
            Money returnPrice = order.getReturnPrice(returnReason);

            // then
            assertThat(returnPrice).isEqualTo(Money.of(20000)); // 최종가격 28000 + 사용포인트 2000 - 배송비 10000
        }
    }

    @Nested
    @DisplayName("주문 상태 변경 테스트")
    class StatusTransitionTest {

        @Test
        @DisplayName("결제 완료 - BEFORE_PAYMENT에서 PENDING으로 변경")
        void completePayment_success() {
            // given
            Order order = createTestOrder();
            assertThat(order.getStatus()).isEqualTo(OrderStatus.BEFORE_PAYMENT);

            // when
            order.completePayment();

            // then
            assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
        }

        @Test
        @DisplayName("결제 완료 - 잘못된 상태에서 호출 시 예외 발생")
        void completePayment_invalidStatus_throwsException() {
            // given
            Order order = createTestOrder();
            order.completePayment(); // PENDING 상태로 변경

            // when & then
            assertThatThrownBy(order::completePayment)
                    .isInstanceOf(InvalidStatusException.class);
        }

        @Test
        @DisplayName("발송 처리 - PENDING에서 SHIPPED로 변경")
        void shipped_success() {
            // given
            Order order = createTestOrder();
            order.completePayment(); // PENDING 상태로 변경

            // when
            order.shipped();

            // then
            assertThat(order.getStatus()).isEqualTo(OrderStatus.SHIPPED);
        }

        @Test
        @DisplayName("발송 처리 - 잘못된 상태에서 호출 시 예외 발생")
        void shipped_invalidStatus_throwsException() {
            // given
            Order order = createTestOrder(); // BEFORE_PAYMENT 상태

            // when & then
            assertThatThrownBy(order::shipped)
                    .isInstanceOf(InvalidStatusException.class);
        }

        @Test
        @DisplayName("배송 완료 - SHIPPED에서 DELIVERED로 변경")
        void delivered_success() {
            // given
            Order order = createTestOrder();
            order.completePayment();
            order.shipped();

            // when
            order.delivered();

            // then
            assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERED);
        }

        @Test
        @DisplayName("배송 완료 - 잘못된 상태에서 호출 시 예외 발생")
        void delivered_invalidStatus_throwsException() {
            // given
            Order order = createTestOrder();
            order.completePayment(); // PENDING 상태

            // when & then
            assertThatThrownBy(order::delivered)
                    .isInstanceOf(InvalidStatusException.class);
        }

        @Test
        @DisplayName("주문 취소 - BEFORE_PAYMENT에서 CANCELLED로 변경")
        void cancelled_fromBeforePayment_success() {
            // given
            Order order = createTestOrder(); // BEFORE_PAYMENT 상태

            // when
            order.cancelled();

            // then
            assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        }

        @Test
        @DisplayName("주문 취소 - PENDING에서 CANCELLED로 변경")
        void cancelled_fromPending_success() {
            // given
            Order order = createTestOrder();
            order.completePayment(); // PENDING 상태로 변경

            // when
            order.cancelled();

            // then
            assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        }

        @Test
        @DisplayName("주문 취소 - 잘못된 상태에서 호출 시 예외 발생")
        void cancelled_invalidStatus_throwsException() {
            // given
            Order order = createTestOrder();
            order.completePayment();
            order.shipped(); // SHIPPED 상태

            // when & then
            assertThatThrownBy(order::cancelled)
                    .isInstanceOf(InvalidStatusException.class);
        }

        @Test
        @DisplayName("결제 취소 - BEFORE_PAYMENT에서 CANCELLED로 변경")
        void cancelPayment_success() {
            // given
            Order order = createTestOrder(); // BEFORE_PAYMENT 상태

            // when
            order.cancelPayment();

            // then
            assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        }

        @Test
        @DisplayName("결제 취소 - 잘못된 상태에서 호출 시 예외 발생")
        void cancelPayment_invalidStatus_throwsException() {
            // given
            Order order = createTestOrder();
            order.completePayment(); // PENDING 상태로 변경

            // when & then
            assertThatThrownBy(order::cancelPayment)
                    .isInstanceOf(InvalidStatusException.class);
        }

        @Test
        @DisplayName("반품 처리 - DELIVERED에서 RETURNED로 변경")
        void returned_success() {
            // given
            Order order = createTestOrder();
            order.completePayment();
            order.shipped();
            order.delivered();
            ReturnReason returnReason = ReturnReason.UNUSED;

            // when
            order.returned(returnReason);

            // then
            assertThat(order.getStatus()).isEqualTo(OrderStatus.RETURNED);
        }

        @Test
        @DisplayName("반품 처리 - 잘못된 상태에서 호출 시 예외 발생")
        void returned_invalidStatus_throwsException() {
            // given
            Order order = createTestOrder();
            order.completePayment(); // PENDING 상태
            ReturnReason returnReason = ReturnReason.UNUSED;

            // when & then
            assertThatThrownBy(() -> order.returned(returnReason))
                    .isInstanceOf(InvalidStatusException.class);
        }
    }

    @Nested
    @DisplayName("적립금 관리 테스트")
    class EarnedPointTest {

        @Test
        @DisplayName("적립금을 설정하고 조회한다")
        void setAndGetEarnedPoint_success() {
            // given
            Order order = createTestOrder();
            Money earnedPoint = Money.of(1000);

            // when
            order.setEarnedPoint(earnedPoint);

            // then
            assertThat(order.getEarnedPoint()).isEqualTo(earnedPoint);
        }
    }

    // 테스트 헬퍼 메소드들
    private Order createTestOrder() {
        return Order.createOrder(
                createTestOrderer(),
                createTestShippingInfo(),
                createTestOrderPrice(),
                createTestDiscounts(),
                createTestOrderProducts()
        );
    }

    private Orderer createTestOrderer() {
        return Orderer.createOrderer(1L, "홍길동", "010-1234-5678", "test@example.com");
    }

    private ShippingInfo createTestShippingInfo() {
        return ShippingInfo.create(
                "김수령",
                "010-9876-5432",
                "receiver@example.com",
                "서울시 강남구 테헤란로 123",
                LocalDate.now().plusDays(3)
        );
    }

    private OrderPrice createTestOrderPrice() {
        return OrderPrice.create(
                Money.of(20000), // 상품 가격
                Money.of(3000),  // 포장 가격
                Money.of(10000)  // 배송 가격
        );
    }

    private Discounts createTestDiscounts() {
        Discounts discounts = new Discounts(Money.of(3000), Money.of(2000), 1L);
        discounts.setEarnedPoint(Money.of(500));
        return discounts;
    }

    private List<OrderProduct> createTestOrderProducts() {
        List<OrderProduct> orderProducts = new ArrayList<>();
        orderProducts.add(createTestOrderProduct("9788901234567"));
        orderProducts.add(createTestOrderProduct("9788901234568"));
        return orderProducts;
    }

    private OrderProduct createTestOrderProduct(String isbn) {
        return OrderProduct.builder()
                .isbn(isbn)
                .qty(1)
                .amount(Money.of(10000))
                .packagingRequest(true)
                .discountAmount(Money.of(0))
                .finalAmount(Money.of(10000))
                .appliedCoupon(null)
                .build();
    }
}