package shop.sajotuna.order.orders.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.orders.controller.dto.response.OrderDetailResponse;
import shop.sajotuna.order.orders.controller.dto.response.OrderInfoResponse;
import shop.sajotuna.order.orders.domain.*;
import shop.sajotuna.order.orders.repository.OrderRepository;
import shop.sajotuna.order.orders.service.product.OrderProductService;
import shop.sajotuna.order.payment.repository.PaymentRepository;
import shop.sajotuna.order.point.exception.OrderNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class OrderQueryServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderProductService orderProductService;

    @InjectMocks
    private OrderQueryService orderQueryService;

    @Test
    @DisplayName("주문 조회 - OrderInfoResponse")
    void getOrderInfo() {
        // given
        Order fakeOrder = createTestOrder();
        String orderNumber = fakeOrder.getOrderNumber();

        // when
        when(orderRepository.findOrderByOrderNumber(orderNumber))
                .thenReturn(fakeOrder);

        OrderInfoResponse response = orderQueryService.getOrderInfo(orderNumber);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getOrderNumber()).isEqualTo(orderNumber);
        assertThat(response.getStatus()).isEqualTo(OrderStatus.PENDING);
    }

    @Test
    @DisplayName("주문 조회 - OrderDetailResponse")
    void getOrderInfo_orderDetail() {
        // given
        Order fakeOrder = createTestOrder();

        // when
        when(orderRepository.findById(1L)).thenReturn(Optional.of(fakeOrder));

        OrderDetailResponse response = orderQueryService.findOrderDetail(1L);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getOrderNumber()).isEqualTo(fakeOrder.getOrderNumber());
        assertThat(response.getStatus()).isEqualTo(OrderStatus.PENDING);
    }


    @Test
    @DisplayName("주문 조회 - 주문 찾을 수 없음")
    void getOrderInfo_orderNotFound() {
        lenient().when(orderRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class,
                () -> orderQueryService.findOrderDetail(1L));
    }

    private Order createTestOrder() {
        Orderer orderer = new Orderer(1L, "홍길동", "010-1234-5678", "test@example.com");
        ShippingInfo shippingInfo = ShippingInfo.create(
                "홍길동", "010-1234-5678", "test@example.com",
                "서울시 강남구 테헤란로 123",
                LocalDateTime.now().plusDays(3)
        );
        OrderPrice orderPrice = OrderPrice.create(Money.of(17000), Money.of(0), Money.of(3000));
        Discounts discounts = new Discounts(Money.of(0), Money.of(0), null);

        OrderProduct orderProduct = createTestOrderProduct();

        Order order = Order.createOrder(orderer, shippingInfo, orderPrice, discounts, List.of(orderProduct));

        order.completePayment();

        return order;
    }

    private OrderProduct createTestOrderProduct() {
        return OrderProduct.builder()
                .isbn("1235433121")
                .qty(1)
                .amount(Money.of(10000))
                .packagingRequest(false)
                .build();
    }
}
