package shop.sajotuna.order.payment.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.orders.domain.Order;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod method;

    @Column(nullable = false)
    private Money amount;

    @NotNull
    private LocalDateTime createdAt;

    public Payment(Order order, PaymentMethod method) {
        this.order = order;
        this.method = method;
        this.amount = order.getFinalPrice();
        this.createdAt = LocalDateTime.now();
    }
}
