package shop.sajotuna.order.orders.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.sajotuna.order.orders.dto.*;
import shop.sajotuna.order.orders.entity.*;
import shop.sajotuna.order.orders.repository.*;

import java.util.List;

@RequiredArgsConstructor
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final GuestOrderRepository guestOrderRepository;
    private final OrderPackagingRepository orderPackagingRepository;

    // 주문내역 존재 확인
    private boolean isNotExistOrder(long orderId){
        return !orderRepository.existsById(orderId);
    }

    // 주문 조회
    public Order findOrder(long orderId){
        return orderRepository.findById(orderId).orElse(null);
    }

    // 회원의 주문 목록 조회
    public List<Order> findOrdersByUserId(long userId){
        return orderRepository.findByUserId(userId);
    }

    // 주문, 주문상품들 저장 - 결제, 포인트도 한번에 처리하도록 구현해야 함
    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest){
        Order order = orderRepository.save(new Order(orderRequest));

        for(OrderProductRequest item: orderRequest.getItems()){
            if(!orderPackagingRepository.existsById(item.getOrderPackingId())){
                throw new EntityNotFoundException("orderPackaging not found");
            }
            OrderPackaging packaging = orderPackagingRepository.findById(item.getOrderPackingId()).orElse(null);
            OrderProduct orderProduct = new OrderProduct(order, item, packaging);
            orderProductRepository.save(orderProduct);
        }

        return new OrderResponse(order.getId(), order.getTotalPrice());
    }


    // 비회원 주문 저장
    public void createGuestOrder(long orderId, String name, String phoneNumber, String email){
        if(isNotExistOrder(orderId)) {
            throw new EntityNotFoundException("orders not found");
        }
        Order order = orderRepository.getReferenceById(orderId);
        GuestOrder guestOrder = new GuestOrder(order, name, phoneNumber, email);
        guestOrderRepository.save(guestOrder);
    }
}
