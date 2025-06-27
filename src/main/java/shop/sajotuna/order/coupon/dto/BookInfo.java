package shop.sajotuna.order.coupon.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class BookInfo {
    private String isbn;
    private List<Long> categoryIds;
}
