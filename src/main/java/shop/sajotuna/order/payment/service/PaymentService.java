package shop.sajotuna.order.payment.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.sajotuna.order.payment.dto.PaymentResponse;
import shop.sajotuna.order.payment.entity.Payment;
import shop.sajotuna.order.payment.repository.PaymentRepository;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;

    // 주문 번호에 맞춰 결제 정보 조회
    public PaymentResponse getPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(EntityNotFoundException::new);

        return PaymentResponse.from(payment);
    }

    // 모든 결제 정보 조회
    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll().stream().map(PaymentResponse::from).collect(Collectors.toList());
    }
}