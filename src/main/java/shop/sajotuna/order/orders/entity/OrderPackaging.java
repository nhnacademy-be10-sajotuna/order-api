package shop.sajotuna.order.orders.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Entity
@Table(name = "order_packaging")
public class OrderPackaging {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String packaging;

    @Column(nullable = false)
    private Integer price;

    public OrderPackaging(String packaging, Integer price) {
        this.packaging = packaging;
        this.price = price;
    }
}
