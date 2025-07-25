package shop.sajotuna.order.point.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import shop.sajotuna.order.point.controller.request.PointPolicyUpdateRequest;
import shop.sajotuna.order.point.controller.response.PointPolicyResponse;
import shop.sajotuna.order.point.domain.CalculationMode;
import shop.sajotuna.order.point.domain.PointPolicyType;
import shop.sajotuna.order.point.exception.PointPolicyNotFoundException;
import shop.sajotuna.order.point.service.PointPolicyService;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminPointPolicyController.class)
@ActiveProfiles("test")
class AdminPointPolicyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PointPolicyService pointPolicyService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("모든 포인트 정책 조회 성공")
    void getAllPointPolicies_Success() throws Exception {
        List<PointPolicyResponse> policies = List.of(
                PointPolicyResponse.builder()
                        .id(1L)
                        .type(PointPolicyType.REVIEW)
                        .calculationMode(CalculationMode.FIXED)
                        .value(100)
                        .build(),
                PointPolicyResponse.builder()
                        .id(2L)
                        .type(PointPolicyType.PURCHASE)
                        .calculationMode(CalculationMode.RATE)
                        .value(1000)
                        .build()
        );

        given(pointPolicyService.getAllPointPolicies()).willReturn(policies);

        mockMvc.perform(get("/api/admin/point-policies"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].type").value("REVIEW"))
                .andExpect(jsonPath("$[0].calculationMode").value("FIXED"))
                .andExpect(jsonPath("$[0].value").value(100))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].type").value("PURCHASE"))
                .andExpect(jsonPath("$[1].calculationMode").value("RATE"))
                .andExpect(jsonPath("$[1].value").value(1000));

        verify(pointPolicyService).getAllPointPolicies();
    }

    @Test
    @DisplayName("포인트 정책 수정 성공")
    void updatePointPolicy_Success() throws Exception {
        Long policyId = 1L;
        PointPolicyUpdateRequest request = new PointPolicyUpdateRequest(200);

        mockMvc.perform(put("/api/admin/point-policies/{policy-id}", policyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        verify(pointPolicyService).updatePointPolicy(eq(policyId), any(PointPolicyUpdateRequest.class));
    }

    @Test
    @DisplayName("존재하지 않는 포인트 정책 수정 시 404 반환")
    void updatePointPolicy_NotFound() throws Exception {
        Long policyId = 999L;
        PointPolicyUpdateRequest request = new PointPolicyUpdateRequest(200);

        doThrow(new PointPolicyNotFoundException()).when(pointPolicyService)
                .updatePointPolicy(eq(policyId), any(PointPolicyUpdateRequest.class));

        mockMvc.perform(put("/api/admin/point-policies/{policy-id}", policyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("잘못된 요청 데이터로 포인트 정책 수정 시 400 반환")
    void updatePointPolicy_InvalidRequest() throws Exception {
        Long policyId = 1L;
        PointPolicyUpdateRequest request = new PointPolicyUpdateRequest(0);

        mockMvc.perform(put("/api/admin/point-policies/{policy-id}", policyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}