package shop.sajotuna.order.orders.domain;

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
@Table(name = "returns_and_exchanges")
public class ReturnsExchanges {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "order_product_id", nullable = false)
    private OrderProduct orderProduct;

    @Column(name = "reasons", nullable = false)
    private String reasons;

    @Column(name = "status", nullable = false)
    private OrderStatus orderStatus;

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;


}
