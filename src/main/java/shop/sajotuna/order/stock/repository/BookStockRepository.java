package shop.sajotuna.order.stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.sajotuna.order.stock.domain.BookStock;

import java.util.List;
import java.util.Optional;

public interface BookStockRepository extends JpaRepository<BookStock, Long> {
    Optional<BookStock> findByIsbn(String isbn);

    boolean existsByIsbn(String isbn);

    boolean existsByIsbnIn(List<String> isbns);
}
