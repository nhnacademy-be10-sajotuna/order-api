package shop.sajotuna.order.point.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
public class Point {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long userId;

    @NotNull
    @PositiveOrZero
    private Long amount;

    @Enumerated(value = EnumType.STRING)
    @NotNull
    private PointType type;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    @PositiveOrZero
    private Long remainPoint;
}
