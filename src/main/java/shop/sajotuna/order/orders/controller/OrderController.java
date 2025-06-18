package shop.sajotuna.order.orders.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.sajotuna.order.orders.dto.*;
import shop.sajotuna.order.orders.entity.OrderStatus;
import shop.sajotuna.order.orders.service.OrderService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    // 주문 조회
    @GetMapping("/{order-id}")
    public ResponseEntity<OrderDetailResponse> getOrder(@PathVariable("order-id") Long orderId){
        return ResponseEntity.ok(orderService.findOrderDetail(orderId));
    }

    // 회원의 주문내역 조회
    @GetMapping("/user")
    public ResponseEntity<List<OrderResponse>> getUserOrder(@RequestHeader("X-User-Id") Long userId){
        return ResponseEntity.ok(orderService.findOrdersByUserId(userId));
    }

    // 대기 중인 주문들 조회 (관리자 전용)
    @GetMapping("/pending")
    public ResponseEntity<List<OrderResponse>> getPendingOrders(){
        return ResponseEntity.ok(orderService.findOrdersByStatus(OrderStatus.PENDING));
    }

    // 배송 중으로 전환 (관리자 전용)
    @PutMapping("/pending/{order-id}")
    public ResponseEntity<List<OrderResponse>> shippedOrder(@PathVariable("order-id") Long orderId){
        orderService.shippedOrder(orderId);

        return ResponseEntity.noContent().build();
    }

    // 회원 주문
    @PostMapping("/user")
    public ResponseEntity<OrderResponse> createUserOrders(@RequestHeader("X-User-Id") Long userId, @RequestBody @Valid OrderRequest request) {
        log.info("createUserOrders: userId = {}, request = {}", userId, request.getUsedUserCoupon());

        return ResponseEntity.ok(orderService.createUserOrder(request, userId));
    }

    // 비회원 주문
    @PostMapping("/guest")
    public ResponseEntity<OrderResponse> createGuestOrders(@RequestBody @Valid GuestOrderRequest request) {
        return ResponseEntity.ok(orderService.createGuestOrder(request));
    }

    // 주문 반품 처리
    @PutMapping("/returned/{order-id}")
    public ResponseEntity<Void> returnedOrder(@PathVariable("order-id") Long orderId, @RequestBody @Valid OrderReturnedRequest request){
        orderService.returnedOrder(request.getUserId(), orderId);

        return ResponseEntity.noContent().build();
    }

    // 주문 취소 처리
    @PutMapping("/cancelled/{order-id}")
    public ResponseEntity<Void> cancelledOrder(@PathVariable("order-id") Long orderId){
        orderService.cancelOrder(orderId);

        return ResponseEntity.noContent().build();
    }
}
