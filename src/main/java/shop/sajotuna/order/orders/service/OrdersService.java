package shop.sajotuna.order.orders.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.sajotuna.order.orders.domain.MemberOrder;
import shop.sajotuna.order.orders.domain.Orders;
import shop.sajotuna.order.orders.domain.OrdersRequest;
import shop.sajotuna.order.orders.exception.NotFoundException;
import shop.sajotuna.order.orders.repository.MemberOrderRepository;
import shop.sajotuna.order.orders.repository.OrderProductRepository;
import shop.sajotuna.order.orders.repository.OrdersRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor
@Service
public class OrdersService {
    private final OrdersRepository ordersRepository;
    private final OrderProductRepository orderProductRepository;
    private final MemberOrderRepository memberOrderRepository;

    // 주문내역 존재 확인
    public boolean isExistOrders(String orderId){
        return ordersRepository.existsById(orderId);
    }

    // 주문상품 존재 확인
    public boolean isExistOrderProduct(int orderProductId){
        return orderProductRepository.existsById(orderProductId);
    }

    // 주문 조회
    public Orders getOrders(String orderId){
        return ordersRepository.findById(orderId).orElse(null);
    }

    // 회원의 주문내역들 조회
    public List<Orders> findOrdersByMemberId(int memberId) {
        return ordersRepository.findOrdersByMemberId(memberId);
    }

    // 주문 생성
    public Orders createOrders(OrdersRequest ordersRequest){
        Orders orders = new Orders(ordersRequest, getRandomId());

        return ordersRepository.saveAndFlush(orders);
    }

    // 회원주문 생성
    public void createMemberOrders(int memberId, String orderId) {
        if(!isExistOrders(orderId)) {
            throw new NotFoundException("orders not found");
        }
        Orders orders = ordersRepository.getReferenceById(orderId);
        MemberOrder memberOrder = new MemberOrder(orders, memberId);

        memberOrderRepository.saveAndFlush(memberOrder);
    }

    // 비회원주문 정보 조회


    // 비회원주문 생성


    // 포장 목록 조회

    // 포장 수정

    // 주문내역 생성

    // 주문번호의 상품들 조회

    // 주문상품 상태 변경

    // 교환 및 반품 생성

    // Orders 아이디 생성
    private String getRandomId(){
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String today = now.format(formatter);
        String formatted = String.format("%08d", ThreadLocalRandom.current().nextInt(0, 100_000_000));

        return today + formatted;
    }
}
