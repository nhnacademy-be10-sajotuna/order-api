package shop.sajotuna.order.orders.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.sajotuna.order.orders.dto.OrderProductRequest;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
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

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private Boolean packagingRequest;

    public OrderProduct(Order order, OrderProductRequest orderProductRequest, OrderPackaging orderPackaging) {
        this.order = order;
        this.isbn = orderProductRequest.getIsbn();
        this.orderPackaging = orderPackaging;
        this.qty = orderProductRequest.getQty();
        this.amount = orderProductRequest.getAmount();
        this.packagingRequest = orderProductRequest.getPackagingRequest();
        this.status = OrderStatus.PENDING;
    }
}
