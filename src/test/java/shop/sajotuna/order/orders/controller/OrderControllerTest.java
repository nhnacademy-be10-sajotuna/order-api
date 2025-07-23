package shop.sajotuna.order.orders.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.orders.controller.dto.response.OrderDetailResponse;
import shop.sajotuna.order.orders.controller.dto.response.OrderInfoResponse;
import shop.sajotuna.order.orders.domain.*;
import shop.sajotuna.order.orders.service.OrderFormService;
import shop.sajotuna.order.orders.service.OrderQueryService;
import shop.sajotuna.order.point.exception.OrderNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@ActiveProfiles("test")
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderQueryService orderQueryService;

    @MockitoBean
    private OrderFormService orderFormService;

    OrderInfoResponse orderInfoResponse;
    OrderDetailResponse orderDetailResponse;
    String orderNumber;
    Long userId = 1L;
    Pageable pageable = PageRequest.of(0, 10);

    @BeforeEach
    public void setup() {
        Order fakeOrder = createTestOrder();

        orderNumber = fakeOrder.getOrderNumber();
        orderInfoResponse = OrderInfoResponse.from(fakeOrder);
        orderDetailResponse = OrderDetailResponse.from(fakeOrder, List.of(), null);
    }

    @Test
    @DisplayName("GET /api/orders/info/{order-number} - 주문 조회")
    void getOrderInfo() throws Exception {

        given(orderQueryService.getOrderInfo(orderNumber)).willReturn(orderInfoResponse);

        mockMvc.perform(get("/api/orders/info/{order-number}", orderNumber))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.orderNumber").value(orderNumber))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.totalProductCount").value(orderInfoResponse.getTotalProductCount()));
    }

    @Test
    @DisplayName("GET /api/orders/user - 회원의 주문내역 조회")
    void getUserOrder() throws Exception {
        Page<OrderInfoResponse> responses = new PageImpl<>(List.of(orderInfoResponse));

        given(orderQueryService.findOrdersByUserId(userId, pageable)).willReturn(responses);

        mockMvc.perform(get("/api/orders/user").header("X-User-ID", userId).param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").exists())
                .andExpect(jsonPath("$.totalPages").exists());
    }

    @Test
    @DisplayName("GET /api/orders/info/{order-number} - 주문 조회 실패 시 not found 응답")
    void getOrderInfoFail() throws Exception {

        given(orderQueryService.getOrderInfo(anyString())).willThrow(new OrderNotFoundException());

        mockMvc.perform(get("/api/orders/info/{order-number}", orderNumber))
                .andExpect(status().isNotFound());
    }

    private Order createTestOrder() {
        Orderer orderer = new Orderer(1L, "홍길동", "010-1234-5678", "test@example.com");
        ShippingInfo shippingInfo = ShippingInfo.create(
                "홍길동", "010-1234-5678", "test@example.com",
                "서울시 강남구 테헤란로 123",
                LocalDateTime.now().plusDays(3)
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
