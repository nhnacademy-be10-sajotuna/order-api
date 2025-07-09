package shop.sajotuna.order.stock.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.sajotuna.order.common.exception.NullValueException;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class BookStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String isbn;

    @Column(nullable = false)
    @Embedded
    private Stock stock;

    @Version
    private Long version;

    public BookStock(String isbn, Stock stock) {
        validateIsbn(isbn);
        this.isbn = isbn;
        this.stock = stock;
    }

    private void validateIsbn(String isbn) {
        if (isbn == null || isbn.isBlank()) {
            throw new NullValueException("ISBN은 null이거나 빈 문자열일 수 없습니다.");
        }
    }

    public void increaseStock(Stock  quantity) {
        this.stock = this.stock.increase(quantity);
    }

    public void decreaseStock(Stock quantity) {
        this.stock = this.stock.decrease(quantity);
    }

    public boolean isSoldOut() {
        return this.stock.isSoldOut();
    }

    public void update(Stock stock) {
        if (stock == null) {
            throw new NullValueException("재고 정보는 null일 수 없습니다.");
        }
        this.stock = stock;
    }
}
