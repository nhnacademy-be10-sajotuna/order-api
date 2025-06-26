package shop.sajotuna.order.orders.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.sajotuna.order.orders.dto.PackageRequest;
import shop.sajotuna.order.orders.dto.PackageResponse;
import shop.sajotuna.order.orders.domain.OrderPackaging;
import shop.sajotuna.order.orders.exception.PackageNotFoundException;
import shop.sajotuna.order.orders.repository.OrderPackagingRepository;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class PackageService {
    private final OrderPackagingRepository orderPackagingRepository;

    // package 생성
    public PackageResponse createPackage(PackageRequest request) {
        OrderPackaging orderPackaging = orderPackagingRepository.save(new OrderPackaging(request.getPackaging(), request.getPrice()));

        return PackageResponse.from(orderPackaging);
    }

    // package 수정
    public void updatePackage(long id, PackageRequest request) {
        OrderPackaging orderPackaging = orderPackagingRepository.findById(id).orElseThrow(PackageNotFoundException::new);
        orderPackaging.update(request.getPackaging(), request.getPrice());
    }

    // package 삭제
    public void deletePackage(long id) {
        if(!orderPackagingRepository.existsById(id)){
            throw new PackageNotFoundException();
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
