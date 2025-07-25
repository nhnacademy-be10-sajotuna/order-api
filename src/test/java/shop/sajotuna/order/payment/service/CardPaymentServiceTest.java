package shop.sajotuna.order.payment.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.orders.domain.Order;
import shop.sajotuna.order.orders.repository.OrderRepository;
import shop.sajotuna.order.payment.domain.Payment;
import shop.sajotuna.order.payment.domain.PaymentMethod;
import shop.sajotuna.order.payment.dto.PaymentConfirmRequest;
import shop.sajotuna.order.payment.dto.PaymentResponse;
import shop.sajotuna.order.payment.repository.PaymentRepository;
import shop.sajotuna.order.point.exception.OrderNotFoundException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CardPaymentServiceTest {
    @InjectMocks
    private CardPaymentService cardPaymentService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Test
    @DisplayName("카드 결제 승인 성공")
    void requestPaymentConfirm_success() {
        // given
        String orderNumber = "order123";
        Order order = mock(Order.class);
        lenient().when(orderRepository.findOrderByOrderNumber(orderNumber)).thenReturn(order);
        lenient().when(order.getOrderNumber()).thenReturn("order123");
        lenient().when(order.getFinalPrice()).thenReturn(Money.of(10000));

        PaymentConfirmRequest request = new PaymentConfirmRequest(
                PaymentMethod.CARD,
                orderNumber,
                10000,
                UUID.randomUUID().toString()
        );

        Payment payment = new Payment(order, PaymentMethod.CARD);

        // paymentRepository.save(...)가 저장된 객체 반환하도록 설정
        when(paymentRepository.save(any())).thenReturn(payment);

        // when
        PaymentResponse response = cardPaymentService.requestPaymentConfirm(request);

        // then
        assertNotNull(response);
        assertEquals(PaymentMethod.CARD, payment.getMethod());
        verify(orderRepository).findOrderByOrderNumber(orderNumber);
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    @DisplayName("지원하는 결제 방식: CARD")
    void support_card() {
        assertTrue(cardPaymentService.support(PaymentMethod.CARD));
        assertFalse(cardPaymentService.support(PaymentMethod.TOSS));
    }

    @Test
    @DisplayName("카드 결제 승인 실패 - 존재하지 않는 주문")
    void requestPaymentConfirm_orderNotFound() {
        // given
        String orderNumber = "NON_EXISTENT";
        when(orderRepository.findOrderByOrderNumber(orderNumber)).thenReturn(null);

        PaymentConfirmRequest request = new PaymentConfirmRequest(
                PaymentMethod.CARD,
                orderNumber,
                10000,
                UUID.randomUUID().toString()
        );

        // when & then
        assertThrows(OrderNotFoundException.class, () -> cardPaymentService.requestPaymentConfirm(request));

        verify(orderRepository).findOrderByOrderNumber(orderNumber);
        verify(paymentRepository, never()).save(any());
    }

    @Test
    @DisplayName("카드 결제 취소 로그 출력 (예외 없이 종료)")
    void requestPaymentCancel_logsSuccessfully() {
        Payment payment = mock(Payment.class);
        assertDoesNotThrow(() ->
                cardPaymentService.requestPaymentCancel(payment, "사용자 요청")
        );
    }
}
