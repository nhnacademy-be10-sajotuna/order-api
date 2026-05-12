package shop.sajotuna.order.payment.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.sajotuna.order.orders.domain.Order;
import shop.sajotuna.order.orders.repository.OrderRepository;
import shop.sajotuna.order.payment.dto.PaymentConfirmRequest;
import shop.sajotuna.order.payment.dto.PaymentResponse;
import shop.sajotuna.order.payment.domain.Payment;
import shop.sajotuna.order.payment.exception.PaymentNotFoundException;
import shop.sajotuna.order.payment.repository.PaymentRepository;
import shop.sajotuna.order.point.service.dto.event.UserGradeRefreshEvent;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final ExternalPaymentServiceFactory externalPaymentServiceFactory;
    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;

    // 주문 번호에 맞춰 결제 정보 조회
    @Transactional(readOnly = true)
    public PaymentResponse getPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(EntityNotFoundException::new);

        return PaymentResponse.from(payment);
    }

    // 모든 결제 정보 조회
    @Transactional(readOnly = true)
    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll().stream().map(PaymentResponse::from).collect(Collectors.toList());
    }

    public PaymentResponse processUserPayment(PaymentConfirmRequest paymentConfirmRequest) {
        ExternalPaymentService service = externalPaymentServiceFactory.getService(paymentConfirmRequest.getPaymentMethod());

        Order order = orderRepository.findOrderByOrderNumber(paymentConfirmRequest.getOrderNumber());
        PaymentResponse paymentResponse = service.requestPaymentConfirm(paymentConfirmRequest);

        order.completePayment();
        publishUserGradeRefreshEvent(order);

        return paymentResponse;
    }

    // 결제 취소 요청
    public void cancelPayment(Long orderId, String cancelReason){
        Payment payment = paymentRepository.getPaymentByOrder_Id(orderId);
        if(payment == null){
            throw new PaymentNotFoundException();
        }
        ExternalPaymentService service = externalPaymentServiceFactory.getService(payment.getMethod());

        service.requestPaymentCancel(payment, cancelReason);
    }

    private void publishUserGradeRefreshEvent(Order order) {
        Long userId = order.getOrderer().getUserId();
        if (userId != null) {
            eventPublisher.publishEvent(new UserGradeRefreshEvent(userId));
        }
    }
}
