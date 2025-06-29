package shop.sajotuna.order.orders.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.sajotuna.order.common.domain.Money;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "order_packaging")
public class OrderPackaging {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String packaging;

    @Column(nullable = false)
    private Money price;

    public OrderPackaging(String packaging, Money price) {
        this.packaging = packaging;
        this.price = price;
    }

    public void update(String packaging, Money price) {
        this.packaging = packaging;
        this.price = price;
    }
}
