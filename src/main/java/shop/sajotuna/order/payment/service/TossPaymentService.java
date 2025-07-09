package shop.sajotuna.order.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import shop.sajotuna.order.orders.domain.Order;
import shop.sajotuna.order.orders.repository.OrderRepository;
import shop.sajotuna.order.orders.service.product.OrderProductService;
import shop.sajotuna.order.payment.domain.Payment;
import shop.sajotuna.order.payment.domain.PaymentMethod;
import shop.sajotuna.order.payment.domain.TossPayment;
import shop.sajotuna.order.payment.dto.PaymentConfirmRequest;
import shop.sajotuna.order.payment.dto.PaymentResponse;
import shop.sajotuna.order.payment.exception.PaymentFailException;
import shop.sajotuna.order.payment.exception.PaymentNotFoundException;
import shop.sajotuna.order.payment.repository.PaymentRepository;
import shop.sajotuna.order.payment.repository.TossPaymentRepository;
import shop.sajotuna.order.point.exception.OrderNotFoundException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@RequiredArgsConstructor
@Service
public class TossPaymentService implements ExternalPaymentService{

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final TossPaymentRepository tossPaymentRepository;
    private final OrderProductService orderProductService;

    @Value("${toss.payment.secret-key}")
    private String secretKey;

    // 토스 결제 승인 요청
    @Override
    public PaymentResponse requestPaymentConfirm(PaymentConfirmRequest paymentConfirmRequest) {
        try(HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.tosspayments.com/v1/payments/confirm"))
                    .header("Authorization", getAuthorizations())
                    .header("Content-Type", "application/json")
                    .method("POST", HttpRequest.BodyPublishers.ofString("{\"paymentKey\":\"" + paymentConfirmRequest.getPaymentKey()
                            + "\",\"orderId\":\"" + paymentConfirmRequest.getOrderNumber()
                            + "\",\"amount\":" + paymentConfirmRequest.getAmount() + "}"))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() == 200){
                Order order = orderRepository.findOrderByOrderNumber(paymentConfirmRequest.getOrderNumber());
                if(order == null){
                    throw new OrderNotFoundException();
                }
                Payment payment = new Payment(order, paymentConfirmRequest.getPaymentMethod());
                paymentRepository.save(payment);

                TossPayment tossPayment = TossPayment.builder()
                        .payment(payment)
                        .paymentKey(paymentConfirmRequest.getPaymentKey())
                        .amount(paymentConfirmRequest.getAmount())
                        .orderId(paymentConfirmRequest.getOrderNumber())
                        .build();
                tossPaymentRepository.save(tossPayment);

                return PaymentResponse.from(payment);
            } else {
                Order order = orderRepository.findOrderByOrderNumber(paymentConfirmRequest.getOrderNumber());
                orderProductService.deleteByOrderId(order.getId());
                orderRepository.delete(order);

                throw new PaymentFailException();
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean support(PaymentMethod paymentMethod) {
        return paymentMethod == PaymentMethod.TOSS;
    }

    // 토스 결제 취소 요청
    @Override
    public void requestPaymentCancel(Payment payment, String cancelReason) {
        TossPayment tossPayment = tossPaymentRepository.getByPayment_Id(payment.getId());
        if(tossPayment == null){
            throw new PaymentNotFoundException();
        }

        try(HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.tosspayments.com/v1/payments/" + tossPayment.getPaymentKey() + "/cancel"))
                    .header("Authorization", getAuthorizations())
                    .header("Content-Type", "application/json")
                    .method("POST", HttpRequest.BodyPublishers.ofString("{\"cancelReason\":\"" + cancelReason + "\"}"))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() == 200){
                // TODO: 사용한 쿠폰, 포인트 반환

            } else {
                throw new PaymentFailException();
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private String getAuthorizations() {
        final byte[] encodedBytes = Base64.getEncoder().encode((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedBytes);
    }
}
