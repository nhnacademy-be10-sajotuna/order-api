package shop.sajotuna.order.orders.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import shop.sajotuna.order.orders.domain.Orders;
import shop.sajotuna.order.orders.domain.OrdersRequest;
import shop.sajotuna.order.orders.service.OrdersService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrdersService orderService;

    // 주문 조회
    @GetMapping("/api/orders/{orderId}")
    public Orders getOrders(@PathVariable String orderId){
        return orderService.getOrders(orderId);
    }

    // 회원의 주문내역들 조회
    @GetMapping("/api/orders")
    public List<Orders> getOrdersByMemberId(@RequestParam String memberId) {
        return orderService.findOrdersByMemberId(Integer.parseInt(memberId));
    }

    // 회원 주문
    @PostMapping("/api/orders/member")
    public Orders createMemberOrders(@RequestBody OrdersRequest request, @RequestParam int memberId) {
        Orders orders = orderService.createOrders(request);

        orderService.createMemberOrders(memberId, orders.getId());

        return orders;
    }

    // 비회원주문
    @PostMapping("/api/orders/quest")
    public Orders createQuestOrders(@RequestBody OrdersRequest request) {
        Orders orders = orderService.createOrders(request);

        return orders;
    }
}
