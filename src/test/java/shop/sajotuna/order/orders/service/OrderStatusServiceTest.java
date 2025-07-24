package shop.sajotuna.order.orders.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.orders.domain.*;
import shop.sajotuna.order.orders.repository.OrderRepository;
import shop.sajotuna.order.payment.service.PaymentService;
import shop.sajotuna.order.point.domain.PointPolicyType;
import shop.sajotuna.order.point.exception.OrderNotFoundException;
import shop.sajotuna.order.point.service.PointService;
import shop.sajotuna.order.point.service.dto.event.PointEarnRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderStatusServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private PaymentService paymentService;

    @Mock
    private PointService pointService;

    @Mock
    private RefundService refundService;



    @InjectMocks
    private OrderStatusService orderStatusService;

    @Test
    @DisplayName("반품 처리 성공 - 미사용 반품 (10일 이내)")
    void returnedOrder_unused_success() {
        // given
        Long userId = 1L;
        Long orderId = 1L;
        ReturnReason returnReason = ReturnReason.UNUSED;
        
        Order order = createTestOrder();
        OrderProduct orderProduct = createTestOrderProduct("9781234567890", 2);
        order.getOrderProducts().add(orderProduct);
        
        when(orderRepository.findByIdWithOrderProducts(orderId)).thenReturn(Optional.of(order));

        // when
        orderStatusService.returnOrder(userId, orderId, returnReason);

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.RETURNED);
        verify(refundService).returnStock(order);
        verify(refundService).returnCoupon(order);
        verify(pointService).returnPoints(userId, order.getEarnedPoint());
        verify(eventPublisher).publishEvent(any(PointEarnRequest.class));
    }

    @Test
    @DisplayName("반품 처리 성공 - 파손 반품 (30일 이내)")
    void returnedOrder_damaged_success() {
        // given
        Long userId = 1L;
        Long orderId = 1L;
        ReturnReason returnReason = ReturnReason.DAMAGED;
        
        Order order = createTestOrder();
        OrderProduct orderProduct = createTestOrderProduct("9781234567890", 1);
        order.getOrderProducts().add(orderProduct);
        
        when(orderRepository.findByIdWithOrderProducts(orderId)).thenReturn(Optional.of(order));

        // when
        orderStatusService.returnOrder(userId, orderId, returnReason);

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.RETURNED);
        verify(refundService).returnStock(order);
        verify(refundService).returnCoupon(order);
        verify(pointService).returnPoints(userId, order.getEarnedPoint());
        verify(eventPublisher).publishEvent(any(PointEarnRequest.class));
    }

    @Test
    @DisplayName("반품 처리 성공 - 파본 반품 (30일 이내)")
    void returnedOrder_defective_success() {
        // given
        Long userId = 1L;
        Long orderId = 1L;
        ReturnReason returnReason = ReturnReason.DEFECTIVE;
        
        Order order = createTestOrder();
        OrderProduct orderProduct = createTestOrderProduct("9781234567890", 1);
        order.getOrderProducts().add(orderProduct);
        
        when(orderRepository.findByIdWithOrderProducts(orderId)).thenReturn(Optional.of(order));

        // when
        orderStatusService.returnOrder(userId, orderId, returnReason);

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.RETURNED);
        verify(refundService).returnStock(order);
        verify(refundService).returnCoupon(order);
        verify(pointService).returnPoints(userId, order.getEarnedPoint());
        verify(eventPublisher).publishEvent(any(PointEarnRequest.class));
    }

    @Test
    @DisplayName("반품 처리 실패 - 주문을 찾을 수 없음")
    void returnedOrder_orderNotFound() {
        // given
        Long userId = 1L;
        Long orderId = 999L;
        ReturnReason returnReason = ReturnReason.UNUSED;
        
        when(orderRepository.findByIdWithOrderProducts(orderId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> orderStatusService.returnOrder(userId, orderId, returnReason))
                .isInstanceOf(OrderNotFoundException.class);
        
        verify(refundService, never()).returnStock(any(Order.class));
        verify(refundService, never()).returnCoupon(any(Order.class));
        verify(pointService, never()).returnPoints(anyLong(), any());
        verify(eventPublisher, never()).publishEvent(any(PointEarnRequest.class));
    }

    @Test
    @DisplayName("반품 처리 - 여러 상품 재고 복원")
    void returnedOrder_multipleProducts() {
        // given
        Long userId = 1L;
        Long orderId = 1L;
        ReturnReason returnReason = ReturnReason.UNUSED;
        
        Order order = createTestOrder();
        OrderProduct product1 = createTestOrderProduct("9781234567890", 2);
        OrderProduct product2 = createTestOrderProduct("9781234567891", 1);
        order.getOrderProducts().addAll(List.of(product1, product2));
        
        when(orderRepository.findByIdWithOrderProducts(orderId)).thenReturn(Optional.of(order));

        // when
        orderStatusService.returnOrder(userId, orderId, returnReason);

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.RETURNED);
        verify(refundService).returnStock(order);
        verify(refundService).returnCoupon(order);
        verify(pointService).returnPoints(userId, order.getEarnedPoint());
        verify(eventPublisher).publishEvent(any(PointEarnRequest.class));
    }

    @Test
    @DisplayName("반품 금액 계산 - 미사용 반품시 택배비 차감")
    void returnedOrder_unused_deductShippingFee() {
        // given
        Long userId = 1L;
        Long orderId = 1L;
        ReturnReason returnReason = ReturnReason.UNUSED;
        
        Order order = createTestOrder();
        OrderProduct orderProduct = createTestOrderProduct("9781234567890", 1);
        order.getOrderProducts().add(orderProduct);
        
        when(orderRepository.findByIdWithOrderProducts(orderId)).thenReturn(Optional.of(order));

        // when
        orderStatusService.returnOrder(userId, orderId, returnReason);

        // then
        verify(eventPublisher).publishEvent(argThat((PointEarnRequest pointEvent) -> 
            pointEvent.getUserId().equals(userId) &&
            pointEvent.getType().equals(PointPolicyType.RETURNED) &&
            pointEvent.getPointAmount().equals(order.getReturnPrice(returnReason))
        ));
    }

    @Test
    @DisplayName("반품 금액 계산 - 파손/파본 반품시 택배비 차감 없음")
    void returnedOrder_damaged_noDeductShippingFee() {
        // given
        Long userId = 1L;
        Long orderId = 1L;
        ReturnReason returnReason = ReturnReason.DAMAGED;
        
        Order order = createTestOrder();
        OrderProduct orderProduct = createTestOrderProduct("9781234567890", 1);
        order.getOrderProducts().add(orderProduct);
        
        when(orderRepository.findByIdWithOrderProducts(orderId)).thenReturn(Optional.of(order));

        // when
        orderStatusService.returnOrder(userId, orderId, returnReason);

        // then
        verify(eventPublisher).publishEvent(argThat((PointEarnRequest pointEvent) -> 
            pointEvent.getUserId().equals(userId) &&
            pointEvent.getType().equals(PointPolicyType.RETURNED) &&
            pointEvent.getPointAmount().equals(order.getReturnPrice(returnReason))
        ));
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
        
        Order order = Order.createOrder(orderer, shippingInfo, orderPrice, discounts, List.of());
        order.completePayment();
        order.shipped();
        order.delivered();
        
        return order;
    }

    private OrderProduct createTestOrderProduct(String isbn, int quantity) {
        return OrderProduct.builder()
            .isbn(isbn)
            .qty(quantity)
            .amount(Money.of(10000))
            .packagingRequest(false)
            .build();
    }
}