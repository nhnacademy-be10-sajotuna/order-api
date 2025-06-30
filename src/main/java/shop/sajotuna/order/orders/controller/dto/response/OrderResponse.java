package shop.sajotuna.order.orders.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import shop.sajotuna.order.orders.domain.Order;
import shop.sajotuna.order.orders.domain.OrderStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
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
    private LocalDateTime shippingDate;
    
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
        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),

                // Orderer 정보
                order.getOrderer().getUserId(),
                order.getOrderer().getOrdererName(),
                order.getOrderer().getOrdererPhoneNumber(),
                order.getOrderer().getOrdererEmail(),
                
                order.isUserOrder(),
                
                // ShippingInfo 정보
                order.getShippingInfo().getRecipientName(),
                order.getShippingInfo().getRecipientPhoneNumber(),
                order.getShippingInfo().getRecipientEmail(),
                order.getShippingInfo().getRecipientAddress(),
                order.getShippingInfo().getShippingDate(),
                
                // OrderPrice 정보
                order.getOrderPrice().getTotalProductPrice().getAmount(),
                order.getOrderPrice().getPackagingPrice().getAmount(),
                order.getOrderPrice().getDeliveryPrice().getAmount(),
                order.getTotalPrice().getAmount(),
                
                // Discounts 정보
                order.getDiscounts().getCouponDiscountAmount().getAmount(),
                order.getDiscounts().getUsedPoint().getAmount(),
                order.getDiscounts().getTotalDiscountAmount().getAmount(),
                
                // 최종 금액
                order.getFinalPrice().getAmount(),
                
                order.getStatus(),
                order.getCreatedAt()
        );
    }
}
