package shop.sajotuna.order.orders.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.sajotuna.order.orders.dto.OrderProductResponse;
import shop.sajotuna.order.orders.dto.OrderProductUpdateRequest;
import shop.sajotuna.order.orders.service.OrderProductService;

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

    // 주문 상품의 배송 상태 수정
    @PutMapping("/{orderProductId}")
    public ResponseEntity<Void> updateOrderProduct(@PathVariable Long orderProductId, @RequestBody @Valid OrderProductUpdateRequest request){
        productService.updateOrderProduct(orderProductId, request);

        return ResponseEntity.noContent().build();
    }
}
