package shop.sajotuna.order.orders.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.common.exception.NullValueException;
import shop.sajotuna.order.coupon.domain.UserCoupon;

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

    @OneToOne
    @JoinColumn(name = "user_coupon_id")
    private UserCoupon appliedCoupon;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "discount_amount"))
    private Money discountAmount;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "final_amount"))
    private Money finalAmount;

    public static OrderProduct create(Order order, String isbn, OrderPackaging orderPackaging,
                                      Integer qty, Money amount, Boolean packagingRequest, UserCoupon coupon) {
        return OrderProduct.builder()
                .order(order)
                .isbn(isbn)
                .orderPackaging(orderPackaging)
                .qty(qty)
                .amount(amount)
                .packagingRequest(packagingRequest)
                .discountAmount(Money.zero())  // 기본값: 할인 없음
                .finalAmount(amount.multiply(qty))  // 기본값: 원가 × 수량
                .appliedCoupon(coupon)
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

    public void applyDiscount(Money discountAmount) {
        if (discountAmount == null) {
            throw new NullValueException("할인 금액은 null일 수 없습니다.");
        }
        this.discountAmount = discountAmount;
        this.finalAmount = getTotalPrice().minus(discountAmount);
    }

    public Money applyCouponDiscount() {
        if (appliedCoupon != null) {
            Money discount = appliedCoupon.applyCoupon(getAmount());
            applyDiscount(discount);
            return discount;
        }
        return Money.zero();
    }
}
