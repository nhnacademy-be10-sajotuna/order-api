package shop.sajotuna.order.orders.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import shop.sajotuna.order.orders.controller.dto.response.OrderProductResponse;
import shop.sajotuna.order.orders.service.product.OrderProductService;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderProductController.class)
@ActiveProfiles("test")
class OrderProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderProductService productService;

    @Test
    @DisplayName("주문 상품 조회 성공")
    void getOrderProduct_Success() throws Exception {
        Long orderProductId = 1L;
        OrderProductResponse mockResponse = OrderProductResponse.builder()
                .id(orderProductId)
                .isbn("9781234567890")
                .qty(2)
                .amount(15000)
                .packagingRequest(false)
                .build();

        given(productService.findById(orderProductId)).willReturn(mockResponse);

        mockMvc.perform(get("/api/orders/product/{orderProductId}", orderProductId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(orderProductId))
                .andExpect(jsonPath("$.isbn").value("9781234567890"))
                .andExpect(jsonPath("$.qty").value(2))
                .andExpect(jsonPath("$.amount").value(15000))
                .andExpect(jsonPath("$.packagingRequest").value(false));

        verify(productService).findById(orderProductId);
    }

    @Test
    @DisplayName("주문 번호에 포함된 상품들 조회 성공")
    void getOrderProducts_Success() throws Exception {
        Long orderId = 1L;
        List<OrderProductResponse> mockResponses = List.of(
                OrderProductResponse.builder()
                        .id(1L)
                        .isbn("9781234567890")
                        .qty(1)
                        .amount(10000)
                        .packagingRequest(false)
                        .build(),
                OrderProductResponse.builder()
                        .id(2L)
                        .isbn("9789876543210")
                        .qty(2)
                        .amount(20000)
                        .packagingRequest(true)
                        .build()
        );

        given(productService.findByOrderId(orderId)).willReturn(mockResponses);

        mockMvc.perform(get("/api/orders/product/list/{orderId}", orderId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].isbn").value("9781234567890"))
                .andExpect(jsonPath("$[0].qty").value(1))
                .andExpect(jsonPath("$[0].amount").value(10000))
                .andExpect(jsonPath("$[0].packagingRequest").value(false))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].isbn").value("9789876543210"))
                .andExpect(jsonPath("$[1].qty").value(2))
                .andExpect(jsonPath("$[1].amount").value(20000))
                .andExpect(jsonPath("$[1].packagingRequest").value(true));

        verify(productService).findByOrderId(orderId);
    }

    @Test
    @DisplayName("리뷰 작성 가능 여부 확인 - 가능한 경우")
    void isEligibleForReview_Eligible() throws Exception {
        Long userId = 1L;
        String isbn = "9781234567890";

        given(productService.isEligibleForReview(userId, isbn)).willReturn(true);

        mockMvc.perform(get("/api/orders/product/review-eligible/{userId}/{isbn}", userId, isbn))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("true"));

        verify(productService).isEligibleForReview(userId, isbn);
    }

    @Test
    @DisplayName("리뷰 작성 가능 여부 확인 - 불가능한 경우")
    void isEligibleForReview_NotEligible() throws Exception {
        Long userId = 1L;
        String isbn = "9781234567890";

        given(productService.isEligibleForReview(userId, isbn)).willReturn(false);

        mockMvc.perform(get("/api/orders/product/review-eligible/{userId}/{isbn}", userId, isbn))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("false"));

        verify(productService).isEligibleForReview(userId, isbn);
    }

    @Test
    @DisplayName("잘못된 orderProductId로 조회 시 적절한 응답")
    void getOrderProduct_InvalidId() throws Exception {
        Long invalidId = 999L;

        given(productService.findById(invalidId)).willReturn(null);

        mockMvc.perform(get("/api/orders/product/{orderProductId}", invalidId))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(productService).findById(invalidId);
    }

    @Test
    @DisplayName("존재하지 않는 주문 번호로 상품 조회 시 빈 목록 반환")
    void getOrderProducts_NonExistentOrderId() throws Exception {
        Long nonExistentOrderId = 999L;

        given(productService.findByOrderId(nonExistentOrderId)).willReturn(List.of());

        mockMvc.perform(get("/api/orders/product/list/{orderId}", nonExistentOrderId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));

        verify(productService).findByOrderId(nonExistentOrderId);
    }
}