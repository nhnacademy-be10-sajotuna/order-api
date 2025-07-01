package shop.sajotuna.order.orders.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.sajotuna.order.orders.service.OrderStatusService;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderStatusController {

    private final OrderStatusService orderStatusService;

    // 주문 반품 처리
    @PutMapping("/returned/{order-id}")
    public ResponseEntity<Void> returnedOrder(@PathVariable("order-id") Long orderId, @RequestHeader("X-User-Id") Long userId){
        orderStatusService.returnedOrder(userId, orderId);

        return ResponseEntity.noContent().build();
    }

    // 주문 취소 처리
    @PutMapping("/cancelled/{order-id}")
    public ResponseEntity<Void> cancelledOrder(@PathVariable("order-id") Long orderId){
        orderStatusService.cancelOrder(orderId);

        return ResponseEntity.noContent().build();
    }
}
