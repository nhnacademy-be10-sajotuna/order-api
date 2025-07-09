package shop.sajotuna.order.coupon.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.sajotuna.order.coupon.domain.Coupon;
import shop.sajotuna.order.coupon.domain.CouponSpecificBook;
import shop.sajotuna.order.coupon.domain.CouponSpecificCategory;
import shop.sajotuna.order.coupon.dto.request.CouponRequest;
import shop.sajotuna.order.coupon.dto.response.CouponResponse;
import shop.sajotuna.order.coupon.repository.BookCouponRepository;
import shop.sajotuna.order.coupon.repository.CategoryCouponRepository;
import shop.sajotuna.order.coupon.repository.CouponRepository;

@RequiredArgsConstructor
@Service
@Transactional
public class CouponService {

    private final CouponRepository couponRepository;
    private final BookCouponRepository bookCouponRepository;
    private final CategoryCouponRepository categoryCouponRepository;

    // 쿠폰 조회
    @Transactional(readOnly = true)
    public CouponResponse findCoupon(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId).orElseThrow(EntityNotFoundException::new);

        return CouponResponse.from(coupon);
    }

    // 쿠폰 생성
    public CouponResponse saveCoupon(CouponRequest couponRequest) {
        Coupon coupon = couponRepository.save(couponRequest.toEntity());

        return CouponResponse.from(coupon);
    }

    // 책 쿠폰 생성
    public CouponResponse saveBookCoupon(String isbn, CouponRequest couponRequest){
        Coupon coupon = couponRepository.save(couponRequest.toEntity());
        bookCouponRepository.save(new CouponSpecificBook(isbn, coupon));

        return CouponResponse.from(coupon);
    }

    // 카테고리 쿠폰 생성
    public CouponResponse saveCategoryCoupon(Long categoryId, CouponRequest couponRequest){
        Coupon coupon = couponRepository.save(couponRequest.toEntity());
        categoryCouponRepository.save(new CouponSpecificCategory(categoryId, coupon));

        return CouponResponse.from(coupon);
    }

    // 쿠폰 삭제
    public void deleteCoupon(Long couponId) {
        if(!couponRepository.existsById(couponId)) {
            throw new EntityNotFoundException("Coupon not found");
        }

        if(bookCouponRepository.existsByCoupon_Id(couponId)) {
            bookCouponRepository.deleteByCoupon_Id(couponId);
        }
        if(categoryCouponRepository.existsByCoupon_Id(couponId)){
            categoryCouponRepository.deleteByCoupon_Id(couponId);
        }
        couponRepository.deleteById(couponId);
    }
}
