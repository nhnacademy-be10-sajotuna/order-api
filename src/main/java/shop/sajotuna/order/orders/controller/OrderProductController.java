package shop.sajotuna.order.orders.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.sajotuna.order.orders.dto.OrderProductResponse;
import shop.sajotuna.order.orders.dto.OrderProductUpdateRequest;
import shop.sajotuna.order.orders.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api/orders/product")
@RequiredArgsConstructor
public class OrderProductController {
    private final ProductService productService;

    // 주문 상품 조회
    @GetMapping("/{orderProductId}")
    public ResponseEntity<OrderProductResponse> getOrderProduct(@PathVariable Long orderProductId){
        return new ResponseEntity<>(productService.findById(orderProductId), HttpStatus.OK);
    }

    // 주문 번호에 포함된 상품들 조회
    @GetMapping("/list/{orderId}")
    public ResponseEntity<List<OrderProductResponse>> getOrderProducts(@PathVariable Long orderId){
        return new ResponseEntity<>(productService.findByOrderId(orderId), HttpStatus.OK);
    }

    // 주문 상품의 배송 상태 수정
    @PutMapping("/{orderProductId}")
    public ResponseEntity<String> updateOrderProduct(@PathVariable Long orderProductId, @RequestBody OrderProductUpdateRequest request){
        productService.updateOrderProduct(orderProductId, request);

        return new ResponseEntity<>("정상적으로 동작되었습니다", HttpStatus.OK);
    }
}
