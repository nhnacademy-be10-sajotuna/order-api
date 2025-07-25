package shop.sajotuna.order.orders.service.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.orders.controller.dto.request.PackageRequest;
import shop.sajotuna.order.orders.controller.dto.response.PackageResponse;
import shop.sajotuna.order.orders.domain.OrderPackaging;
import shop.sajotuna.order.orders.exception.PackageNotFoundException;
import shop.sajotuna.order.orders.repository.OrderPackagingRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PackageServiceTest {

    @Mock
    private OrderPackagingRepository orderPackagingRepository;

    @InjectMocks
    private PackageService packageService;

    @Test
    @DisplayName("패키지 생성 성공")
    void createPackage_success() {
        // given
        PackageRequest request = mock(PackageRequest.class);
        when(request.getPackaging()).thenReturn("선물포장");
        when(request.getPrice()).thenReturn(2000);
        
        OrderPackaging savedPackaging = mock(OrderPackaging.class);
        when(orderPackagingRepository.save(any(OrderPackaging.class))).thenReturn(savedPackaging);
        when(savedPackaging.getPrice()).thenReturn(Money.of(2000));

        // when
        PackageResponse result = packageService.createPackage(request);

        // then
        assertThat(result).isNotNull();
        verify(orderPackagingRepository).save(any(OrderPackaging.class));
    }

    @Test
    @DisplayName("패키지 수정 성공")
    void updatePackage_success() {
        // given
        long packageId = 1L;
        PackageRequest request = mock(PackageRequest.class);
        when(request.getPackaging()).thenReturn("고급포장");
        when(request.getPrice()).thenReturn(3000);
        
        OrderPackaging existingPackaging = mock(OrderPackaging.class);
        when(orderPackagingRepository.findById(packageId)).thenReturn(Optional.of(existingPackaging));

        // when
        packageService.updatePackage(packageId, request);

        // then
        verify(orderPackagingRepository).findById(packageId);
        verify(existingPackaging).update("고급포장", Money.of(3000));
    }

    @Test
    @DisplayName("패키지 수정 실패 - 패키지를 찾을 수 없음")
    void updatePackage_packageNotFound() {
        // given
        long packageId = 999L;
        PackageRequest request = mock(PackageRequest.class);
        
        when(orderPackagingRepository.findById(packageId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> packageService.updatePackage(packageId, request))
                .isInstanceOf(PackageNotFoundException.class);
                
        verify(orderPackagingRepository).findById(packageId);
    }

    @Test
    @DisplayName("패키지 삭제 성공")
    void deletePackage_success() {
        // given
        long packageId = 1L;
        
        when(orderPackagingRepository.existsById(packageId)).thenReturn(true);

        // when
        packageService.deletePackage(packageId);

        // then
        verify(orderPackagingRepository).existsById(packageId);
        verify(orderPackagingRepository).deleteById(packageId);
    }

    @Test
    @DisplayName("패키지 삭제 실패 - 패키지를 찾을 수 없음")
    void deletePackage_packageNotFound() {
        // given
        long packageId = 999L;
        
        when(orderPackagingRepository.existsById(packageId)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> packageService.deletePackage(packageId))
                .isInstanceOf(PackageNotFoundException.class);
                
        verify(orderPackagingRepository).existsById(packageId);
        verify(orderPackagingRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("패키지 조회 성공")
    void getPackage_success() {
        // given
        long packageId = 1L;
        OrderPackaging orderPackaging = mock(OrderPackaging.class);
        
        when(orderPackagingRepository.findById(packageId)).thenReturn(Optional.of(orderPackaging));
        when(orderPackaging.getPrice()).thenReturn(Money.of(1000));

        // when
        PackageResponse result = packageService.getPackage(packageId);

        // then
        assertThat(result).isNotNull();
        verify(orderPackagingRepository).findById(packageId);
    }

    @Test
    @DisplayName("패키지 조회 실패 - 패키지를 찾을 수 없음")
    void getPackage_packageNotFound() {
        // given
        long packageId = 999L;
        
        when(orderPackagingRepository.findById(packageId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> packageService.getPackage(packageId))
                .isInstanceOf(PackageNotFoundException.class);
                
        verify(orderPackagingRepository).findById(packageId);
    }

    @Test
    @DisplayName("패키지 목록 조회 성공")
    void getPackages_success() {
        // given
        OrderPackaging package1 = mock(OrderPackaging.class);
        OrderPackaging package2 = mock(OrderPackaging.class);
        List<OrderPackaging> packages = List.of(package1, package2);
        
        when(orderPackagingRepository.findAll()).thenReturn(packages);
        when(package1.getPrice()).thenReturn(Money.of(1000));
        when(package2.getPrice()).thenReturn(Money.of(2000));

        // when
        List<PackageResponse> result = packageService.getPackages();

        // then
        assertThat(result).hasSize(2);
        verify(orderPackagingRepository).findAll();
    }

    @Test
    @DisplayName("패키지 목록 조회 성공 - 빈 목록")
    void getPackages_emptyList() {
        // given
        when(orderPackagingRepository.findAll()).thenReturn(List.of());

        // when
        List<PackageResponse> result = packageService.getPackages();

        // then
        assertThat(result).isEmpty();
        verify(orderPackagingRepository).findAll();
    }
}