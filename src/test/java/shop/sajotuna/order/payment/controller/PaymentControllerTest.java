package shop.sajotuna.order.payment.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import shop.sajotuna.order.payment.domain.Payment;
import shop.sajotuna.order.payment.domain.PaymentMethod;
import shop.sajotuna.order.payment.dto.PaymentConfirmRequest;
import shop.sajotuna.order.payment.dto.PaymentResponse;
import shop.sajotuna.order.payment.service.PaymentService;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.orders.domain.*;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(PaymentController.class)
@ActiveProfiles("test")
public class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("단일 결제 정보 조회")
    void getPayment() throws Exception {
        // given
        Long paymentId = 1L;

        Payment payment = mock(Payment.class);

        when(payment.getId()).thenReturn(paymentId);
        when(payment.getMethod()).thenReturn(PaymentMethod.CARD);
        when(payment.getAmount()).thenReturn(Money.of(10000));
        when(payment.getCreatedAt()).thenReturn(LocalDateTime.now());

        PaymentResponse response = PaymentResponse.from(payment);

        when(paymentService.getPayment(paymentId)).thenReturn(response);

        // when & then
        mockMvc.perform(get("/api/payments/{payment-id}", paymentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(paymentId))
                .andExpect(jsonPath("$.amount").value(10000))
                .andExpect(jsonPath("$.method").value("CARD"));
    }

    @Test
    @DisplayName("결제 승인 요청")
    void confirmPayment() throws Exception {
        // given
        Order order = Mockito.mock(Order.class);

        lenient().when(order.getOrderNumber()).thenReturn("order123");
        lenient().when(order.getFinalPrice()).thenReturn(Money.of(10000));

        PaymentConfirmRequest request = new PaymentConfirmRequest(PaymentMethod.CARD, "order123", 10000, "payKey123");
        PaymentResponse response = PaymentResponse.from(new Payment(order, PaymentMethod.CARD));

        lenient().when(paymentService.processUserPayment(any(PaymentConfirmRequest.class))).thenReturn(response);

        // when & then
        mockMvc.perform(post("/api/payments/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(10000))
                .andExpect(jsonPath("$.method").value("CARD"));
    }
}
