package shop.sajotuna.order.payment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.orders.domain.Order;
import shop.sajotuna.order.orders.domain.Orderer;
import shop.sajotuna.order.orders.domain.OrderStatus;
import shop.sajotuna.order.orders.repository.OrderRepository;
import shop.sajotuna.order.payment.domain.Payment;
import shop.sajotuna.order.payment.domain.PaymentMethod;
import shop.sajotuna.order.payment.dto.PaymentConfirmRequest;
import shop.sajotuna.order.payment.dto.PaymentResponse;
import shop.sajotuna.order.payment.exception.PaymentFailException;
import shop.sajotuna.order.payment.repository.PaymentRepository;
import shop.sajotuna.order.point.service.dto.event.UserGradeRefreshEvent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CardPaymentService cardPaymentService;


    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private ExternalPaymentServiceFactory externalPaymentServiceFactory;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @BeforeEach
    void setup() {
        paymentService = new PaymentService(paymentRepository, externalPaymentServiceFactory, orderRepository, eventPublisher);
    }

    @Test
    @DisplayName("결제 정보 확인")
    void getPaymentById() {
        Payment payment = Mockito.mock(Payment.class);

        when(payment.getId()).thenReturn(1L);
        when(payment.getAmount()).thenReturn(Money.of(10000));
        when(payment.getMethod()).thenReturn(PaymentMethod.CARD);

        given(paymentRepository.findById(1L)).willReturn(Optional.of(payment));

        PaymentResponse response = paymentService.getPayment(1L);

        assertThat(response).isNotNull();
        assertThat(response.getAmount()).isEqualTo(10000);
        assertThat(response.getMethod()).isEqualTo(PaymentMethod.CARD);
    }

    @Test
    @DisplayName("모든 결제 정보 조회 성공")
    void getAllPayments_success() {
        // given
        Payment payment1 = mock(Payment.class);
        Payment payment2 = mock(Payment.class);

        when(payment1.getId()).thenReturn(1L);
        when(payment1.getAmount()).thenReturn(Money.of(10000));
        when(payment1.getMethod()).thenReturn(PaymentMethod.CARD);

        when(payment2.getId()).thenReturn(1L);
        when(payment2.getAmount()).thenReturn(Money.of(10000));
        when(payment2.getMethod()).thenReturn(PaymentMethod.CARD);

        when(paymentRepository.findAll()).thenReturn(List.of(payment1, payment2));

        // when
        List<PaymentResponse> responses = paymentService.getAllPayments();

        // then
        assertEquals(2, responses.size());
        verify(paymentRepository).findAll();
    }

    @Test
    @DisplayName("결제 완료")
    void processUserPayment_card() {
        // given
        Order order = Mockito.mock(Order.class);

        lenient().when(order.getOrderNumber()).thenReturn("testtest");
        lenient().when(order.getStatus()).thenReturn(OrderStatus.BEFORE_PAYMENT);
        lenient().when(order.getFinalPrice()).thenReturn(Money.of(10000));
        lenient().when(order.getOrderer()).thenReturn(new Orderer(1L, "tester", "010-1234-5678", "test@example.com"));

        when(externalPaymentServiceFactory.getService(PaymentMethod.CARD)).thenReturn(cardPaymentService);
        when(orderRepository.findOrderByOrderNumber("testtest")).thenReturn(order);

        PaymentConfirmRequest request = new PaymentConfirmRequest(
                PaymentMethod.CARD, "testtest", 10000, UUID.randomUUID().toString()
        );

        PaymentResponse expectedResponse = PaymentResponse.from(new Payment(order, PaymentMethod.CARD));
        when(cardPaymentService.requestPaymentConfirm(request)).thenReturn(expectedResponse);

        // when
        PaymentResponse actualResponse = paymentService.processUserPayment(request);

        // then
        assertEquals(expectedResponse, actualResponse);
        InOrder inOrder = inOrder(cardPaymentService, order, eventPublisher);
        inOrder.verify(cardPaymentService).requestPaymentConfirm(request);
        inOrder.verify(order).completePayment();
        inOrder.verify(eventPublisher).publishEvent(any(UserGradeRefreshEvent.class));
    }

    @Test
    @DisplayName("결제 승인 실패 시 주문 완료와 등급 갱신 이벤트를 실행하지 않는다")
    void processUserPayment_paymentConfirmFailed_doesNotCompleteOrder() {
        Order order = Mockito.mock(Order.class);
        PaymentConfirmRequest request = new PaymentConfirmRequest(
                PaymentMethod.CARD, "testtest", 10000, UUID.randomUUID().toString()
        );

        when(externalPaymentServiceFactory.getService(PaymentMethod.CARD)).thenReturn(cardPaymentService);
        when(orderRepository.findOrderByOrderNumber("testtest")).thenReturn(order);
        when(cardPaymentService.requestPaymentConfirm(request)).thenThrow(new PaymentFailException());

        assertThatThrownBy(() -> paymentService.processUserPayment(request))
                .isInstanceOf(PaymentFailException.class);

        verify(order, never()).completePayment();
        verify(eventPublisher, never()).publishEvent(any(UserGradeRefreshEvent.class));
    }

    @Test
    @DisplayName("결제 취소 처리 성공")
    void cancelPayment_success() {
        // given
        Long orderId = 100L;
        Payment payment = mock(Payment.class);

        when(paymentRepository.getPaymentByOrder_Id(orderId)).thenReturn(payment);
        when(payment.getMethod()).thenReturn(PaymentMethod.CARD);
        when(externalPaymentServiceFactory.getService(PaymentMethod.CARD)).thenReturn(cardPaymentService);

        // when
        paymentService.cancelPayment(orderId, "사용자 요청");

        // then
        verify(cardPaymentService).requestPaymentCancel(payment, "사용자 요청");
    }
}
