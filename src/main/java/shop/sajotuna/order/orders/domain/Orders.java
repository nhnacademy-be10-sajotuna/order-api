package shop.sajotuna.order.orders.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Entity
@Table(name = "orders")
public class Orders {
    @Id
    @Length(max = 16)
    private String id;

    @Column(name = "is_member", nullable = false)
    private Boolean isMember;

    @NotNull
    @Column(name = "shipping_date", nullable = false)
    private LocalDateTime shippingDate;

    @Column(name = "street_address", nullable = false)
    private String streetAddress;

    @Column(name = "detailed_address")
    private String detailedAddress;

    @Column(name = "delivery_price", nullable = false)
    private int deliveryPrice;

    @Column(name = "total_price", nullable = false)
    private int totalPrice;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public Orders(OrdersRequest request, String id) {
        this.id = id;
        this.isMember = request.getIsMember();
        this.shippingDate = request.getShippingDate();
        this.streetAddress = request.getStreetAddress();
        this.detailedAddress = request.getDetailedAddress();
        this.deliveryPrice = request.getDeliveryPrice();
        this.totalPrice = request.getTotalPrice();
        this.createdAt = LocalDateTime.now();
    }
}
