package shop.sajotuna.order.payment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.orders.domain.Order;
import shop.sajotuna.order.orders.repository.OrderRepository;
import shop.sajotuna.order.payment.domain.Payment;
import shop.sajotuna.order.payment.domain.PaymentMethod;
import shop.sajotuna.order.payment.dto.PaymentConfirmRequest;
import shop.sajotuna.order.payment.dto.PaymentResponse;
import shop.sajotuna.order.payment.exception.PaymentFailException;
import shop.sajotuna.order.payment.repository.PaymentRepository;
import shop.sajotuna.order.payment.repository.TossPaymentRepository;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TossPaymentServiceTest {
    @InjectMocks
    private TossPaymentService tossPaymentService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private TossPaymentRepository tossPaymentRepository;

    @BeforeEach
    void setup() {
        // secretKey @Value 주입용
        ReflectionTestUtils.setField(tossPaymentService, "secretKey", "sk_test_abc123");
    }

    @Test
    @DisplayName("토스 결제 승인 요청 - 성공")
    void requestPaymentConfirm_success() throws Exception {
        // given
        PaymentConfirmRequest request = new PaymentConfirmRequest(
                PaymentMethod.TOSS, "order123", 10000, "payKey123"
        );

        Order order = mock(Order.class);
        lenient().when(order.getOrderNumber()).thenReturn("order123");
        lenient().when(order.getFinalPrice()).thenReturn(Money.of(10000));
        lenient().when(orderRepository.findOrderByOrderNumber("order123")).thenReturn(order);

        Payment payment = new Payment(order, PaymentMethod.TOSS);
        lenient().when(paymentRepository.save(any())).thenReturn(payment);

        // HttpClient mocking - Java 기본 API는 직접 Mock 어려우므로 스파이 또는 래핑 필요
        HttpClient mockClient = mock(HttpClient.class);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);

        lenient().when(mockResponse.statusCode()).thenReturn(200);
        lenient().when(mockResponse.body()).thenReturn("{...}");

        lenient().when(mockClient.send(any(), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(mockResponse);

        // HttpClient.newHttpClient() → 대체할 수단 필요
        // → 아래에서 설명

        // 실제 저장하는 객체는 단순히 저장됐는지만 확인
        when(paymentRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(tossPaymentRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        // when - 강제 대체된 HttpClient로 테스트 실행
        try (MockedStatic<HttpClient> httpClientStatic = mockStatic(HttpClient.class)) {
            httpClientStatic.when(HttpClient::newHttpClient).thenReturn(mockClient);

            // when
            PaymentResponse response = tossPaymentService.requestPaymentConfirm(request);

            // then
            assertNotNull(response);
            verify(orderRepository).findOrderByOrderNumber("order123");
            verify(paymentRepository).save(any());
            verify(tossPaymentRepository).save(any());
        }
    }

    @Test
    @DisplayName("토스 결제 승인 요청 - 실패")
    void requestPaymentConfirm_failStatus() throws Exception {
        PaymentConfirmRequest request = new PaymentConfirmRequest(PaymentMethod.TOSS, "order123", 10000, "payKey123");

        HttpClient mockClient = mock(HttpClient.class);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);

        when(mockResponse.statusCode()).thenReturn(400); // 실패
        when(mockClient.send(any(), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(mockResponse);

        try (MockedStatic<HttpClient> httpClientStatic = mockStatic(HttpClient.class)) {
            httpClientStatic.when(HttpClient::newHttpClient).thenReturn(mockClient);

            assertThrows(PaymentFailException.class, () -> tossPaymentService.requestPaymentConfirm(request));
        }
    }

    @Test
    @DisplayName("지원하는 결제 방식 확인")
    void support_shouldReturnTrueForToss() {
        assertEquals(PaymentMethod.TOSS, tossPaymentService.getPaymentMethod());
    }
}
