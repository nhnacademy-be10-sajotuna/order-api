package shop.sajotuna.order.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.sajotuna.order.payment.dto.PaymentConfirmRequest;
import shop.sajotuna.order.payment.dto.PaymentResponse;
import shop.sajotuna.order.payment.service.PaymentService;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    // 결제 정보 조회
    @GetMapping("/{payment-id}")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable("payment-id") Long paymentId) {
        return ResponseEntity.ok(paymentService.getPayment(paymentId));
    }

    // 모든 결제 정보 조회
    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getAll() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    // 결제 승인
    @PostMapping("/confirm")
    public ResponseEntity<PaymentResponse> confirmPayment(@RequestBody PaymentConfirmRequest paymentConfirmRequest) {
        return ResponseEntity.ok(paymentService.processUserPayment(paymentConfirmRequest));
    }
}
