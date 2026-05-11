package shop.sajotuna.order.stock.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import shop.sajotuna.order.stock.controller.request.CreateStockRequest;
import shop.sajotuna.order.stock.controller.response.BookStockResponse;
import shop.sajotuna.order.stock.domain.BookStock;
import shop.sajotuna.order.stock.domain.Stock;
import shop.sajotuna.order.stock.exception.BookStockNotFoundException;
import shop.sajotuna.order.stock.exception.InsufficientStockException;
import shop.sajotuna.order.stock.exception.StockProcessingFailedException;
import shop.sajotuna.order.stock.repository.BookStockRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private BookStockRepository bookStockRepository;

    @InjectMocks
    private StockService stockService;

    @Test
    @DisplayName("decrease stock succeeds with atomic update")
    void decreaseStock_success() {
        String isbn = "9781234567890";
        int quantity = 5;

        when(bookStockRepository.decreaseStockAtomically(isbn, quantity)).thenReturn(1);

        stockService.decreaseStock(isbn, quantity);

        verify(bookStockRepository).decreaseStockAtomically(isbn, quantity);
        verify(bookStockRepository, never()).existsByIsbn(isbn);
    }

    @Test
    @DisplayName("decrease stock fails when stock row does not exist")
    void decreaseStock_bookNotFound() {
        String isbn = "9781234567890";
        int quantity = 5;

        when(bookStockRepository.decreaseStockAtomically(isbn, quantity)).thenReturn(0);
        when(bookStockRepository.existsByIsbn(isbn)).thenReturn(false);

        assertThatThrownBy(() -> stockService.decreaseStock(isbn, quantity))
                .isInstanceOf(BookStockNotFoundException.class);

        verify(bookStockRepository).decreaseStockAtomically(isbn, quantity);
        verify(bookStockRepository).existsByIsbn(isbn);
    }

    @Test
    @DisplayName("decrease stock fails when quantity is insufficient")
    void decreaseStock_insufficientStock() {
        String isbn = "9781234567890";
        int quantity = 5;

        when(bookStockRepository.decreaseStockAtomically(isbn, quantity)).thenReturn(0);
        when(bookStockRepository.existsByIsbn(isbn)).thenReturn(true);

        assertThatThrownBy(() -> stockService.decreaseStock(isbn, quantity))
                .isInstanceOf(InsufficientStockException.class);

        verify(bookStockRepository).decreaseStockAtomically(isbn, quantity);
        verify(bookStockRepository).existsByIsbn(isbn);
    }

    @Test
    @DisplayName("increase stock succeeds")
    void increaseStock_success() {
        String isbn = "9781234567890";
        int quantity = 5;
        BookStock bookStock = createBookStock(isbn, 10);

        when(bookStockRepository.findByIsbn(isbn)).thenReturn(Optional.of(bookStock));

        stockService.increaseStock(isbn, quantity);

        verify(bookStockRepository).findByIsbn(isbn);
    }

    @Test
    @DisplayName("increase stock fails when stock row does not exist")
    void increaseStock_bookNotFound() {
        String isbn = "9781234567890";
        int quantity = 5;

        when(bookStockRepository.findByIsbn(isbn)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> stockService.increaseStock(isbn, quantity))
                .isInstanceOf(BookStockNotFoundException.class);

        verify(bookStockRepository).findByIsbn(isbn);
    }

    @Test
    @DisplayName("create stock succeeds")
    void createStock_success() {
        String isbn = "9781234567890";
        int quantity = 10;
        BookStock savedBookStock = createBookStock(isbn, quantity);

        when(bookStockRepository.existsByIsbn(isbn)).thenReturn(false);
        when(bookStockRepository.save(any(BookStock.class))).thenReturn(savedBookStock);

        BookStockResponse result = stockService.createStock(isbn, quantity);

        assertThat(result).isNotNull();
        assertThat(result.getIsbn()).isEqualTo(isbn);
        assertThat(result.getStockQuantity()).isEqualTo(quantity);

        verify(bookStockRepository).existsByIsbn(isbn);
        verify(bookStockRepository).save(any(BookStock.class));
    }

    @Test
    @DisplayName("create stock fails with duplicate ISBN")
    void createStock_duplicateIsbn() {
        String isbn = "9781234567890";
        int quantity = 10;

        when(bookStockRepository.existsByIsbn(isbn)).thenReturn(true);

        assertThatThrownBy(() -> stockService.createStock(isbn, quantity))
                .isInstanceOf(DuplicateBookStockException.class);

        verify(bookStockRepository).existsByIsbn(isbn);
        verify(bookStockRepository, never()).save(any(BookStock.class));
    }

    @Test
    @DisplayName("create multiple stocks succeeds")
    void createStocks_success() {
        CreateStockRequest request1 = createStockRequest("9781234567890", 10);
        CreateStockRequest request2 = createStockRequest("9781234567891", 5);
        List<CreateStockRequest> requests = List.of(request1, request2);

        List<String> isbns = List.of("9781234567890", "9781234567891");
        when(bookStockRepository.findByIsbnIn(isbns)).thenReturn(List.of());

        BookStock savedStock1 = createBookStock("9781234567890", 10);
        BookStock savedStock2 = createBookStock("9781234567891", 5);
        when(bookStockRepository.saveAll(any())).thenReturn(List.of(savedStock1, savedStock2));

        List<BookStockResponse> result = stockService.createStocks(requests);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getIsbn()).isEqualTo("9781234567890");
        assertThat(result.get(1).getIsbn()).isEqualTo("9781234567891");

        verify(bookStockRepository).findByIsbnIn(isbns);
        verify(bookStockRepository).saveAll(any());
    }

    @Test
    @DisplayName("update stock succeeds")
    void updateStock_success() {
        String isbn = "9781234567890";
        int quantity = 15;
        BookStock bookStock = createBookStock(isbn, 10);

        when(bookStockRepository.findByIsbn(isbn)).thenReturn(Optional.of(bookStock));

        stockService.updateStock(isbn, quantity);

        verify(bookStockRepository).findByIsbn(isbn);
    }

    @Test
    @DisplayName("update stock fails when stock row does not exist")
    void updateStock_bookNotFound() {
        String isbn = "9781234567890";
        int quantity = 15;

        when(bookStockRepository.findByIsbn(isbn)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> stockService.updateStock(isbn, quantity))
                .isInstanceOf(BookStockNotFoundException.class);

        verify(bookStockRepository).findByIsbn(isbn);
    }

    @Test
    @DisplayName("recover increase stock throws stock processing failed exception")
    void recoverIncreaseStock_optimisticLockingFailure() {
        String isbn = "9781234567890";
        int quantity = 5;
        ObjectOptimisticLockingFailureException exception =
                new ObjectOptimisticLockingFailureException("test", new RuntimeException());

        assertThatThrownBy(() -> stockService.recoverIncreaseStock(exception, isbn, quantity))
                .isInstanceOf(StockProcessingFailedException.class);
    }

    private BookStock createBookStock(String isbn, int quantity) {
        return new BookStock(isbn, Stock.of(quantity));
    }

    private CreateStockRequest createStockRequest(String isbn, int stock) {
        CreateStockRequest request = mock(CreateStockRequest.class);
        when(request.getIsbn()).thenReturn(isbn);
        when(request.getStock()).thenReturn(stock);
        return request;
    }
}
