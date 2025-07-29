package shop.sajotuna.order.coupon.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.coupon.domain.CouponType;
import shop.sajotuna.order.coupon.dto.request.BookInfo;
import shop.sajotuna.order.coupon.dto.response.CouponResponse;
import shop.sajotuna.order.coupon.service.UserCouponService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AvailableCouponController.class)
@ActiveProfiles("test")
class AvailableCouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserCouponService userCouponService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("사용 가능한 북 쿠폰 조회 성공")
    void getAvailableBookCoupons_success() throws Exception {
        // given
        Long userId = 1L;
        CouponResponse bookCoupon1 = CouponResponse.builder()
                .id(10L)
                .name("책 할인 쿠폰")
                .couponType(CouponType.BOOK)
                .discountAmount(2000)
                .build();

        CouponResponse bookCoupon2 = CouponResponse.builder()
                .id(11L)
                .name("베스트셀러 쿠폰")
                .couponType(CouponType.BOOK)
                .discountAmount(1500)
                .build();

        when(userCouponService.getAvailableCoupons(eq(userId), any(BookInfo.class)))
                .thenReturn(List.of(bookCoupon1, bookCoupon2));

        // when & then
        mockMvc.perform(get("/api/coupons/available-coupons/book")
                        .header("X-User-Id", userId)
                        .param("isbn", "1234567890")
                        .param("categoryIds", "10,20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(10L))
                .andExpect(jsonPath("$[0].name").value("책 할인 쿠폰"))
                .andExpect(jsonPath("$[0].couponType").value("BOOK"))
                .andExpect(jsonPath("$[0].discountAmount").value(2000))
                .andExpect(jsonPath("$[1].id").value(11L))
                .andExpect(jsonPath("$[1].name").value("베스트셀러 쿠폰"))
                .andExpect(jsonPath("$[1].couponType").value("BOOK"))
                .andExpect(jsonPath("$[1].discountAmount").value(1500));
    }

    @Test
    @DisplayName("사용 가능한 오더 쿠폰 조회 성공")
    void getAvailableOrderCoupons_success() throws Exception {
        // given
        Long userId = 1L;
        Integer totalPrice = 50000;
        
        CouponResponse orderCoupon1 = CouponResponse.builder()
                .id(20L)
                .name("주문 할인 쿠폰")
                .couponType(CouponType.ORDER)
                .discountAmount(5000)
                .build();

        CouponResponse orderCoupon2 = CouponResponse.builder()
                .id(21L)
                .name("대용량 주문 쿠폰")
                .couponType(CouponType.ORDER)
                .discountAmount(3000)
                .build();

        when(userCouponService.getAvailableOrderCoupons(eq(userId), eq(Money.of(totalPrice))))
                .thenReturn(List.of(orderCoupon1, orderCoupon2));

        // when & then
        mockMvc.perform(get("/api/coupons/available-coupons/order")
                        .header("X-User-Id", userId)
                        .param("totalPrice", String.valueOf(totalPrice)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(20L))
                .andExpect(jsonPath("$[0].name").value("주문 할인 쿠폰"))
                .andExpect(jsonPath("$[0].couponType").value("ORDER"))
                .andExpect(jsonPath("$[0].discountAmount").value(5000))
                .andExpect(jsonPath("$[1].id").value(21L))
                .andExpect(jsonPath("$[1].name").value("대용량 주문 쿠폰"))
                .andExpect(jsonPath("$[1].couponType").value("ORDER"))
                .andExpect(jsonPath("$[1].discountAmount").value(3000));
    }

    @Test
    @DisplayName("사용자 ID가 없으면 500 에러")
    void getAvailableBookCoupons_noUserId_internalServerError() throws Exception {
        // when & then
        mockMvc.perform(get("/api/coupons/available-coupons/book")
                        .param("isbn", "1234567890")
                        .param("categoryIds", "10,20"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("빈 쿠폰 목록 반환")
    void getAvailableBookCoupons_emptyCoupons() throws Exception {
        // given
        Long userId = 1L;
        when(userCouponService.getAvailableCoupons(eq(userId), any(BookInfo.class)))
                .thenReturn(List.of());

        // when & then
        mockMvc.perform(get("/api/coupons/available-coupons/book")
                        .header("X-User-Id", userId)
                        .param("isbn", "1234567890")
                        .param("categoryIds", "10,20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}