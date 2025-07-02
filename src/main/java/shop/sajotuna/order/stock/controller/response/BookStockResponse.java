package shop.sajotuna.order.stock.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import shop.sajotuna.order.stock.domain.BookStock;

@AllArgsConstructor
@Getter
public class BookStockResponse {

    private String isbn;
    private int stockQuantity;

    public static BookStockResponse from(BookStock bookStock) {
        return new BookStockResponse(bookStock.getIsbn(), bookStock.getStock().getQuantity());
    }
}
