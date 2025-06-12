package shop.sajotuna.order.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.sajotuna.order.payment.service.PaymentService;

@RestController
@RequestMapping("/api/orders/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    // 주문 번호에 맞춰 결제 정보 조회
    @GetMapping("/{orderId}")
    public ResponseEntity<?> getPayment(@PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.getPaymentByOrderId(orderId));
    }
}
