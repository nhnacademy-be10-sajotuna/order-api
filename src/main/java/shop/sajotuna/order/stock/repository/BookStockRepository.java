package shop.sajotuna.order.stock.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.sajotuna.order.stock.domain.BookStock;

import java.util.List;
import java.util.Optional;

public interface BookStockRepository extends JpaRepository<BookStock, Long> {
    Optional<BookStock> findByIsbn(String isbn);

    boolean existsByIsbn(String isbn);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            update BookStock bs
            set bs.stock.quantity = bs.stock.quantity - :quantity,
                bs.version = bs.version + 1
            where bs.isbn = :isbn
              and bs.stock.quantity >= :quantity
            """)
    int decreaseStockAtomically(@Param("isbn") String isbn, @Param("quantity") int quantity);

    List<BookStock> findByIsbnIn(List<String> isbns);
}
