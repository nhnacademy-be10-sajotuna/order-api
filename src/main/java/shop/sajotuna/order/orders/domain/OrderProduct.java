package shop.sajotuna.order.orders.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.common.exception.NullValueException;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
@Entity
public class OrderProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(length = 20, nullable = false)
    private String isbn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_packaging_id")
    private OrderPackaging orderPackaging;

    @Column(nullable = false)
    private Integer qty;

    @Column(nullable = false)
    private Money amount;

    @Column(nullable = false)
    private Boolean packagingRequest;

    public static OrderProduct create(Order order, String isbn, OrderPackaging orderPackaging,
                                      Integer qty, Money amount, Boolean packagingRequest) {
        return OrderProduct.builder()
                .order(order)
                .isbn(isbn)
                .orderPackaging(orderPackaging)
                .qty(qty)
                .amount(amount)
                .packagingRequest(packagingRequest)
                .build();
    }

    public void setOrder(Order order) {
        if (order == null) {
            throw new NullValueException("주문은 null일 수 없습니다.");
        }
        this.order = order;
    }

    public Money getTotalPrice() {
        return amount.multiply(qty);
    }

    public Money getPackagingPrice() {
        if (orderPackaging == null) {
            return Money.zero();
        }
        return orderPackaging.getPrice().multiply(qty);
    }
}
