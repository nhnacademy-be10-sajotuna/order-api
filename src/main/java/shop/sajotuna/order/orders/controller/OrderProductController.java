package shop.sajotuna.order.orders.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.sajotuna.order.orders.controller.dto.response.OrderProductResponse;
import shop.sajotuna.order.orders.service.product.OrderProductService;

import java.util.List;

@RestController
@RequestMapping("/api/orders/product")
@RequiredArgsConstructor
public class OrderProductController {
    private final OrderProductService productService;

    // 주문 상품 조회
    @GetMapping("/{orderProductId}")
    public ResponseEntity<OrderProductResponse> getOrderProduct(@PathVariable Long orderProductId){
        return ResponseEntity.ok(productService.findById(orderProductId));
    }

    // 주문 번호에 포함된 상품들 조회
    @GetMapping("/list/{orderId}")
    public ResponseEntity<List<OrderProductResponse>> getOrderProducts(@PathVariable Long orderId){
        return ResponseEntity.ok(productService.findByOrderId(orderId));
    }

    // 사용자가 특정 상품에 대해 리뷰를 작성할 수 있는지 확인
    @GetMapping("/review-eligible/{userId}/{isbn}")
    public ResponseEntity<Boolean> isEligibleForReview(@PathVariable Long userId, @PathVariable String isbn) {
        return ResponseEntity.ok(productService.isEligibleForReview(userId, isbn));
    }
}
