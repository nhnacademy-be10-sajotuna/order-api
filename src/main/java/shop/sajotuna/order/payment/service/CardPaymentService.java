package shop.sajotuna.order.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import shop.sajotuna.order.orders.domain.Order;
import shop.sajotuna.order.orders.repository.OrderRepository;
import shop.sajotuna.order.payment.domain.Payment;
import shop.sajotuna.order.payment.domain.PaymentMethod;
import shop.sajotuna.order.payment.dto.PaymentConfirmRequest;
import shop.sajotuna.order.payment.dto.PaymentResponse;
import shop.sajotuna.order.payment.repository.PaymentRepository;
import shop.sajotuna.order.point.exception.OrderNotFoundException;

@Slf4j
@RequiredArgsConstructor
@Service
public class CardPaymentService implements ExternalPaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public PaymentResponse requestPaymentConfirm(PaymentConfirmRequest paymentConfirmRequest) {
        Order order = orderRepository.findOrderByOrderNumber(paymentConfirmRequest.getOrderNumber());
        if(order == null){
            throw new OrderNotFoundException();
        }

        Payment payment = new Payment(order, paymentConfirmRequest.getPaymentMethod());
        paymentRepository.save(payment);

        return PaymentResponse.from(payment);
    }

    @Override
    public void requestPaymentCancel(Payment payment, String cancelReason) {
        log.info("결제가 취소되었습니다.");
    }

    @Override
    public boolean support(PaymentMethod paymentMethod) {
        return paymentMethod == PaymentMethod.CARD;
    }
}
