package shop.sajotuna.order.stock.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.sajotuna.order.common.exception.NullValueException;
import shop.sajotuna.order.stock.exception.InsufficientStockException;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Stock {

    private int quantity;

    private Stock(int quantity) {
        validateQuantity(quantity);
        this.quantity = quantity;
    }

    public static Stock of(int quantity) {
        return new Stock(quantity);
    }

    private void validateQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("재고 수량은 0 이상이어야 합니다.");
        }
    }

    protected Stock increase(Stock stock) {
        if (stock == null) {
            throw new NullValueException("증가할 재고는 null일 수 없습니다.");
        }
        return Stock.of(this.quantity + stock.getQuantity());
    }

    protected Stock decrease(Stock stock) {
        if (stock == null) {
            throw new NullValueException("감소할 재고는 null일 수 없습니다.");
        }
        if (isSoldOut()) {
            throw new InsufficientStockException();
        }
        if (stock.getQuantity() <= 0) {
            throw new IllegalArgumentException("감소할 수량은 0 보다 커야 합니다.");
        }
        if (stock.getQuantity() > this.quantity) {
            throw new InsufficientStockException();
        }
        return Stock.of(this.quantity - stock.getQuantity());
    }

    protected boolean isSoldOut() {
        return this.quantity == 0;
    }
}
