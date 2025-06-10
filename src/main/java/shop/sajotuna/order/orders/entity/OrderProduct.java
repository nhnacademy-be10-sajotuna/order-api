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
    private int id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Orders orders;

    @Column(name = "isbn", length = 20, nullable = false)
    private String isbn;

    @ManyToOne
    @JoinColumn(name = "order_packaging_id")
    private OrderPackaging orderPackaging;

    @Column(name = "qty", nullable = false)
    private Integer qty;

    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus orderStatus;

    @Column(name = "packaging_request", nullable = false)
    private Boolean packagingRequest;

    public OrderProduct(Orders orders, OrderProductRequest orderProductRequest, OrderPackaging orderPackaging) {
        this.orders = orders;
        this.isbn = orderProductRequest.getIsbn();
        this.orderPackaging = orderPackaging;
        this.qty = orderProductRequest.getQty();
        this.amount = orderProductRequest.getAmount();
        this.packagingRequest = orderProductRequest.getPackaging_request();
        this.orderStatus = OrderStatus.pending;
    }
}
