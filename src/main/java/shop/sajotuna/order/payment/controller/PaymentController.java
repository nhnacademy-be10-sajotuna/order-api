package shop.sajotuna.order.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.sajotuna.order.payment.dto.PaymentResponse;
import shop.sajotuna.order.payment.service.PaymentService;

import java.util.List;

@RestController
@RequestMapping("/api/orders/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    // 주문 번호에 맞춰 결제 정보 조회
    @GetMapping("/{orderId}")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.getPaymentByOrderId(orderId));
    }

    // 유저의 결제 내역 조회
    @GetMapping("/list")
    public ResponseEntity<List<PaymentResponse>> getUserPayments(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(paymentService.getPaymentByUserId(userId));
    }
}
