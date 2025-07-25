package shop.sajotuna.order.orders.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import shop.sajotuna.order.orders.controller.dto.response.PackageResponse;
import shop.sajotuna.order.orders.service.product.PackageService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PackageController.class)
@ActiveProfiles("test")
class PackageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PackageService packageService;

    @Test
    @DisplayName("포장 목록 조회 성공")
    void getPackage_Success() throws Exception {
        PackageResponse package1 = PackageResponse.builder()
                .id(1L)
                .packaging("기본 포장")
                .price(1000)
                .build();

        PackageResponse package2 = PackageResponse.builder()
                .id(2L)
                .packaging("선물 포장")
                .price(2000)
                .build();

        List<PackageResponse> packages = Arrays.asList(package1, package2);

        given(packageService.getPackages()).willReturn(packages);

        mockMvc.perform(get("/api/orders/package"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].packaging").value("기본 포장"))
                .andExpect(jsonPath("$[0].price").value(1000))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].packaging").value("선물 포장"))
                .andExpect(jsonPath("$[1].price").value(2000));

        verify(packageService).getPackages();
    }

    @Test
    @DisplayName("포장 목록이 비어있을 때")
    void getPackage_EmptyList() throws Exception {
        List<PackageResponse> emptyPackages = Arrays.asList();

        given(packageService.getPackages()).willReturn(emptyPackages);

        mockMvc.perform(get("/api/orders/package"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(packageService).getPackages();
    }

    @Test
    @DisplayName("포장 단일 조회 성공")
    void getPackageById_Success() throws Exception {
        Long packageId = 1L;
        PackageResponse packageResponse = PackageResponse.builder()
                .id(packageId)
                .packaging("기본 포장")
                .price(1000)
                .build();

        given(packageService.getPackage(packageId)).willReturn(packageResponse);

        mockMvc.perform(get("/api/orders/package/{package-id}", packageId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.packaging").value("기본 포장"))
                .andExpect(jsonPath("$.price").value(1000));

        verify(packageService).getPackage(packageId);
    }

    @Test
    @DisplayName("존재하지 않는 포장 ID로 조회")
    void getPackageById_NonExistentId() throws Exception {
        Long nonExistentId = 999L;

        given(packageService.getPackage(nonExistentId)).willReturn(null);

        mockMvc.perform(get("/api/orders/package/{package-id}", nonExistentId))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(packageService).getPackage(nonExistentId);
    }

    @Test
    @DisplayName("잘못된 포장 ID 형식으로 조회")
    void getPackageById_InvalidIdFormat() throws Exception {
        mockMvc.perform(get("/api/orders/package/invalid-id"))
                .andExpect(status().is5xxServerError());

        verify(packageService, never()).getPackage(anyLong());
    }

    @Test
    @DisplayName("0으로 포장 ID 조회")
    void getPackageById_ZeroId() throws Exception {
        Long packageId = 0L;
        PackageResponse packageResponse = PackageResponse.builder()
                .id(packageId)
                .packaging("0번 포장")
                .price(0)
                .build();

        given(packageService.getPackage(packageId)).willReturn(packageResponse);

        mockMvc.perform(get("/api/orders/package/{package-id}", packageId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(0))
                .andExpect(jsonPath("$.packaging").value("0번 포장"))
                .andExpect(jsonPath("$.price").value(0));

        verify(packageService).getPackage(packageId);
    }

    @Test
    @DisplayName("음수 포장 ID로 조회")
    void getPackageById_NegativeId() throws Exception {
        Long negativeId = -1L;

        given(packageService.getPackage(negativeId)).willReturn(null);

        mockMvc.perform(get("/api/orders/package/{package-id}", negativeId))
                .andExpect(status().isOk());

        verify(packageService).getPackage(negativeId);
    }

    @Test
    @DisplayName("매우 큰 포장 ID로 조회")
    void getPackageById_LargeId() throws Exception {
        Long largeId = Long.MAX_VALUE;
        PackageResponse packageResponse = PackageResponse.builder()
                .id(largeId)
                .packaging("큰 ID 포장")
                .price(5000)
                .build();

        given(packageService.getPackage(largeId)).willReturn(packageResponse);

        mockMvc.perform(get("/api/orders/package/{package-id}", largeId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(largeId))
                .andExpect(jsonPath("$.packaging").value("큰 ID 포장"))
                .andExpect(jsonPath("$.price").value(5000));

        verify(packageService).getPackage(largeId);
    }
}