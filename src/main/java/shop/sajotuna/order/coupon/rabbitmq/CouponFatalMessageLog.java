package shop.sajotuna.order.coupon.rabbitmq;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CouponFatalMessageLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private String payload;

    @Lob
    private String headers;

    private String exceptionType;
    private String exceptionMessage;
    private LocalDateTime occurredAt;
}
