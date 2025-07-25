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
import shop.sajotuna.order.stock.exception.StockProcessingFailedException;
import shop.sajotuna.order.stock.repository.BookStockRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private BookStockRepository bookStockRepository;

    @InjectMocks
    private StockService stockService;

    @Test
    @DisplayName("재고 감소 성공")
    void decreaseStock_success() {
        // given
        String isbn = "9781234567890";
        int quantity = 5;
        BookStock bookStock = createBookStock(isbn, 10);
        
        when(bookStockRepository.findByIsbn(isbn)).thenReturn(Optional.of(bookStock));

        // when
        stockService.decreaseStock(isbn, quantity);

        // then
        verify(bookStockRepository).findByIsbn(isbn);
    }

    @Test
    @DisplayName("재고 감소 실패 - 책을 찾을 수 없음")
    void decreaseStock_bookNotFound() {
        // given
        String isbn = "9781234567890";
        int quantity = 5;
        
        when(bookStockRepository.findByIsbn(isbn)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> stockService.decreaseStock(isbn, quantity))
                .isInstanceOf(BookStockNotFoundException.class);
                
        verify(bookStockRepository).findByIsbn(isbn);
    }

    @Test
    @DisplayName("재고 증가 성공")
    void increaseStock_success() {
        // given
        String isbn = "9781234567890";
        int quantity = 5;
        BookStock bookStock = createBookStock(isbn, 10);
        
        when(bookStockRepository.findByIsbn(isbn)).thenReturn(Optional.of(bookStock));

        // when
        stockService.increaseStock(isbn, quantity);

        // then
        verify(bookStockRepository).findByIsbn(isbn);
    }

    @Test
    @DisplayName("재고 증가 실패 - 책을 찾을 수 없음")
    void increaseStock_bookNotFound() {
        // given
        String isbn = "9781234567890";
        int quantity = 5;
        
        when(bookStockRepository.findByIsbn(isbn)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> stockService.increaseStock(isbn, quantity))
                .isInstanceOf(BookStockNotFoundException.class);
                
        verify(bookStockRepository).findByIsbn(isbn);
    }

    @Test
    @DisplayName("재고 생성 성공")
    void createStock_success() {
        // given
        String isbn = "9781234567890";
        int quantity = 10;
        BookStock savedBookStock = createBookStock(isbn, quantity);
        
        when(bookStockRepository.existsByIsbn(isbn)).thenReturn(false);
        when(bookStockRepository.save(any(BookStock.class))).thenReturn(savedBookStock);

        // when
        BookStockResponse result = stockService.createStock(isbn, quantity);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getIsbn()).isEqualTo(isbn);
        assertThat(result.getStockQuantity()).isEqualTo(quantity);
        
        verify(bookStockRepository).existsByIsbn(isbn);
        verify(bookStockRepository).save(any(BookStock.class));
    }

    @Test
    @DisplayName("재고 생성 실패 - 중복된 ISBN")
    void createStock_duplicateIsbn() {
        // given
        String isbn = "9781234567890";
        int quantity = 10;
        
        when(bookStockRepository.existsByIsbn(isbn)).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> stockService.createStock(isbn, quantity))
                .isInstanceOf(DuplicateBookStockException.class);
                
        verify(bookStockRepository).existsByIsbn(isbn);
        verify(bookStockRepository, never()).save(any(BookStock.class));
    }

    @Test
    @DisplayName("여러 재고 생성 성공")
    void createStocks_success() {
        // given
        CreateStockRequest request1 = createStockRequest("9781234567890", 10);
        CreateStockRequest request2 = createStockRequest("9781234567891", 5);
        List<CreateStockRequest> requests = List.of(request1, request2);
        
        List<String> isbns = List.of("9781234567890", "9781234567891");
        when(bookStockRepository.findByIsbnIn(isbns)).thenReturn(List.of());
        
        BookStock savedStock1 = createBookStock("9781234567890", 10);
        BookStock savedStock2 = createBookStock("9781234567891", 5);
        when(bookStockRepository.saveAll(any())).thenReturn(List.of(savedStock1, savedStock2));

        // when
        List<BookStockResponse> result = stockService.createStocks(requests);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getIsbn()).isEqualTo("9781234567890");
        assertThat(result.get(1).getIsbn()).isEqualTo("9781234567891");
        
        verify(bookStockRepository).findByIsbnIn(isbns);
        verify(bookStockRepository).saveAll(any());
    }

    @Test
    @DisplayName("재고 업데이트 성공")
    void updateStock_success() {
        // given
        String isbn = "9781234567890";
        int quantity = 15;
        BookStock bookStock = createBookStock(isbn, 10);
        
        when(bookStockRepository.findByIsbn(isbn)).thenReturn(Optional.of(bookStock));

        // when
        stockService.updateStock(isbn, quantity);

        // then
        verify(bookStockRepository).findByIsbn(isbn);
    }

    @Test
    @DisplayName("재고 업데이트 실패 - 책을 찾을 수 없음")
    void updateStock_bookNotFound() {
        // given
        String isbn = "9781234567890";
        int quantity = 15;
        
        when(bookStockRepository.findByIsbn(isbn)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> stockService.updateStock(isbn, quantity))
                .isInstanceOf(BookStockNotFoundException.class);
                
        verify(bookStockRepository).findByIsbn(isbn);
    }

    @Test
    @DisplayName("재고 감소 복구 메서드 - OptimisticLockingFailureException 발생")
    void recoverDecreaseStock_optimisticLockingFailure() {
        // given
        String isbn = "9781234567890";
        int quantity = 5;
        ObjectOptimisticLockingFailureException exception = new ObjectOptimisticLockingFailureException("test", new RuntimeException());

        // when & then
        assertThatThrownBy(() -> stockService.recoverDecreaseStock(exception, isbn, quantity))
                .isInstanceOf(StockProcessingFailedException.class);
    }

    @Test
    @DisplayName("재고 증가 복구 메서드 - OptimisticLockingFailureException 발생")
    void recoverIncreaseStock_optimisticLockingFailure() {
        // given
        String isbn = "9781234567890";
        int quantity = 5;
        ObjectOptimisticLockingFailureException exception = new ObjectOptimisticLockingFailureException("test", new RuntimeException());

        // when & then
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