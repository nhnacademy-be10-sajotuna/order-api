package shop.sajotuna.order.orders.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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

    private String detailedAddress;

    @Column(nullable = false)
    private Integer deliveryPrice;

    @Column(nullable = false)
    private Integer totalPrice;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private Integer userId;

    public Order(OrderRequest request) {
        this.isMember = request.getIsMember();
        this.shippingDate = request.getShippingDate();
        this.streetAddress = request.getStreetAddress();
        this.detailedAddress = request.getDetailedAddress();
        this.deliveryPrice = request.getDeliveryPrice();
        this.totalPrice = request.getTotalPrice();
        this.createdAt = LocalDateTime.now();
    }
}
