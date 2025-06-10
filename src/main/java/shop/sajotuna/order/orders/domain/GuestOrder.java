package shop.sajotuna.order.orders.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Entity
@Table(name = "guest_order")
public class GuestOrder {
    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId  // User의 PK를 공유하며 외래키로 사용
    @JoinColumn(name = "order_id")
    private Orders orders;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "email", nullable = false)
    private String email;
}
