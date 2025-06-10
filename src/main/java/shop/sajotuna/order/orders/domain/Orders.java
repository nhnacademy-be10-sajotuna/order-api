package shop.sajotuna.order.orders.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Entity
@Table(name = "orders")
public class Orders {
    @Id
    private Long id;

    @NotNull
    private boolean isMember;

    @NotNull
    private LocalDateTime shippingDate;

    @NotBlank
    private String streetAddress;

    private String detailedAddress;

    @NotNull
    private int deliveryPrice;

    @NotNull
    private int totalPrice;

    @NotNull
    private LocalDateTime createdAt;
}
