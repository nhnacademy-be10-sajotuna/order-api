package shop.sajotuna.order.orders.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "order_product")
public class OrderProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(length = 20, nullable = false)
    private String isbn;

    @ManyToOne
    @JoinColumn(name = "order_packaging_id")
    private OrderPackaging orderPackaging;

    @Column(nullable = false)
    private Integer qty;

    @Column(nullable = false)
    private Integer amount;

    @Column(nullable = false)
    private Boolean packagingRequest;
}
