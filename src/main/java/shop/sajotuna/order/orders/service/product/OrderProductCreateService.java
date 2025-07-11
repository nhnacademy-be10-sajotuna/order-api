package shop.sajotuna.order.orders.service.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.sajotuna.order.coupon.domain.UserCoupon;
import shop.sajotuna.order.coupon.domain.CouponType;
import shop.sajotuna.order.coupon.exception.CouponNotFoundException;
import shop.sajotuna.order.coupon.repository.UserCouponRepository;
import shop.sajotuna.order.coupon.service.BookCouponValidator;
import shop.sajotuna.order.coupon.service.CategoryCouponValidator;
import shop.sajotuna.order.orders.domain.OrderPackaging;
import shop.sajotuna.order.orders.domain.OrderProduct;
import shop.sajotuna.order.orders.exception.PackageNotFoundException;
import shop.sajotuna.order.orders.repository.OrderPackagingRepository;
import shop.sajotuna.order.orders.service.dto.command.CreateOrderProductCommand;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OrderProductCreateService {

    private final OrderPackagingRepository orderPackagingRepository;
    private final UserCouponRepository userCouponRepository;
    private final BookCouponValidator bookCouponValidator;
    private final CategoryCouponValidator categoryCouponValidator;

    @Transactional
    public List<OrderProduct> createOrderProducts(List<CreateOrderProductCommand> productCommands, Long userId) {
        return productCommands.stream()
                .map(product -> createOrderProduct(product, userId))
                .toList();
    }

    private OrderProduct createOrderProduct(CreateOrderProductCommand product, Long userId) {
        OrderPackaging packaging = resolvePackaging(product);
        UserCoupon userCoupon = resolveUserCoupon(product, userId);
        
        return product.toEntity(packaging, userCoupon);
    }
    
    private OrderPackaging resolvePackaging(CreateOrderProductCommand product) {
        if (product.getPackagingRequest() && product.getOrderPackagingId() != null) {
            return getPackaging(product.getOrderPackagingId());
        }
        return null;
    }
    
    private UserCoupon resolveUserCoupon(CreateOrderProductCommand product, Long userId) {
        if (product.getBookCouponId() != null) {
            return getUserCoupon(userId, product.getBookCouponId(), product.getIsbn(), product.getCategoryIds());
        }
        return null;
    }

    private OrderPackaging getPackaging(Long packagingId) {
        return orderPackagingRepository.findById(packagingId)
                .orElseThrow(PackageNotFoundException::new);
    }

    private UserCoupon getUserCoupon(Long userId, Long bookCouponId, String isbn, Set<Long> categoryIds) {
        UserCoupon userCoupon = userCouponRepository.findByUserIdAndCouponIdWithCoupon(userId, bookCouponId)
                .orElseThrow(()-> new CouponNotFoundException(bookCouponId));
        hasCoupon(bookCouponId, userId);

        if (userCoupon.getCoupon().getCouponType() == CouponType.BOOK) {
            bookCouponValidator.validateCoupon(userCoupon.getCoupon().getId(), isbn);
        }
        if (userCoupon.getCoupon().getCouponType() == CouponType.CATEGORY) {
            categoryCouponValidator.validateCoupon(userCoupon.getCoupon().getId(), categoryIds);
        }
        return userCoupon;
    }

    private void hasCoupon(Long couponId, Long userId) {
        if (!userCouponRepository.existsByUserIdAndCouponId(userId, couponId)) {
            throw new CouponNotFoundException(couponId);
        }
    }
}
