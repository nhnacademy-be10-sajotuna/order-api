package shop.sajotuna.order.orders.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.sajotuna.order.orders.dto.GuestOrderRequest;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "guest_order")
public class GuestOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String email;

    public GuestOrder(Order order, GuestOrderRequest guestOrderRequest) {
        this.order = order;
        this.name = guestOrderRequest.getName();
        this.phoneNumber = guestOrderRequest.getPhoneNumber();
        this.email = guestOrderRequest.getEmail();
    }
}
