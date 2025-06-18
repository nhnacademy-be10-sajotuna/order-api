package shop.sajotuna.order.orders.entity;

import jakarta.persistence.*;
import lombok.*;
import shop.sajotuna.order.orders.exception.InvalidStatusException;
import shop.sajotuna.order.orders.exception.TimeOutException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private Long userId;

    // 주문 발송
    public void shipped(){
        if (!this.status.equals(OrderStatus.PENDING)) {
            throw new InvalidStatusException();
        }
        this.shippingDate = LocalDateTime.now();
        this.status = OrderStatus.SHIPPED;
    }

    // 주문 발송 완료
    public void delivered(){
        if (!this.status.equals(OrderStatus.SHIPPED)) {
            throw new InvalidStatusException();
        }
        this.status = OrderStatus.DELIVERED;
    }

    // 주문 취소
    public void cancelled(){
        if (!this.status.equals(OrderStatus.PENDING)) {
            throw new InvalidStatusException();
        }
        this.status = OrderStatus.CANCELLED;
    }

    // 주문 반품
    public void returned(){
        if (!this.status.equals(OrderStatus.DELIVERED)) {
            throw new InvalidStatusException();
        }
        if(ChronoUnit.DAYS.between(shippingDate, LocalDateTime.now()) > 10) {
            throw new TimeOutException();
        }

        this.status = OrderStatus.RETURNED;
    }
}
