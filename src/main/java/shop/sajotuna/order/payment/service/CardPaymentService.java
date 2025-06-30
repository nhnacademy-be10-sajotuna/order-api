package shop.sajotuna.order.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.sajotuna.order.orders.domain.Order;
import shop.sajotuna.order.orders.repository.OrderRepository;
import shop.sajotuna.order.payment.domain.Payment;
import shop.sajotuna.order.payment.dto.PaymentConfirmRequest;
import shop.sajotuna.order.payment.dto.PaymentResponse;
import shop.sajotuna.order.payment.repository.PaymentRepository;
import shop.sajotuna.order.point.exception.OrderNotFoundException;

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
}
