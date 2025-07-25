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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import shop.sajotuna.order.orders.controller.dto.response.OrderResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    @DisplayName("주문 조회 - 주문 번호로 찾을 수 없음")
    void getOrderInfo_orderNotFoundByOrderNumber() {
        // given
        String nonExistentOrderNumber = "NON_EXISTENT";

        // when
        when(orderRepository.findOrderByOrderNumber(nonExistentOrderNumber))
                .thenReturn(null); // 또는 적절한 예외 발생

        // then
        assertThrows(OrderNotFoundException.class,
                () -> orderQueryService.getOrderInfo(nonExistentOrderNumber));
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

    @Test
    @DisplayName("주문번호로 주문 상세 조회")
    void findOrderDetailByOrderNumber() {
        // given
        Order fakeOrder = createTestOrder();
        String orderNumber = fakeOrder.getOrderNumber();

        // when
        when(orderRepository.findOrderByOrderNumber(orderNumber))
                .thenReturn(fakeOrder);

        OrderDetailResponse response = orderQueryService.findOrderDetailByOrderNumber(orderNumber);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getOrderNumber()).isEqualTo(orderNumber);
        assertThat(response.getStatus()).isEqualTo(OrderStatus.PENDING);
    }

    @Test
    @DisplayName("주문번호로 주문 상세 조회 - 주문 찾을 수 없음")
    void findOrderDetailByOrderNumber_orderNotFound() {
        // given
        String nonExistentOrderNumber = "NON_EXISTENT";

        // when
        when(orderRepository.findOrderByOrderNumber(nonExistentOrderNumber))
                .thenReturn(null);

        // then
        assertThrows(OrderNotFoundException.class,
                () -> orderQueryService.findOrderDetailByOrderNumber(nonExistentOrderNumber));
    }

    @Test
    @DisplayName("모든 주문 조회 - 페이징")
    void findAllOrders() {
        // given
        List<Order> orders = List.of(createTestOrder(), createTestOrder());
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> orderPage = new PageImpl<>(orders, pageable, orders.size());

        // when
        when(orderRepository.findAllBy(pageable)).thenReturn(orderPage);

        Page<OrderResponse> response = orderQueryService.findAllOrders(pageable);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(2);
        assertThat(response.getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("사용자별 주문 조회 - 페이징")
    void findOrdersByUserId() {
        // given
        long userId = 1L;
        List<Order> orders = List.of(createTestOrder(), createTestOrder());
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> orderPage = new PageImpl<>(orders, pageable, orders.size());

        // when
        when(orderRepository.findOrdersByOrdererUserIdOrderByCreatedAtDesc(userId, pageable))
                .thenReturn(orderPage);

        Page<OrderInfoResponse> response = orderQueryService.findOrdersByUserId(userId, pageable);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(2);
        assertThat(response.getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("주문 상태별 조회 - 페이징")
    void findOrdersByStatus() {
        // given
        OrderStatus status = OrderStatus.PENDING;
        List<Order> orders = List.of(createTestOrder(), createTestOrder());
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> orderPage = new PageImpl<>(orders, pageable, orders.size());

        // when
        when(orderRepository.findOrdersByStatus(status, pageable)).thenReturn(orderPage);

        Page<OrderResponse> response = orderQueryService.findOrdersByStatus(status, pageable);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(2);
        assertThat(response.getTotalElements()).isEqualTo(2);
        assertThat(response.getContent().get(0).getStatus()).isEqualTo(status);
    }

    private Order createTestOrder() {
        Orderer orderer = new Orderer(1L, "홍길동", "010-1234-5678", "test@example.com");
        ShippingInfo shippingInfo = ShippingInfo.create(
                "홍길동", "010-1234-5678", "test@example.com",
                "서울시 강남구 테헤란로 123",
                LocalDate.now().plusDays(3)
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