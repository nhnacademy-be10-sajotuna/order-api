package shop.sajotuna.order.orders.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.sajotuna.order.orders.controller.dto.response.OrderDetailResponse;
import shop.sajotuna.order.orders.controller.dto.response.OrderResponse;
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
    public ResponseEntity<OrderDetailResponse> getOrder(@PathVariable("order-id") Long orderId) {
        return ResponseEntity.ok(orderService.findOrderDetail(orderId));
    }

    // 회원의 주문내역 조회
    @GetMapping("/user")
    public ResponseEntity<List<OrderResponse>> getUserOrder(@RequestHeader("X-User-Id") Long userId){
        return ResponseEntity.ok(orderService.findOrdersByUserId(userId));
    }

    // 주문 반품 처리
    @PutMapping("/returned/{order-id}")
    public ResponseEntity<Void> returnedOrder(@PathVariable("order-id") Long orderId, @RequestHeader("X-User-Id") Long userId){
        orderService.returnedOrder(userId, orderId);

        return ResponseEntity.noContent().build();
    }

    // 주문 취소 처리
    @PutMapping("/cancelled/{order-id}")
    public ResponseEntity<Void> cancelledOrder(@PathVariable("order-id") Long orderId){
        orderService.cancelOrder(orderId);

        return ResponseEntity.noContent().build();
    }
}
