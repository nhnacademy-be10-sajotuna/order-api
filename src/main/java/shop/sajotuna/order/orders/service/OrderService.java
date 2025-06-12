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

    // 회원 주문 저장 - 주문상품, 결제, 쿠폰, 포인트도 한번에 처리하도록 구현해야 함
    @Transactional
    public OrderResponse createUserOrder(OrderRequest orderRequest){
        Order savedOrder = orderRepository.save(new Order(orderRequest));

        for(OrderProductRequest item: orderRequest.getItems()){
            if(!orderPackagingRepository.existsById(item.getOrderPackingId())){
                throw new EntityNotFoundException("orderPackaging not found");
            }
            OrderPackaging packaging = orderPackagingRepository.findById(item.getOrderPackingId()).orElse(null);
            OrderProduct orderProduct = new OrderProduct(savedOrder, item, packaging);
            orderProductRepository.save(orderProduct);
        }

        return new OrderResponse(savedOrder.getId(), savedOrder.getTotalPrice());
    }

    // 비회원 주문 저장
    @Transactional
    public OrderResponse createGuestOrder(GuestOrderRequest guestOrderRequest){
        Order savedOrder = orderRepository.save(new Order(guestOrderRequest));

        GuestOrder guestOrder = new GuestOrder(savedOrder, guestOrderRequest);
        guestOrderRepository.save(guestOrder);

        return new OrderResponse(savedOrder.getId(), savedOrder.getTotalPrice());
    }

}
