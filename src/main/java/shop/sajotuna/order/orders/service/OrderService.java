package shop.sajotuna.order.orders.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import shop.sajotuna.order.orders.dto.*;
import shop.sajotuna.order.orders.entity.*;
import shop.sajotuna.order.orders.repository.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final GuestOrderRepository guestOrderRepository;
    private final OrderPackagingRepository orderPackagingRepository;

    // 주문 조회
    public OrderResponse findOrder(long orderId){
        Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);

        return OrderResponse.from(order);
    }

    // 회원의 주문 목록 조회
    public List<OrderResponse> findOrdersByUserId(long userId){
        return orderRepository.findByUserId(userId).stream().map(OrderResponse::from).toList();
    }

    // 회원 주문 저장 - 주문상품, 결제, 쿠폰, 포인트도 한번에 처리하도록 구현해야 함
    @Transactional
    public OrderResponse createUserOrder(OrderRequest orderRequest){
        Order savedOrder = orderRepository.save(orderRequest.toEntity());
        // 주문 상품 추가
        for(OrderProductRequest item: orderRequest.getItems()){
            log.info("{}", item.getOrderPackagingId());
            OrderPackaging packaging = orderPackagingRepository.findById(item.getOrderPackagingId()).orElse(null);

            orderProductRepository.save(item.toEntity(savedOrder, packaging));
        }

        return OrderResponse.from(savedOrder);
    }

    // 비회원 주문 저장
    @Transactional
    public OrderResponse createGuestOrder(GuestOrderRequest guestOrderRequest){
        Order savedOrder = orderRepository.save(guestOrderRequest.toEntity());
        // 주문 상품 추가
        for(OrderProductRequest item: guestOrderRequest.getItems()){
            log.info("{}", item.getOrderPackagingId());
            OrderPackaging packaging = orderPackagingRepository.findById(item.getOrderPackagingId()).orElse(null);

            orderProductRepository.save(item.toEntity(savedOrder, packaging));
        }

        guestOrderRepository.save(new GuestOrder(savedOrder, guestOrderRequest));

        return OrderResponse.from(savedOrder);
    }

}
