package shop.sajotuna.order.coupon.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import shop.sajotuna.order.coupon.domain.CouponPolicyType;
import shop.sajotuna.order.coupon.dto.request.BookInfo;
import shop.sajotuna.order.coupon.dto.response.CouponResponse;
import shop.sajotuna.order.coupon.domain.CouponType;
import shop.sajotuna.order.coupon.service.UserCouponService;

import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AvailableCouponController.class)
class AvailableCouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserCouponService userCouponService;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @DisplayName("사용 가능한 북 쿠폰 조회 - 성공")
    void getAvailableBookCoupons_success() throws Exception {
        // given
        Long userId = 1L;
        String isbn = "9788998139766";
        Set<Long> categoryIds = Set.of(101L, 102L);

        List<CouponResponse> mockResponse = List.of(
                new CouponResponse(1L, "북쿠폰", CouponType.BOOK, CouponPolicyType.FIXED, 3000, 10000, 5000, 7)
        );

        when(userCouponService.getAvailableCoupons(userId, new BookInfo(isbn, categoryIds)))
                .thenReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/api/coupons/available-coupons/book")
                        .header("X-User-Id", userId)
                        .param("isbn", isbn)
                        .param("categoryIds", "101")
                        .param("categoryIds", "102"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].name").value("북쿠폰"));
    }

    // 왜 Coupon Controller는 되는데 다른거는 안되는지 모르겟어요
}
