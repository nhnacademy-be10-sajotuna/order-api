package shop.sajotuna.order.point.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;

@Entity
@Getter
public class PointPolicy {

    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    @NotNull
    private PointPolicyType type;

    private Integer fixedPoint;

    @Column(precision=4, scale=3)
    private BigDecimal rate;
}
