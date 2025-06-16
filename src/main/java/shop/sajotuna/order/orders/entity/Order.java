package shop.sajotuna.order.orders.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Data
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
}
