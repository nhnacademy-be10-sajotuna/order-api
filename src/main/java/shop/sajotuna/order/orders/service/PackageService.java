package shop.sajotuna.order.orders.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.sajotuna.order.orders.dto.PackageRequest;
import shop.sajotuna.order.orders.dto.PackageResponse;
import shop.sajotuna.order.orders.entity.OrderPackaging;
import shop.sajotuna.order.orders.repository.OrderPackagingRepository;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class PackageService {
    private final OrderPackagingRepository orderPackagingRepository;

    // package 생성
    public PackageResponse createPackage(PackageRequest request) {
        OrderPackaging orderPackaging = orderPackagingRepository.save(new OrderPackaging(request.getPackaging(), request.getPrice()));

        return new PackageResponse(orderPackaging.getId(), orderPackaging.getPackaging(), orderPackaging.getPrice());
    }

    // package 수정
    public void updatePackage(long id, PackageRequest request) {
        if(!orderPackagingRepository.existsById(id)){
            throw new EntityNotFoundException("OrderPackaging not found");
        }
        OrderPackaging orderPackaging = orderPackagingRepository.findById(id).orElse(null);

        Objects.requireNonNull(orderPackaging).setPackaging(request.getPackaging());
        orderPackaging.setPrice(request.getPrice());
    }

    // package 삭제
    public void deletePackage(long id) {
        if(!orderPackagingRepository.existsById(id)){
            throw new EntityNotFoundException("OrderPackaging not found");
        }
        orderPackagingRepository.deleteById(id);
    }

    // package 목록 조회
    @Transactional(readOnly = true)
    public List<PackageResponse> getPackages(){
        List<OrderPackaging> orderPackages = orderPackagingRepository.findAll();

        return orderPackages.stream().map(PackageResponse::from).toList();
    }
}
