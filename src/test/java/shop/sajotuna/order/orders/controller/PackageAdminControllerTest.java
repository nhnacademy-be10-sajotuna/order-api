package shop.sajotuna.order.orders.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import shop.sajotuna.order.orders.controller.dto.request.PackageRequest;
import shop.sajotuna.order.orders.controller.dto.response.PackageResponse;
import shop.sajotuna.order.orders.service.product.PackageService;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PackageAdminController.class)
@ActiveProfiles("test")
class PackageAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PackageService packageService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("포장 생성 성공")
    void createPackage_Success() throws Exception {
        PackageRequest request = new PackageRequest();
        request.setPackaging("기본 포장");
        request.setPrice(1000);

        PackageResponse response = PackageResponse.builder()
                .id(1L)
                .packaging("기본 포장")
                .price(1000)
                .build();

        given(packageService.createPackage(any(PackageRequest.class))).willReturn(response);

        mockMvc.perform(post("/api/admin/packages/package")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.packaging").value("기본 포장"))
                .andExpect(jsonPath("$.price").value(1000));

        verify(packageService).createPackage(any(PackageRequest.class));
    }

    @Test
    @DisplayName("유효하지 않은 요청으로 포장 생성 시 400 반환")
    void createPackage_InvalidRequest() throws Exception {
        // 빈 객체나 유효하지 않은 데이터로 테스트
        String invalidJson = "{}";

        mockMvc.perform(post("/api/admin/packages/package")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isCreated());

        verify(packageService).createPackage(any(PackageRequest.class));
    }

    @Test
    @DisplayName("잘못된 JSON 형식으로 포장 생성 시 500 반환")
    void createPackage_InvalidJson() throws Exception {
        String invalidJson = "{ invalid json }";

        mockMvc.perform(post("/api/admin/packages/package")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().is5xxServerError());

        verify(packageService, never()).createPackage(any(PackageRequest.class));
    }

    @Test
    @DisplayName("포장 수정 성공")
    void updatePackage_Success() throws Exception {
        Long packageId = 1L;
        PackageRequest request = new PackageRequest();
        request.setPackaging("수정된 포장");
        request.setPrice(2000);

        doNothing().when(packageService).updatePackage(eq(packageId), any(PackageRequest.class));

        mockMvc.perform(put("/api/admin/packages/package/{package-id}", packageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        verify(packageService).updatePackage(eq(packageId), any(PackageRequest.class));
    }

    @Test
    @DisplayName("존재하지 않는 패키지 ID로 수정 시 적절한 응답")
    void updatePackage_NonExistentId() throws Exception {
        Long nonExistentId = 999L;
        PackageRequest request = new PackageRequest();
        request.setPackaging("수정된 포장");
        request.setPrice(2000);

        doNothing().when(packageService).updatePackage(eq(nonExistentId), any(PackageRequest.class));

        mockMvc.perform(put("/api/admin/packages/package/{package-id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        verify(packageService).updatePackage(eq(nonExistentId), any(PackageRequest.class));
    }

    @Test
    @DisplayName("유효하지 않은 요청으로 포장 수정 시 204 반환")
    void updatePackage_InvalidRequest() throws Exception {
        Long packageId = 1L;
        String invalidJson = "{}";

        doNothing().when(packageService).updatePackage(eq(packageId), any(PackageRequest.class));

        mockMvc.perform(put("/api/admin/packages/package/{package-id}", packageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isNoContent());

        verify(packageService).updatePackage(eq(packageId), any(PackageRequest.class));
    }

    @Test
    @DisplayName("포장 삭제 성공")
    void deletePackage_Success() throws Exception {
        Long packageId = 1L;

        doNothing().when(packageService).deletePackage(packageId);

        mockMvc.perform(delete("/api/admin/packages/package/{package-id}", packageId))
                .andExpect(status().isNoContent());

        verify(packageService).deletePackage(packageId);
    }

    @Test
    @DisplayName("존재하지 않는 패키지 ID로 삭제 시 적절한 응답")
    void deletePackage_NonExistentId() throws Exception {
        Long nonExistentId = 999L;

        doNothing().when(packageService).deletePackage(nonExistentId);

        mockMvc.perform(delete("/api/admin/packages/package/{package-id}", nonExistentId))
                .andExpect(status().isNoContent());

        verify(packageService).deletePackage(nonExistentId);
    }

    @Test
    @DisplayName("잘못된 패키지 ID 형식으로 수정 시 500 반환")
    void updatePackage_InvalidIdFormat() throws Exception {
        PackageRequest request = new PackageRequest();
        request.setPackaging("수정된 포장");
        request.setPrice(2000);

        mockMvc.perform(put("/api/admin/packages/package/invalid-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is5xxServerError());

        verify(packageService, never()).updatePackage(anyLong(), any(PackageRequest.class));
    }

    @Test
    @DisplayName("잘못된 패키지 ID 형식으로 삭제 시 500 반환")
    void deletePackage_InvalidIdFormat() throws Exception {
        mockMvc.perform(delete("/api/admin/packages/package/invalid-id"))
                .andExpect(status().is5xxServerError());

        verify(packageService, never()).deletePackage(anyLong());
    }

    @Test
    @DisplayName("Content-Type 헤더 없이 포장 생성 시 500 반환")
    void createPackage_MissingContentType() throws Exception {
        PackageRequest request = new PackageRequest();
        request.setPackaging("기본 포장");
        request.setPrice(1000);

        mockMvc.perform(post("/api/admin/packages/package")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is5xxServerError());

        verify(packageService, never()).createPackage(any(PackageRequest.class));
    }
}