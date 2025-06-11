package shop.sajotuna.order.orders.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.sajotuna.order.orders.dto.OrderRequest;
import shop.sajotuna.order.orders.dto.GuestOrderRequest;
import shop.sajotuna.order.orders.service.OrderService;

@RestController
@RequestMapping("/order-api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 주문 조회
    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrder(@PathVariable Long orderId){
        return null;
    }

    // 회원의 주문내역 조회
    @GetMapping("/user")
    public ResponseEntity<?> getUserOrder(@RequestParam Long userId){
        return null;
    }

    // 회원 주문
    @PostMapping("/user")
    public ResponseEntity<?> createUserOrders(@RequestBody OrderRequest request) {
        return null;
    }

    // 비회원 주문
    @PostMapping("/guest")
    public ResponseEntity<?> createGuestOrders(@RequestBody GuestOrderRequest request) {

        return null;
    }
}
