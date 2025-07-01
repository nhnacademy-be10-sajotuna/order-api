package shop.sajotuna.order.orders.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.sajotuna.order.orders.service.OrderStatusService;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class OrderStatusAdminController {

    private final OrderStatusService orderStatusService;

    // 배송 중으로 전환
    @PutMapping("/pending/{order-id}")
    public ResponseEntity<Void> shippedOrder(@PathVariable("order-id") Long orderId) {
        orderStatusService.shippedOrder(orderId);

        return ResponseEntity.noContent().build();
    }
}
