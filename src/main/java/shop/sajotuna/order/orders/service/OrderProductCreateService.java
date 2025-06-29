package shop.sajotuna.order.orders.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.sajotuna.order.orders.domain.OrderPackaging;
import shop.sajotuna.order.orders.domain.OrderProduct;
import shop.sajotuna.order.orders.exception.PackageNotFoundException;
import shop.sajotuna.order.orders.repository.OrderPackagingRepository;
import shop.sajotuna.order.orders.service.dto.command.CreateOrderProductCommand;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderProductCreateService {

    private final OrderPackagingRepository orderPackagingRepository;

    public List<OrderProduct> createOrderProducts(List<CreateOrderProductCommand> productCommands) {
        return productCommands.stream()
                .map(this::createOrderProduct)
                .toList();
    }

    private OrderProduct createOrderProduct(CreateOrderProductCommand product) {
        if (product.isPackagingRequest()) {
            return product.toEntity(getPackaging(product.getOrderPackagingId()));
        }
        return product.toEntity(null);
    }

    private OrderPackaging getPackaging(Long packagingId) {
        return orderPackagingRepository.findById(packagingId)
                .orElseThrow(PackageNotFoundException::new);
    }
}
