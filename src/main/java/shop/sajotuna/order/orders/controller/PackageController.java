package shop.sajotuna.order.orders.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    public ResponseEntity<List<PackageResponse>> getPackage() {
        List<PackageResponse> packages = packageService.getPackages();
        return ResponseEntity.ok(packages);
    }
}
