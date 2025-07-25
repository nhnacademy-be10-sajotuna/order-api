package shop.sajotuna.order.payment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.orders.domain.Order;
import shop.sajotuna.order.orders.domain.OrderStatus;
import shop.sajotuna.order.orders.repository.OrderRepository;
import shop.sajotuna.order.payment.domain.Payment;
import shop.sajotuna.order.payment.domain.PaymentMethod;
import shop.sajotuna.order.payment.dto.PaymentConfirmRequest;
import shop.sajotuna.order.payment.dto.PaymentResponse;
import shop.sajotuna.order.payment.repository.PaymentRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
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

    @Mock
    private TossPaymentService tossPaymentService;

    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    void setup() {
        paymentService = new PaymentService(paymentRepository, List.of(cardPaymentService, tossPaymentService), orderRepository);
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
    @DisplayName("결제 완료")
    void processUserPayment_card() {
        // given
        Order order = Mockito.mock(Order.class);

        lenient().when(order.getOrderNumber()).thenReturn("testtest");
        lenient().when(order.getStatus()).thenReturn(OrderStatus.BEFORE_PAYMENT);
        lenient().when(order.getFinalPrice()).thenReturn(Money.of(10000));

        when(cardPaymentService.support(PaymentMethod.CARD)).thenReturn(true);
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
        verify(order).completePayment();
        verify(cardPaymentService).requestPaymentConfirm(request);
    }
}
