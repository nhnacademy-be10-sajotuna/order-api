package shop.sajotuna.order.orders.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.sajotuna.order.orders.dto.OrderRequest;
import shop.sajotuna.order.orders.dto.GuestOrderRequest;
import shop.sajotuna.order.orders.dto.OrderResponse;
import shop.sajotuna.order.orders.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 주문 조회
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long orderId){
        return ResponseEntity.ok(orderService.findOrder(orderId));
    }

    // 회원의 주문내역 조회
    @GetMapping("/user")
    public ResponseEntity<List<OrderResponse>> getUserOrder(@RequestParam Long userId){
        return ResponseEntity.ok(orderService.findOrdersByUserId(userId));
    }

    // 회원 주문
    @PostMapping("/user")
    public ResponseEntity<OrderResponse> createUserOrders(@RequestBody OrderRequest request) {
        return ResponseEntity.ok(orderService.createUserOrder(request));
    }

    // 비회원 주문
    @PostMapping("/guest")
    public ResponseEntity<OrderResponse> createGuestOrders(@RequestBody GuestOrderRequest request) {
        return ResponseEntity.ok(orderService.createGuestOrder(request));
    }
}
