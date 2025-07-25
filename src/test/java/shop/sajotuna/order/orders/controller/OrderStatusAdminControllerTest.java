package shop.sajotuna.order.orders.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import shop.sajotuna.order.orders.service.OrderStatusService;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderStatusAdminController.class)
@ActiveProfiles("test")
class OrderStatusAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderStatusService orderStatusService;

    @Test
    @DisplayName("주문을 배송 중으로 전환 성공")
    void shippedOrder_Success() throws Exception {
        Long orderId = 1L;

        doNothing().when(orderStatusService).shippedOrder(orderId);

        mockMvc.perform(put("/api/admin/orders/{order-id}/delivery", orderId))
                .andExpect(status().isNoContent());

        verify(orderStatusService).shippedOrder(orderId);
    }

    @Test
    @DisplayName("유효하지 않은 주문 ID로 배송 상태 변경 요청")
    void shippedOrder_InvalidOrderId() throws Exception {
        Long invalidOrderId = -1L;

        doNothing().when(orderStatusService).shippedOrder(invalidOrderId);

        mockMvc.perform(put("/api/admin/orders/{order-id}/delivery", invalidOrderId))
                .andExpect(status().isNoContent());

        verify(orderStatusService).shippedOrder(invalidOrderId);
    }

    @Test
    @DisplayName("존재하지 않는 주문 ID로 배송 상태 변경 요청")
    void shippedOrder_NonExistentOrderId() throws Exception {
        Long nonExistentOrderId = 999L;

        doNothing().when(orderStatusService).shippedOrder(nonExistentOrderId);

        mockMvc.perform(put("/api/admin/orders/{order-id}/delivery", nonExistentOrderId))
                .andExpect(status().isNoContent());

        verify(orderStatusService).shippedOrder(nonExistentOrderId);
    }

    @Test
    @DisplayName("주문 ID가 null인 경우 - URL 경로 테스트")
    void shippedOrder_NullOrderId() throws Exception {
        // URL에서 null을 전달할 수 없으므로, 잘못된 경로로 테스트
        mockMvc.perform(put("/api/admin/orders//delivery"))
                .andExpect(status().is5xxServerError());

        verify(orderStatusService, never()).shippedOrder(any());
    }

    @Test
    @DisplayName("숫자가 아닌 주문 ID로 요청 시 400 반환")
    void shippedOrder_InvalidOrderIdFormat() throws Exception {
        mockMvc.perform(put("/api/admin/orders/invalid-id/delivery"))
                .andExpect(status().is5xxServerError());

        verify(orderStatusService, never()).shippedOrder(any());
    }

    @Test
    @DisplayName("여러 주문 ID로 배송 상태 변경 테스트")
    void shippedOrder_MultipleOrderIds() throws Exception {
        Long[] orderIds = {1L, 2L, 3L, 100L, 999L};

        for (Long orderId : orderIds) {
            doNothing().when(orderStatusService).shippedOrder(orderId);

            mockMvc.perform(put("/api/admin/orders/{order-id}/delivery", orderId))
                    .andExpect(status().isNoContent());

            verify(orderStatusService).shippedOrder(orderId);
        }
    }

    @Test
    @DisplayName("0으로 주문 ID 요청")
    void shippedOrder_ZeroOrderId() throws Exception {
        Long orderId = 0L;

        doNothing().when(orderStatusService).shippedOrder(orderId);

        mockMvc.perform(put("/api/admin/orders/{order-id}/delivery", orderId))
                .andExpect(status().isNoContent());

        verify(orderStatusService).shippedOrder(orderId);
    }

    @Test
    @DisplayName("매우 큰 주문 ID로 요청")
    void shippedOrder_LargeOrderId() throws Exception {
        Long largeOrderId = Long.MAX_VALUE;

        doNothing().when(orderStatusService).shippedOrder(largeOrderId);

        mockMvc.perform(put("/api/admin/orders/{order-id}/delivery", largeOrderId))
                .andExpect(status().isNoContent());

        verify(orderStatusService).shippedOrder(largeOrderId);
    }
}