package shop.sajotuna.order.coupon.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import shop.sajotuna.order.coupon.domain.UserCouponType;
import shop.sajotuna.order.coupon.dto.response.UserCouponDetailResponse;
import shop.sajotuna.order.coupon.domain.CouponType;
import shop.sajotuna.order.coupon.domain.CouponPolicyType;
import shop.sajotuna.order.coupon.service.UserCouponService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserCouponController.class)
@ActiveProfiles("test")
class UserCouponControllerTest {

}
