package shop.sajotuna.order.coupon.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BirthdayUserResponse {
    private Long id;
    private LocalDate birthDate;
}
