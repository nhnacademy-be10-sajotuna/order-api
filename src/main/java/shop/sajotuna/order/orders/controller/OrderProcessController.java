package shop.sajotuna.order.orders.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.sajotuna.order.orders.dto.CreateOrderRequest;
import shop.sajotuna.order.orders.dto.OrderResponse;
import shop.sajotuna.order.orders.service.OrderProcessService;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderProcessController {
    private final OrderProcessService orderProcessService;

    // 회원 주문
    @PostMapping("/user")
    public ResponseEntity<OrderResponse> createUserOrders(@RequestHeader("X-User-Id") Long userId, @RequestBody @Valid CreateOrderRequest request) {
        log.info("createUserOrders: userId = {}, request = {}", userId, request.getOrderCouponId());

        OrderResponse orderResponse = orderProcessService.processUserOrder(request.toCommand(userId));
        return ResponseEntity.ok(orderResponse);
    }

    // TODO: 비회원 주문
}
