package shop.sajotuna.order.orders.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.sajotuna.order.orders.dto.OrderResponse;
import shop.sajotuna.order.orders.entity.Orders;
import shop.sajotuna.order.orders.dto.OrderRequest;
import shop.sajotuna.order.orders.dto.GuestOrderRequest;
import shop.sajotuna.order.orders.service.OrdersService;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrdersService orderService;

    // 주문 조회
    @GetMapping("/{orderId}")
    public Orders getOrders(@PathVariable String orderId){
        return orderService.getOrders(orderId);
    }

    // 회원의 주문내역들 조회
    @GetMapping("/user")
    public List<Orders> getOrdersByUserId(@RequestParam int userId) {
        return orderService.findOrdersByMemberId(userId);
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
