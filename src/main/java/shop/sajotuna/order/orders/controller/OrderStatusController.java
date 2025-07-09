package shop.sajotuna.order.orders.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.sajotuna.order.orders.domain.ReturnReason;
import shop.sajotuna.order.orders.service.OrderStatusService;

@RestController
@RequestMapping("/api/orders/{order-id}")
@RequiredArgsConstructor
public class OrderStatusController {

    private final OrderStatusService orderStatusService;

    // 주문 반품 처리
    @PutMapping("/return")
    public ResponseEntity<Void> returnOrder(@PathVariable("order-id") Long orderId, @RequestHeader("X-User-Id") Long userId,
                                              @RequestParam("return-reason") ReturnReason returnReason) {
        orderStatusService.returnOrder(userId, orderId, returnReason);

        return ResponseEntity.noContent().build();
    }

    // 주문 취소 처리
    @PutMapping("/cancel")
    public ResponseEntity<Void> cancelOrder(@RequestHeader("X-User-Id") Long userId, @PathVariable("order-id") Long orderId){
        orderStatusService.cancelOrder(userId, orderId);

        return ResponseEntity.noContent().build();
    }
}
