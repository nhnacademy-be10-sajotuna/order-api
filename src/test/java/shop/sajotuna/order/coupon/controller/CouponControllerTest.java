package shop.sajotuna.order.coupon.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import shop.sajotuna.order.coupon.domain.CouponType;
import shop.sajotuna.order.coupon.dto.request.BookInfo;
import shop.sajotuna.order.coupon.dto.response.CouponResponse;
import shop.sajotuna.order.coupon.service.CouponService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(CouponController.class)
@ActiveProfiles("test")
class CouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CouponService couponService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("쿠폰 ID로 단일 쿠폰 조회 성공")
    void getCouponById_success() throws Exception {
        // given
        Long couponId = 1L;
        CouponResponse mockResponse = CouponResponse.builder()
                .id(couponId)
                .name("할인 쿠폰")
                .couponType(CouponType.ORDER)
                .discountAmount(1000)
                .build();

        when(couponService.findCoupon(couponId)).thenReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/api/coupons/{coupon-id}", couponId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(couponId))
                .andExpect(jsonPath("$.name").value("할인 쿠폰"))
                .andExpect(jsonPath("$.couponType").value("ORDER"))
                .andExpect(jsonPath("$.discountAmount").value(1000));
    }

    @Test
    @DisplayName("책 쿠폰 목록 조회 성공")
    void getBookCoupons_success() throws Exception {
        // given
        CouponResponse bookCoupon1 = CouponResponse.builder()
                .id(10L)
                .name("책 쿠폰 1")
                .couponType(CouponType.BOOK)
                .discountAmount(2000)
                .build();

        CouponResponse bookCoupon2 = CouponResponse.builder()
                .id(11L)
                .name("책 쿠폰 2")
                .couponType(CouponType.BOOK)
                .discountAmount(1500)
                .build();

        when(couponService.findBookCoupons(any(BookInfo.class)))
                .thenReturn(List.of(bookCoupon1, bookCoupon2));

        // when & then
        mockMvc.perform(get("/api/coupons/book-coupons")
                        .param("isbn", "1234567890")
                        .param("categoryIds", "10,20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(10L))
                .andExpect(jsonPath("$[1].id").value(11L));
    }
}
