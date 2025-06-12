package shop.sajotuna.order.orders.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    public Order(Boolean isMember, LocalDateTime shippingDate, String streetAddress,
                 Integer deliveryPrice, Integer totalPrice, Long userId) {
        this.isMember = isMember;
        this.shippingDate = shippingDate;
        this.streetAddress = streetAddress;
        this.deliveryPrice = deliveryPrice;
        this.totalPrice = totalPrice;
        this.createdAt = LocalDateTime.now();
        this.userId = userId;
    }
}
