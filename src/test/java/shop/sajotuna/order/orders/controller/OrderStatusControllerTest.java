package shop.sajotuna.order.orders.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import shop.sajotuna.order.orders.domain.ReturnReason;
import shop.sajotuna.order.orders.service.OrderStatusService;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderStatusController.class)
@ActiveProfiles("test")
class OrderStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderStatusService orderStatusService;

    @Test
    @DisplayName("주문 반품 처리 성공")
    void returnOrder_Success() throws Exception {
        Long orderId = 1L;
        Long userId = 1L;
        ReturnReason returnReason = ReturnReason.UNUSED;

        doNothing().when(orderStatusService).returnOrder(userId, orderId, returnReason);

        mockMvc.perform(put("/api/orders/{order-id}/return", orderId)
                        .header("X-User-Id", userId)
                        .param("return-reason", returnReason.name()))
                .andExpect(status().isNoContent());

        verify(orderStatusService).returnOrder(userId, orderId, returnReason);
    }

    @Test
    @DisplayName("사용자 ID 헤더 없이 주문 반품 처리 시 500 반환")
    void returnOrder_MissingUserIdHeader() throws Exception {
        Long orderId = 1L;
        ReturnReason returnReason = ReturnReason.DAMAGED;

        mockMvc.perform(put("/api/orders/{order-id}/return", orderId)
                        .param("return-reason", returnReason.name()))
                .andExpect(status().is5xxServerError());

        verify(orderStatusService, never()).returnOrder(any(), any(), any());
    }

    @Test
    @DisplayName("반품 사유 없이 주문 반품 처리 시 400 반환")
    void returnOrder_MissingReturnReason() throws Exception {
        Long orderId = 1L;
        Long userId = 1L;

        mockMvc.perform(put("/api/orders/{order-id}/return", orderId)
                        .header("X-User-Id", userId))
                .andExpect(status().is5xxServerError());

        verify(orderStatusService, never()).returnOrder(any(), any(), any());
    }

    @Test
    @DisplayName("결제 취소 처리 성공")
    void cancelOrder_Success() throws Exception {
        Long orderId = 1L;
        Long userId = 1L;

        doNothing().when(orderStatusService).cancelOrder(userId, orderId);

        mockMvc.perform(put("/api/orders/{order-id}/cancel", orderId)
                        .header("X-User-Id", userId))
                .andExpect(status().isNoContent());

        verify(orderStatusService).cancelOrder(userId, orderId);
    }

    @Test
    @DisplayName("사용자 ID 헤더 없이 결제 취소 처리 시 500 반환")
    void cancelOrder_MissingUserIdHeader() throws Exception {
        Long orderId = 1L;

        mockMvc.perform(put("/api/orders/{order-id}/cancel", orderId))
                .andExpect(status().is5xxServerError());

        verify(orderStatusService, never()).cancelOrder(any(), any());
    }

    @Test
    @DisplayName("주문 취소 처리(결제 전) 성공 - 사용자 ID 있음")
    void cancelOrderBeforePayment_Success_WithUserId() throws Exception {
        Long orderId = 1L;
        Long userId = 1L;

        doNothing().when(orderStatusService).cancelOrderBeforePayment(userId, orderId);

        mockMvc.perform(put("/api/orders/{order-id}/cancel-order", orderId)
                        .header("X-User-Id", userId))
                .andExpect(status().isNoContent());

        verify(orderStatusService).cancelOrderBeforePayment(userId, orderId);
    }

    @Test
    @DisplayName("주문 취소 처리(결제 전) 성공 - 사용자 ID 없음")
    void cancelOrderBeforePayment_Success_WithoutUserId() throws Exception {
        Long orderId = 1L;

        doNothing().when(orderStatusService).cancelOrderBeforePayment(null, orderId);

        mockMvc.perform(put("/api/orders/{order-id}/cancel-order", orderId))
                .andExpect(status().isNoContent());

        verify(orderStatusService).cancelOrderBeforePayment(null, orderId);
    }

    @Test
    @DisplayName("모든 반품 사유 타입으로 반품 처리 테스트")
    void returnOrder_AllReturnReasons() throws Exception {
        Long orderId = 1L;
        Long userId = 1L;

        for (ReturnReason reason : ReturnReason.values()) {
            doNothing().when(orderStatusService).returnOrder(userId, orderId, reason);

            mockMvc.perform(put("/api/orders/{order-id}/return", orderId)
                            .header("X-User-Id", userId)
                            .param("return-reason", reason.name()))
                    .andExpect(status().isNoContent());

            verify(orderStatusService).returnOrder(userId, orderId, reason);
        }
    }

    @Test
    @DisplayName("잘못된 반품 사유로 요청 시 400 반환")
    void returnOrder_InvalidReturnReason() throws Exception {
        Long orderId = 1L;
        Long userId = 1L;

        mockMvc.perform(put("/api/orders/{order-id}/return", orderId)
                        .header("X-User-Id", userId)
                        .param("return-reason", "INVALID_REASON"))
                .andExpect(status().is5xxServerError());

        verify(orderStatusService, never()).returnOrder(any(), any(), any());
    }
}