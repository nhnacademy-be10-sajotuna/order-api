package shop.sajotuna.order.orders.controller.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.orders.domain.Orderer;
import shop.sajotuna.order.orders.domain.ShippingInfo;
import shop.sajotuna.order.orders.service.dto.command.CreateOrderCommand;
import shop.sajotuna.order.orders.service.dto.command.CreateOrderProductCommand;
import shop.sajotuna.order.orders.validation.annotation.PhoneNumber;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class CreateOrderRequest {

    @NotBlank
    private String ordererName;

    @NotBlank
    @PhoneNumber
    private String ordererPhoneNumber;

    @NotBlank
    @Email
    private String ordererEmail;

    @NotBlank
    private String recipientName;

    @NotBlank
    @PhoneNumber
    private String recipientPhoneNumber;

    @Email
    private String recipientEmail;

    @NotBlank
    private String recipientAddress;

    @Future
    @JsonFormat(pattern = "yyyyMMddHHmmss")
    private LocalDateTime expectedDeliveryDate;

    private Long orderCouponId;

    @Min(0)
    private Integer usedPoint;

    @NotEmpty
    @Valid
    private List<OrderProductRequest> items;

    public CreateOrderCommand toCommand(Long userId) {
        if(usedPoint == null) {
            usedPoint = 0;
        }

        return CreateOrderCommand.builder()
                .orderer(toOrderer(userId))
                .shippingInfo(toShippingInfo())
                .orderCouponId(orderCouponId)
                .usedPoint(Money.of(usedPoint))
                .items(items.stream()
                        .map(CreateOrderProductCommand::from)
                        .collect(Collectors.toList()))
                .build();
    }

    private Orderer toOrderer(Long userId) {
        return Orderer.createOrderer(
                userId,
                ordererName,
                ordererPhoneNumber,
                ordererEmail);
    }

    private ShippingInfo toShippingInfo() {
        return ShippingInfo.create(
                recipientName,
                recipientPhoneNumber,
                recipientEmail,
                recipientAddress,
                expectedDeliveryDate
        );
    }
}

