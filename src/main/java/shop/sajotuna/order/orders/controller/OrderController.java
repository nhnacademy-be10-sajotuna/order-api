package shop.sajotuna.order.orders.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import shop.sajotuna.order.orders.domain.Orders;
import shop.sajotuna.order.orders.service.OrdersService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrdersService ordersService;

    @GetMapping("/api/orders/{memberId}")
    public List<Orders> getOrdersByMemberId(@PathVariable String memberId) {
        return ordersService.findOrdersByMemberId(memberId);
    }
}
