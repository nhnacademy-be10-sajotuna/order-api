package shop.sajotuna.order.point.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import shop.sajotuna.order.point.controller.response.GradePointPolicyResponse;
import shop.sajotuna.order.point.domain.Grade;
import shop.sajotuna.order.point.service.UserGradeService;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserGradeController.class)
@ActiveProfiles("test")
class UserGradeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserGradeService userGradeService;

    @Test
    @DisplayName("사용자 등급 조회 및 업데이트 성공")
    void getUserGradeAndUpdate_Success() throws Exception {
        Long userId = 1L;
        GradePointPolicyResponse response = new GradePointPolicyResponse(
                Grade.GENERAL,
                0,
                10000,
                5
        );

        given(userGradeService.getUserGrade(userId)).willReturn(response);

        mockMvc.perform(get("/api/grade/{user-id}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.grade").value("GENERAL"))
                .andExpect(jsonPath("$.minTotalOrderPrice").value(0))
                .andExpect(jsonPath("$.maxTotalOrderPrice").value(10000))
                .andExpect(jsonPath("$.pointRate").value(5));

        verify(userGradeService).getUserGrade(userId);
    }

    @Test
    @DisplayName("유효하지 않은 사용자 ID로 요청 시 적절한 응답")
    void getUserGradeAndUpdate_InvalidUserId() throws Exception {
        Long invalidUserId = -1L;

        mockMvc.perform(get("/api/grade/{user-id}", invalidUserId))
                .andExpect(status().isOk());

        verify(userGradeService).getUserGrade(invalidUserId);
    }

    @Test
    @DisplayName("존재하지 않는 사용자 ID로 요청")
    void getUserGradeAndUpdate_NonExistentUser() throws Exception {
        Long nonExistentUserId = 999L;
        GradePointPolicyResponse response = new GradePointPolicyResponse(
                Grade.GENERAL,
                0,
                10000,
                5
        );

        given(userGradeService.getUserGrade(nonExistentUserId)).willReturn(response);

        mockMvc.perform(get("/api/grade/{user-id}", nonExistentUserId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.grade").value("GENERAL"));

        verify(userGradeService).getUserGrade(nonExistentUserId);
    }
}
