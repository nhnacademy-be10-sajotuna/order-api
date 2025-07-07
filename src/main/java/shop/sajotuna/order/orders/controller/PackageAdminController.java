package shop.sajotuna.order.orders.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.sajotuna.order.orders.controller.dto.request.PackageRequest;
import shop.sajotuna.order.orders.controller.dto.response.PackageResponse;
import shop.sajotuna.order.orders.service.product.PackageService;

@RestController
@RequestMapping("/api/admin/packages")
@RequiredArgsConstructor
public class PackageAdminController {

    private final PackageService packageService;

    // 포장 생성
    @PostMapping("/package")
    public ResponseEntity<PackageResponse> createPackage(@RequestBody @Valid PackageRequest request) {
        PackageResponse packageResponse = packageService.createPackage(request);

        return new ResponseEntity<>(packageResponse, HttpStatus.CREATED);
    }

    // 포장 수정
    @PutMapping("/package/{package-id}")
    public ResponseEntity<Void> updatePackage(@PathVariable("package-id") Long packageId, @RequestBody @Valid PackageRequest request) {
        packageService.updatePackage(packageId, request);

        return ResponseEntity.noContent().build();
    }

    // 포장 삭제
    @DeleteMapping("/package/{package-id}")
    public ResponseEntity<Void> deletePackage(@PathVariable("package-id") Long packageId) {
        packageService.deletePackage(packageId);

        return ResponseEntity.noContent().build();
    }
}
