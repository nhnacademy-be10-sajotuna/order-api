package shop.sajotuna.order.orders.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.sajotuna.order.orders.dto.PackageRequest;
import shop.sajotuna.order.orders.dto.PackageResponse;
import shop.sajotuna.order.orders.service.PackageService;

import java.util.List;

@RestController
@RequestMapping("/api/orders/package")
@RequiredArgsConstructor
public class PackageController {
    private final PackageService packageService;

    // 포장 목록 조회
    @GetMapping
    public ResponseEntity<List<PackageResponse>> getPackage(){
        List<PackageResponse> packages = packageService.getPackages();
        return ResponseEntity.ok(packages);
    }

    // 포장 생성 (관리자 전용)
    @PostMapping
    public ResponseEntity<PackageResponse> createPackage(@RequestBody @Valid PackageRequest request) {
        PackageResponse packageResponse = packageService.createPackage(request);

        return ResponseEntity.ok(packageResponse);
    }

    // 포장 수정 (관리자 전용)
    @PutMapping("/{package-id}")
    public ResponseEntity<Void> updatePackage(@PathVariable("package-id") Long packageId, @RequestBody @Valid PackageRequest request) {
        packageService.updatePackage(packageId, request);

        return ResponseEntity.noContent().build();
    }

    // 포장 삭제 (관리자 전용)
    @DeleteMapping("/{package-id}")
    public ResponseEntity<Void> deletePackage(@PathVariable("package-id") Long packageId){
        packageService.deletePackage(packageId);

        return ResponseEntity.noContent().build();
    }
}
