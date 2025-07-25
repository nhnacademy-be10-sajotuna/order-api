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
import shop.sajotuna.order.orders.controller.dto.response.OrderFormResponse;
import shop.sajotuna.order.orders.controller.dto.response.OrderInfoResponse;
import shop.sajotuna.order.orders.domain.*;
import shop.sajotuna.order.orders.service.OrderFormService;
import shop.sajotuna.order.orders.service.OrderQueryService;
import shop.sajotuna.order.point.exception.OrderNotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
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
    OrderFormResponse orderFormResponse;
    String orderNumber;
    Long userId = 1L;
    Long orderId = 1L;
    Pageable pageable = PageRequest.of(0, 10);

    @BeforeEach
    public void setup() {
        Order fakeOrder = createTestOrder();

        orderNumber = fakeOrder.getOrderNumber();
        orderInfoResponse = OrderInfoResponse.from(fakeOrder);
        orderDetailResponse = OrderDetailResponse.from(fakeOrder, List.of(), null);
        orderFormResponse = OrderFormResponse.builder()
                .packages(List.of())
                .point(1000)
                .coupons(List.of())
                .deliveryPrice(null)
                .build();
    }

    @Test
    @DisplayName("GET /api/orders/detail/{order-id} - 주문 상세 조회")
    void getOrder() throws Exception {
        given(orderQueryService.findOrderDetail(orderId)).willReturn(orderDetailResponse);

        mockMvc.perform(get("/api/orders/detail/{order-id}", orderId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.orderNumber").value(orderNumber))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(orderQueryService).findOrderDetail(orderId);
    }

    @Test
    @DisplayName("GET /api/orders/detail/guest/{order-number} - 비회원 주문 상세 조회")
    void getGuestOrder() throws Exception {
        given(orderQueryService.findOrderDetailByOrderNumber(orderNumber)).willReturn(orderDetailResponse);

        mockMvc.perform(get("/api/orders/detail/guest/{order-number}", orderNumber))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.orderNumber").value(orderNumber))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(orderQueryService).findOrderDetailByOrderNumber(orderNumber);
    }

    @Test
    @DisplayName("GET /api/orders/form - 주문 폼 조회 (회원)")
    void getOrderForm_WithUserId() throws Exception {
        given(orderFormService.getOrderForm(userId)).willReturn(orderFormResponse);

        mockMvc.perform(get("/api/orders/form")
                        .header("X-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.point").value(1000))
                .andExpect(jsonPath("$.packages").isArray())
                .andExpect(jsonPath("$.coupons").isArray());

        verify(orderFormService).getOrderForm(userId);
    }

    @Test
    @DisplayName("GET /api/orders/form - 주문 폼 조회 (비회원)")
    void getOrderForm_WithoutUserId() throws Exception {
        given(orderFormService.getOrderForm(null)).willReturn(orderFormResponse);

        mockMvc.perform(get("/api/orders/form"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.point").value(1000))
                .andExpect(jsonPath("$.packages").isArray())
                .andExpect(jsonPath("$.coupons").isArray());

        verify(orderFormService).getOrderForm(null);
    }

    @Test
    @DisplayName("GET /api/orders/detail/{order-id} - 주문 상세 조회 실패")
    void getOrder_NotFound() throws Exception {
        given(orderQueryService.findOrderDetail(anyLong())).willThrow(new OrderNotFoundException());

        mockMvc.perform(get("/api/orders/detail/{order-id}", 999L))
                .andExpect(status().isNotFound());

        verify(orderQueryService).findOrderDetail(999L);
    }

    @Test
    @DisplayName("GET /api/orders/detail/guest/{order-number} - 비회원 주문 상세 조회 실패")
    void getGuestOrder_NotFound() throws Exception {
        given(orderQueryService.findOrderDetailByOrderNumber(anyString())).willThrow(new OrderNotFoundException());

        mockMvc.perform(get("/api/orders/detail/guest/{order-number}", "INVALID_ORDER_NUMBER"))
                .andExpect(status().isNotFound());

        verify(orderQueryService).findOrderDetailByOrderNumber("INVALID_ORDER_NUMBER");
    }

    @Test
    @DisplayName("GET /api/orders/user - 회원 주문내역 조회 (빈 결과)")
    void getUserOrder_EmptyResult() throws Exception {
        Page<OrderInfoResponse> emptyPage = new PageImpl<>(List.of());
        given(orderQueryService.findOrdersByUserId(userId, pageable)).willReturn(emptyPage);

        mockMvc.perform(get("/api/orders/user")
                        .header("X-User-Id", userId)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(0));

        verify(orderQueryService).findOrdersByUserId(userId, pageable);
    }

    @Test
    @DisplayName("GET /api/orders/detail/{order-id} - 잘못된 ID 형식")
    void getOrder_InvalidIdFormat() throws Exception {
        mockMvc.perform(get("/api/orders/detail/invalid-id"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("GET /api/orders/user - 다른 페이지 크기로 조회")
    void getUserOrder_DifferentPageSize() throws Exception {
        Pageable customPageable = PageRequest.of(1, 5);
        Page<OrderInfoResponse> responses = new PageImpl<>(List.of(orderInfoResponse), customPageable, 6);
        
        given(orderQueryService.findOrdersByUserId(userId, customPageable)).willReturn(responses);

        mockMvc.perform(get("/api/orders/user")
                        .header("X-User-Id", userId)
                        .param("page", "1")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(6))
                .andExpect(jsonPath("$.size").value(5));

        verify(orderQueryService).findOrdersByUserId(userId, customPageable);
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
