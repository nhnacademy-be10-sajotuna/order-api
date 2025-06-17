package shop.sajotuna.order.coupon.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import shop.sajotuna.order.coupon.dto.BirthdayUserResponse;

import java.util.List;

@FeignClient(name = "account-api")
public interface BirthdayClient {
    @GetMapping("/api/users/birth")
    List<BirthdayUserResponse> getBirthdayUsers();

}
