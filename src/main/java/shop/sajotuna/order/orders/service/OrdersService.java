package shop.sajotuna.order.orders.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.sajotuna.order.orders.dto.*;
import shop.sajotuna.order.orders.entity.*;
import shop.sajotuna.order.orders.repository.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor
@Service
public class OrdersService {
    private final OrdersRepository ordersRepository;
    private final OrderProductRepository orderProductRepository;
    private final UserOrderRepository userOrderRepository;
    private final GuestOrderRepository guestOrderRepository;
    private final OrderPackagingRepository orderPackagingRepository;

    // 주문내역 존재 확인
    public boolean isNotExistOrders(String orderId){
        return !ordersRepository.existsById(orderId);
    }

    // 주문 조회
    public Orders getOrders(String orderId){
        return ordersRepository.findById(orderId).orElse(null);
    }

    // 회원의 주문내역들 조회
    public List<Orders> findOrdersByMemberId(int memberId) {
        return ordersRepository.findOrdersByMemberId(memberId);
    }

    // 주문, 주문상품들 저장 - 결제, 포인트도 한번에 처리하도록 구현해야 함
    public OrderResponse createOrders(OrderRequest orderRequest){
        Orders orders = ordersRepository.save(new Orders(orderRequest, getRandomId()));

        for(OrderProductRequest item: orderRequest.getItems()){
            if(!orderPackagingRepository.existsById(item.getOrderPackingId())){
                throw new EntityNotFoundException("orderPackaging not found");
            }
            OrderPackaging packaging = orderPackagingRepository.findById(item.getOrderPackingId()).orElse(null);
            OrderProduct orderProduct = new OrderProduct(orders, item, packaging);
            orderProductRepository.save(orderProduct);
        }

        return new OrderResponse(orders.getId(), orders.getTotalPrice(), "결제가 완료되었습니다");
    }

    // 회원 주문 저장
    public void createUserOrders(int memberId, String orderId) {
        if(isNotExistOrders(orderId)) {
            throw new EntityNotFoundException("orders not found");
        }
        Orders orders = ordersRepository.getReferenceById(orderId);
        UserOrder memberOrder = new UserOrder(orders, memberId);

        userOrderRepository.save(memberOrder);
    }

    // 비회원 주문 저장
    public void createGuestOrders(String orderId, String name, String phoneNumber, String email){
        if(isNotExistOrders(orderId)) {
            throw new EntityNotFoundException("orders not found");
        }
        Orders orders = ordersRepository.getReferenceById(orderId);
        GuestOrder guestOrder = new GuestOrder(orders, name, phoneNumber, email);
        guestOrderRepository.save(guestOrder);
    }

    // Orders 랜덤 아이디 생성
    private String getRandomId(){
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String today = now.format(formatter);
        String formatted = String.format("%08d", ThreadLocalRandom.current().nextInt(0, 100_000_000));

        return today + formatted;
    }
}
