package shop.sajotuna.order.coupon.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public class BookInfo {
    private String isbn;
    private Set<Long> categoryIds;
}
