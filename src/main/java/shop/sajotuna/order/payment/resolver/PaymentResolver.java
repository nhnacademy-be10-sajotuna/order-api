package shop.sajotuna.order.payment.resolver;

import org.springframework.stereotype.Component;
import shop.sajotuna.order.payment.domain.PaymentMethod;
import shop.sajotuna.order.payment.service.ExternalPaymentService;
import shop.sajotuna.order.payment.service.TossPaymentService;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class PaymentResolver {
    private final Map<PaymentMethod, ExternalPaymentService> serviceMap;

    public PaymentResolver(List<ExternalPaymentService> services) {
        this.serviceMap = services.stream()
                .collect(Collectors.toMap(
                        service -> getMethod(service), // 커스텀 로직으로 Method 구분
                        Function.identity()
                ));
    }

    public ExternalPaymentService resolve(PaymentMethod method) {
        return serviceMap.get(method);
    }

    private PaymentMethod getMethod(ExternalPaymentService service) {
        if (service instanceof TossPaymentService) return PaymentMethod.TOSS;
        throw new IllegalArgumentException("Unknown PaymentService");
    }
}
