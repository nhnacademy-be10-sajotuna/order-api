package shop.sajotuna.order.orders.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.sajotuna.order.orders.dto.GuestOrderRequest;
import shop.sajotuna.order.orders.dto.OrderRequest;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Boolean isMember;

    @Column(nullable = false)
    private LocalDateTime shippingDate;

    @Column(nullable = false)
    private String streetAddress;

    @Column(nullable = false)
    private Integer deliveryPrice;

    @Column(nullable = false)
    private Integer totalPrice;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private Long userId;

    public Order(OrderRequest orderRequest){
        this.isMember = true;
        this.shippingDate = orderRequest.getShippingDate();
        this.streetAddress = orderRequest.getStreetAddress();
        this.deliveryPrice = orderRequest.getDeliveryPrice();
        this.totalPrice = orderRequest.getTotalPrice();
        this.createdAt = LocalDateTime.now();
        this.userId = orderRequest.getUserId();
    }

    public Order(GuestOrderRequest guestOrderRequest){
        this.isMember = false;
        this.shippingDate = guestOrderRequest.getShippingDate();
        this.streetAddress = guestOrderRequest.getStreetAddress();
        this.deliveryPrice = guestOrderRequest.getDeliveryPrice();
        this.totalPrice = guestOrderRequest.getTotalPrice();
        this.createdAt = LocalDateTime.now();
        this.userId = null;
    }
}
