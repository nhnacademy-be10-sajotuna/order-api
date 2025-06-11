package shop.sajotuna.order.orders.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.sajotuna.order.orders.dto.PackageRequest;
import shop.sajotuna.order.orders.dto.PackageResponse;
import shop.sajotuna.order.orders.service.PackageService;

import java.util.List;

@RestController
@RequestMapping("/order-api/package")
@RequiredArgsConstructor
public class PackageController {
    private final PackageService packageService;

    // 포장 목록 조회
    @GetMapping
    public ResponseEntity<List<PackageResponse>> getPackage(){
        List<PackageResponse> packages = packageService.getPackages();
        return new ResponseEntity<>(packages, HttpStatus.OK);
    }

    // 포장 생성
    @PostMapping
    public ResponseEntity<PackageResponse> createPackage(@RequestBody PackageRequest request) {
        PackageResponse packageResponse = packageService.createPackage(request);

        return new ResponseEntity<>(packageResponse, HttpStatus.CREATED);
    }

    // 포장 수정
    @PutMapping("/{packageId}")
    public ResponseEntity<String> updatePackage(@PathVariable Long packageId, @RequestBody PackageRequest request) {
        packageService.updatePackage(packageId, request);

        return new ResponseEntity<>("정상적으로 동작되었습니다", HttpStatus.OK);
    }

    // 포장 삭제
    @DeleteMapping("/{packageId}")
    public ResponseEntity<String> deletePackage(@PathVariable Long packageId){
        packageService.deletePackage(packageId);

        return new ResponseEntity<>("정상적으로 동작되었습니다", HttpStatus.OK);
    }
}
