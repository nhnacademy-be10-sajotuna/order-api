package shop.sajotuna.order.orders.controller.dto.response;

import lombok.Builder;
import lombok.Getter;
import shop.sajotuna.order.orders.domain.Order;
import shop.sajotuna.order.orders.domain.OrderStatus;

import java.time.LocalDateTime;

@Getter
@Builder
public class OrderResponse {
    private Long orderId;
    private String orderNumber;
    
    // Orderer 정보
    private Long userId;
    private String ordererName;
    private String ordererPhoneNumber;
    private String ordererEmail;
    
    private boolean isUserOrder;
    
    // ShippingInfo 정보
    private String recipientName;
    private String recipientPhoneNumber;
    private String recipientEmail;
    private String recipientAddress;
    private LocalDateTime expectedDeliveryDate;
    private LocalDateTime shippingStartDate;
    private LocalDateTime shippingEndDate;
    
    // OrderPrice 정보
    private int totalProductPrice;
    private int packagingPrice;
    private int deliveryPrice;
    private int totalPrice;
    
    // Discounts 정보
    private int couponDiscountAmount;
    private int usedPoint;
    private int totalDiscountAmount;
    
    // 최종 금액
    private int finalPrice;
    
    private OrderStatus status;
    private LocalDateTime createdAt;

    public static OrderResponse from(Order order) {
        return OrderResponse.builder()
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                
                // Orderer 정보
                .userId(order.getOrderer().getUserId())
                .ordererName(order.getOrderer().getOrdererName())
                .ordererPhoneNumber(order.getOrderer().getOrdererPhoneNumber())
                .ordererEmail(order.getOrderer().getOrdererEmail())
                
                .isUserOrder(order.isUserOrder())
                
                // ShippingInfo 정보
                .recipientName(order.getShippingInfo().getRecipientName())
                .recipientPhoneNumber(order.getShippingInfo().getRecipientPhoneNumber())
                .recipientEmail(order.getShippingInfo().getRecipientEmail())
                .recipientAddress(order.getShippingInfo().getRecipientAddress())
                .expectedDeliveryDate(order.getShippingInfo().getExpectedDeliveryDate())
                .shippingStartDate(order.getShippingInfo().getShippingStartDate())
                .shippingEndDate(order.getShippingInfo().getShippingEndDate())
                
                // OrderPrice 정보
                .totalProductPrice(order.getOrderPrice().getTotalProductPrice().getAmount())
                .packagingPrice(order.getOrderPrice().getPackagingPrice().getAmount())
                .deliveryPrice(order.getOrderPrice().getDeliveryPrice().getAmount())
                .totalPrice(order.getTotalPrice().getAmount())
                
                // Discounts 정보
                .couponDiscountAmount(order.getDiscounts().getCouponDiscountAmount().getAmount())
                .usedPoint(order.getDiscounts().getUsedPoint().getAmount())
                .totalDiscountAmount(order.getDiscounts().getTotalDiscountAmount().getAmount())
                
                // 최종 금액
                .finalPrice(order.getFinalPrice().getAmount())
                
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
