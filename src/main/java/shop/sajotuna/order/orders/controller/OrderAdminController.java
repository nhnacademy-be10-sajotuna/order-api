package shop.sajotuna.order.orders.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.sajotuna.order.orders.dto.OrderResponse;
import shop.sajotuna.order.orders.dto.PackageRequest;
import shop.sajotuna.order.orders.dto.PackageResponse;
import shop.sajotuna.order.orders.entity.OrderStatus;
import shop.sajotuna.order.orders.exception.InvalidStatusException;
import shop.sajotuna.order.orders.service.OrderService;
import shop.sajotuna.order.orders.service.PackageService;

@Slf4j
@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class OrderAdminController {
    private final OrderService orderService;
    private final PackageService packageService;

    // 모든 주문 목록 조회
    @GetMapping
    public ResponseEntity<Page<OrderResponse>> getAllOrders(Pageable pageable) {
        return ResponseEntity.ok(orderService.findAllOrders(pageable));
    }

    // 배송 상태에 따라 주문 목록 조회
    @GetMapping("/{status}")
    public ResponseEntity<Page<OrderResponse>> getPendingOrders(@PathVariable String status, Pageable pageable) {
        try {
            OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());

            return ResponseEntity.ok(orderService.findOrdersByStatus(orderStatus, pageable));
        } catch (IllegalArgumentException e) {
            throw new InvalidStatusException();
        }
    }

    // 배송 중으로 전환
    @PutMapping("/pending/{order-id}")
    public ResponseEntity<Void> shippedOrder(@PathVariable("order-id") Long orderId){
        orderService.shippedOrder(orderId);

        return ResponseEntity.noContent().build();
    }

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
    public ResponseEntity<Void> deletePackage(@PathVariable("package-id") Long packageId){
        packageService.deletePackage(packageId);

        return ResponseEntity.noContent().build();
    }
}
