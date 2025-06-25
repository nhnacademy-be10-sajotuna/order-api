package shop.sajotuna.order.orders.entity;

import jakarta.persistence.*;
import lombok.*;
import shop.sajotuna.order.orders.exception.InvalidStatusException;
import shop.sajotuna.order.orders.exception.TimeOutException;
import shop.sajotuna.order.point.exception.InvalidPriceException;

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

    @Embedded
    private OrderPrice orderPrice;

    @Embedded
    private Discounts discounts;

    @Column(nullable = false)
    private Integer totalPrice;

    @Column(nullable = false)
    private Integer finalPrice;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private Long userId;

    public static Order createBaseUserOrder(LocalDateTime shippingDate, String streetAddress, Long userId) {
        return Order.builder()
                .isMember(true)
                .shippingDate(shippingDate)
                .streetAddress(streetAddress)
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .userId(userId).build();
    }

    public static Order createBaseGuestOrder(LocalDateTime shippingDate, String streetAddress) {
        return Order.builder()
                .isMember(false)
                .shippingDate(shippingDate)
                .streetAddress(streetAddress)
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now()).build();
    }

    public void setOrderPrice(OrderPrice orderPrice) {
        validatePricingCalculation(orderPrice);
        this.orderPrice = orderPrice;
        this.totalPrice = orderPrice.getTotalProductPrice() + orderPrice.getPackagingPrice() + orderPrice.getDeliveryPrice();
        setFinalPrice();
    }

    private void validatePricingCalculation(OrderPrice orderPrice) {
        if (orderPrice == null) {
            throw new IllegalArgumentException("가격 정보는 null일 수 없습니다.");
        }

        if (orderPrice.getTotalProductPrice() <= 0) {
            throw new InvalidPriceException(orderPrice.getTotalProductPrice());
        }

        if (orderPrice.getDeliveryPrice() < 0) {
            throw new InvalidPriceException(orderPrice.getDeliveryPrice());
        }
    }

    public void setFinalPrice() {
        finalPrice = totalPrice - discounts.getCouponDiscountAmount() - discounts.getUsedPoint();

        if (finalPrice < 0) {
            throw new InvalidPriceException(finalPrice);
        }
    }

    // 주문 발송
    public void shipped() {
        if (!this.status.equals(OrderStatus.PENDING)) {
            throw new InvalidStatusException();
        }
        this.shippingDate = LocalDateTime.now();
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
        if (ChronoUnit.DAYS.between(shippingDate, LocalDateTime.now()) > 10) {
            throw new TimeOutException();
        }

        this.status = OrderStatus.RETURNED;
    }
}
