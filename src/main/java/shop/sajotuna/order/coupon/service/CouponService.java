package shop.sajotuna.order.coupon.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.sajotuna.order.coupon.domain.Coupon;
import shop.sajotuna.order.coupon.domain.CouponSpecificBook;
import shop.sajotuna.order.coupon.domain.CouponSpecificCategory;
import shop.sajotuna.order.coupon.dto.request.BookInfo;
import shop.sajotuna.order.coupon.dto.request.CouponRequest;
import shop.sajotuna.order.coupon.dto.response.CouponResponse;
import shop.sajotuna.order.coupon.repository.BookCouponRepository;
import shop.sajotuna.order.coupon.repository.CategoryCouponRepository;
import shop.sajotuna.order.coupon.repository.CouponRepository;

import java.util.List;
import java.util.stream.Stream;

import static java.util.Arrays.stream;

@RequiredArgsConstructor
@Service
@Transactional
public class CouponService {

    private final CouponRepository couponRepository;
    private final BookCouponRepository bookCouponRepository;
    private final CategoryCouponRepository categoryCouponRepository;

    @Transactional(readOnly = true)
    public List<CouponResponse> getAllCoupons() {
        List<Coupon> coupons = couponRepository.findAll();

        return coupons.stream().map(CouponResponse::from).toList();
    }

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

    // 쿠폰 정보 수정
    public void editCoupon(Long couponId, CouponRequest couponRequest) {
        Coupon coupon = couponRepository.findById(couponId).orElseThrow(EntityNotFoundException::new);

        coupon.updateCoupon(couponRequest);
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

    // 책 쿠폰 + 카테고리 쿠폰 조회
    @Transactional(readOnly = true)
    public List<CouponResponse> findBookCoupons(BookInfo bookInfo) {
        List<CouponSpecificBook> couponSpecificBooks = bookCouponRepository.findByIsbn(bookInfo.getIsbn());
        List<Coupon> isbnCoupons = couponSpecificBooks.stream()
                .map(CouponSpecificBook::getCoupon)
                .toList();

        List<CouponSpecificCategory> couponSpecificCategories = categoryCouponRepository.findByCategoryIdIn((bookInfo.getCategoryIds()));
        List<Coupon> categoryCoupons = couponSpecificCategories.stream()
                .map(CouponSpecificCategory::getCoupon)
                .toList();

        return Stream.concat(isbnCoupons.stream(), categoryCoupons.stream())
                .distinct()
                .map(CouponResponse::from)
                .toList();
    }

}
