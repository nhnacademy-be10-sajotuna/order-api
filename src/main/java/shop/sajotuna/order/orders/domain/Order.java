package shop.sajotuna.order.orders.domain;

import jakarta.persistence.*;
import lombok.*;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.orders.exception.InvalidStatusException;
import shop.sajotuna.order.orders.exception.TimeOutException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@Getter
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Orderer orderer;

    @Column(nullable = false)
    private boolean isUserOrder;

    @Embedded
    private ShippingInfo shippingInfo;

    @Embedded
    private OrderPrice orderPrice;

    @Embedded
    private Discounts discounts;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<OrderProduct> orderProducts = new ArrayList<>();

    private void addOrderProduct(List<OrderProduct> orderProducts) {
        for (OrderProduct orderProduct : orderProducts) {
            if (orderProduct == null) {
                throw new IllegalArgumentException("주문 상품은 null일 수 없습니다.");
            }
            orderProduct.setOrder(this);
            this.orderProducts.add(orderProduct);
        }
    }

    public static Order createUserOrder(
            Orderer orderer,
            ShippingInfo shippingInfo,
            OrderPrice orderPrice,
            Discounts discounts,
            List<OrderProduct> orderProducts
    ) {
        Order order = Order.builder()
                .orderer(orderer)
                .isUserOrder(true)
                .shippingInfo(shippingInfo)
                .orderPrice(orderPrice)
                .discounts(discounts)
                .status(OrderStatus.PENDING)
                .build();
        order.addOrderProduct(orderProducts);
        return order;
    }

    public static Order createGuestOrder(
            Orderer orderer,
            ShippingInfo shippingInfo,
            OrderPrice orderPrice,
            Discounts discounts,
            List<OrderProduct> orderProducts
    ) {
        return Order.builder()
                .orderer(orderer)
                .isUserOrder(false)
                .shippingInfo(shippingInfo)
                .orderPrice(orderPrice)
                .discounts(discounts)
                .status(OrderStatus.PENDING)
                .orderProducts(orderProducts)
                .build();
    }

    public Money getTotalPrice() {
        return orderPrice.getTotalPrice();
    }

    public Money getFinalPrice() {
        return orderPrice.getTotalPrice().minus(discounts.getTotalDiscountAmount());
    }

    public Money getFinalProductPrice() {
        return orderPrice.getTotalProductPrice().minus(discounts.getTotalDiscountAmount());
    }

    // 주문 발송
    public void shipped() {
        if (!this.status.equals(OrderStatus.PENDING)) {
            throw new InvalidStatusException();
        }
        shippingInfo.startShipping();
        this.status = OrderStatus.SHIPPED;
    }

    // 주문 발송 완료
    public void delivered() {
        if (!this.status.equals(OrderStatus.SHIPPED)) {
            throw new InvalidStatusException();
        }
        this.status = OrderStatus.DELIVERED;
    }

    // 주문 취소
    public void cancelled() {
        if (!this.status.equals(OrderStatus.PENDING)) {
            throw new InvalidStatusException();
        }
        this.status = OrderStatus.CANCELLED;
    }

    // 주문 반품
    public void returned() {
        if (!this.status.equals(OrderStatus.DELIVERED)) {
            throw new InvalidStatusException();
        }
        if (ChronoUnit.DAYS.between(shippingInfo.getShippingDate(), LocalDateTime.now()) > 10) {
            throw new TimeOutException();
        }

        this.status = OrderStatus.RETURNED;
    }
}
