package shop.sajotuna.order.orders.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.sajotuna.order.orders.controller.dto.response.OrderResponse;
import shop.sajotuna.order.orders.domain.OrderStatus;
import shop.sajotuna.order.orders.exception.InvalidStatusException;
import shop.sajotuna.order.orders.service.OrderQueryService;

@Slf4j
@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class OrderAdminController {
    private final OrderQueryService orderQueryService;

    // 모든 주문 목록 조회
    @GetMapping
    public ResponseEntity<Page<OrderResponse>> getAllOrders(Pageable pageable) {
        return ResponseEntity.ok(orderQueryService.findAllOrders(pageable));
    }

    // 배송 상태에 따라 주문 목록 조회
    @GetMapping("/{status}")
    public ResponseEntity<Page<OrderResponse>> getPendingOrders(@PathVariable String status, Pageable pageable) {
        try {
            OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());

            return ResponseEntity.ok(orderQueryService.findOrdersByStatus(orderStatus, pageable));
        } catch (IllegalArgumentException e) {
            throw new InvalidStatusException();
        }
    }

}
