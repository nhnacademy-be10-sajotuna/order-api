package shop.sajotuna.order.orders.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.sajotuna.order.orders.domain.Orders;
import shop.sajotuna.order.orders.repository.OrdersRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class OrdersService {
    private final OrdersRepository ordersRepository;

    // 한 회원의 주문내역들 조회
    public List<Orders> findOrdersByMemberId(String memberId) {
        return ordersRepository.findOrdersByMemberId(memberId);
    }


}
