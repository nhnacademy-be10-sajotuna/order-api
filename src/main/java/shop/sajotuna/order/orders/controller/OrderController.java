package shop.sajotuna.order.orders.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.sajotuna.order.orders.controller.dto.response.OrderDetailResponse;
import shop.sajotuna.order.orders.controller.dto.response.OrderInfoResponse;
import shop.sajotuna.order.orders.service.OrderQueryService;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderQueryService orderQueryService;

    @GetMapping("/info/{order-number}")
    public ResponseEntity<OrderInfoResponse> getOrderInfo(@PathVariable("order-number") String orderNumber){
        return ResponseEntity.ok(orderQueryService.getOrderInfo(orderNumber));
    }

    // 주문 조회
    @GetMapping("/detail/{order-id}")
    public ResponseEntity<OrderDetailResponse> getOrder(@PathVariable("order-id") Long orderId) {
        return ResponseEntity.ok(orderQueryService.findOrderDetail(orderId));
    }

    // 비회원 주문 조회
    @GetMapping("/detail/guest/{order-number}")
    public ResponseEntity<OrderDetailResponse> getGuestOrder(@PathVariable("order-number") String orderNumber) {
        return ResponseEntity.ok(orderQueryService.findOrderDetailByOrderNumber(orderNumber));
    }

    // 회원의 주문내역 조회
    @GetMapping("/user")
    public ResponseEntity<Page<OrderInfoResponse>> getUserOrder(@RequestHeader("X-User-Id") Long userId, Pageable pageable){
        return ResponseEntity.ok(orderQueryService.findOrdersByUserId(userId, pageable));
    }
}
