package shop.sajotuna.order.coupon.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public class BookInfo {
    private String isbn;
    private Set<Long> categoryIds;
}
