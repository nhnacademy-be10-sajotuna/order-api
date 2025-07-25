package shop.sajotuna.order.point.controller;

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
import shop.sajotuna.order.point.controller.response.PointHistoryResponse;
import shop.sajotuna.order.point.domain.PointHistoryType;
import shop.sajotuna.order.point.service.PointService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PointController.class)
@ActiveProfiles("test")
class PointControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PointService pointService;

    @Test
    @DisplayName("사용자 포인트 히스토리 페이징 조회 성공")
    void getPointsByUserIdWithPaging_Success() throws Exception {
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        
        List<PointHistoryResponse> histories = List.of(
                PointHistoryResponse.builder()
                        .id(1L)
                        .userId(userId)
                        .amount(100)
                        .type(PointHistoryType.EARNED)
                        .description("리뷰 작성")
                        .createdAt(LocalDateTime.now())
                        .build(),
                PointHistoryResponse.builder()
                        .id(2L)
                        .userId(userId)
                        .amount(50)
                        .type(PointHistoryType.EARNED)
                        .description("구매 적립")
                        .createdAt(LocalDateTime.now())
                        .build()
        );
        Page<PointHistoryResponse> pageResponse = new PageImpl<>(histories, pageable, 2);

        given(pointService.getPointsByUserId(userId, pageable)).willReturn(pageResponse);

        mockMvc.perform(get("/api/points")
                        .header("X-User-Id", userId)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].description").value("리뷰 작성"))
                .andExpect(jsonPath("$.content[0].amount").value(100))
                .andExpect(jsonPath("$.content[0].type").value("EARNED"))
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.content[1].description").value("구매 적립"))
                .andExpect(jsonPath("$.content[1].amount").value(50))
                .andExpect(jsonPath("$.content[1].type").value("EARNED"))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.number").value(0));

        verify(pointService).getPointsByUserId(userId, pageable);
    }

    @Test
    @DisplayName("사용자 ID 헤더 없이 요청 시 400 반환")
    void getPointsByUserIdWithPaging_MissingUserIdHeader() throws Exception {
        mockMvc.perform(get("/api/points")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("사용 가능한 포인트 조회 성공")
    void getAvailablePoint_Success() throws Exception {
        Long userId = 1L;
        Integer availablePoint = 500;

        given(pointService.getAvailablePointByUserId(userId)).willReturn(availablePoint);

        mockMvc.perform(get("/api/points/available")
                        .header("X-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("500"));

        verify(pointService).getAvailablePointByUserId(userId);
    }

    @Test
    @DisplayName("사용자 ID 헤더 없이 사용 가능한 포인트 조회 시 400 반환")
    void getAvailablePoint_MissingUserIdHeader() throws Exception {
        mockMvc.perform(get("/api/points/available"))
                .andExpect(status().is5xxServerError());
    }
}