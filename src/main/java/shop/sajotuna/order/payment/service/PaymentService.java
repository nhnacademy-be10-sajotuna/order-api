package shop.sajotuna.order.payment.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.sajotuna.order.orders.domain.Order;
import shop.sajotuna.order.orders.repository.OrderRepository;
import shop.sajotuna.order.payment.domain.PaymentMethod;
import shop.sajotuna.order.payment.dto.PaymentConfirmRequest;
import shop.sajotuna.order.payment.dto.PaymentResponse;
import shop.sajotuna.order.payment.domain.Payment;
import shop.sajotuna.order.payment.exception.PaymentNotFoundException;
import shop.sajotuna.order.payment.repository.PaymentRepository;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final List<ExternalPaymentService> list;
    private final OrderRepository orderRepository;

    // 주문 번호에 맞춰 결제 정보 조회
    public PaymentResponse getPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(EntityNotFoundException::new);

        return PaymentResponse.from(payment);
    }

    // 모든 결제 정보 조회
    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll().stream().map(PaymentResponse::from).collect(Collectors.toList());
    }

    private ExternalPaymentService getExternalPaymentService(PaymentMethod paymentMethod) {
        return list.stream()
                .filter(externalPaymentService ->
                        externalPaymentService.support(paymentMethod))
                .findFirst()
                .orElseThrow(EntityNotFoundException::new);
    }

    public PaymentResponse processUserPayment(PaymentConfirmRequest paymentConfirmRequest) {
        ExternalPaymentService service = getExternalPaymentService(paymentConfirmRequest.getPaymentMethod());

        Order order = orderRepository.findOrderByOrderNumber(paymentConfirmRequest.getOrderNumber());
        order.completePayment();
        order.getFinalPrice();

        return service.requestPaymentConfirm(paymentConfirmRequest);
    }

    // 결제 취소 요청
    public void cancelPayment(Long orderId, String cancelReason){
        Payment payment = paymentRepository.getPaymentByOrder_Id(orderId);
        if(payment == null){
            throw new PaymentNotFoundException();
        }
        ExternalPaymentService service = getExternalPaymentService(payment.getMethod());

        service.requestPaymentCancel(payment, cancelReason);
    }
}