package shop.sajotuna.order.orders.controller;

import org.aspectj.weaver.ast.Or;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.orders.controller.dto.response.OrderResponse;
import shop.sajotuna.order.orders.domain.*;
import shop.sajotuna.order.orders.service.OrderQueryService;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderAdminController.class)
@ActiveProfiles("test")
public class OrderAdminControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderQueryService orderQueryService;

    Order order = createTestOrder();

    @Test
    @DisplayName("모든 주문 목록 조회 성공")
    void getAllOrders() throws Exception {
        // given
        List<OrderResponse> content = List.of(
                OrderResponse.from(order)
        );
        Page<OrderResponse> page = new PageImpl<>(content);

        when(orderQueryService.findAllOrders(any(Pageable.class))).thenReturn(page);

        // when & then
        mockMvc.perform(get("/api/admin/orders")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("배송 상태로 주문 조회 성공")
    void getOrdersByStatus_success() throws Exception {
        // given
        List<OrderResponse> content = List.of(
                OrderResponse.from(order)
        );
        Page<OrderResponse> page = new PageImpl<>(content);

        when(orderQueryService.findOrdersByStatus(eq(OrderStatus.PENDING), any(Pageable.class)))
                .thenReturn(page);

        // when & then
        mockMvc.perform(get("/api/admin/orders/PENDING")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("잘못된 상태 값으로 요청 시 예외 발생")
    void getOrdersByStatus_invalidStatus() throws Exception {
        mockMvc.perform(get("/api/admin/orders/INVALID_STATUS"))
                .andExpect(status().isBadRequest());
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
