package shop.sajotuna.order.orders.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import shop.sajotuna.order.orders.domain.Order;
import shop.sajotuna.order.orders.domain.OrderStatus;
import shop.sajotuna.order.payment.domain.Payment;
import shop.sajotuna.order.payment.domain.PaymentMethod;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class OrderDetailResponse {
    private Long orderId;
    
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
    private LocalDateTime orderCreatedAt;
    
    // 주문 상품 목록
    private List<OrderProductResponse> items;
    
    // Payment 정보
    private PaymentMethod paymentMethod;
    private Integer paymentAmount;
    private LocalDateTime paymentCreatedAt;

    public static OrderDetailResponse from(Order order, List<OrderProductResponse> items, Payment payment) {
        return OrderDetailResponse.builder()
                .orderId(order.getId())
                
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
                .shippingDate(order.getShippingInfo().getShippingDate())
                
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
                .orderCreatedAt(order.getCreatedAt())
                
                // 주문 상품 목록
                .items(items)
                
                // Payment 정보
                .paymentMethod(payment.getMethod())
                .paymentAmount(payment.getAmount().getAmount())
                .paymentCreatedAt(payment.getCreatedAt())
                .build();
    }
}
