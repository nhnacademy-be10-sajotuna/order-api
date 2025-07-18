package shop.sajotuna.order.stock.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.sajotuna.order.stock.controller.request.CreateStockRequest;
import shop.sajotuna.order.stock.controller.response.BookStockResponse;
import shop.sajotuna.order.stock.domain.BookStock;
import shop.sajotuna.order.stock.domain.Stock;
import shop.sajotuna.order.stock.exception.BookStockNotFoundException;
import shop.sajotuna.order.stock.exception.StockProcessingFailedException;
import shop.sajotuna.order.stock.repository.BookStockRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class StockService {

    private final BookStockRepository bookStockRepository;

    @Retryable(
            maxAttempts = 5,
            backoff = @Backoff(delay = 100, multiplier = 1.5),
            retryFor = OptimisticLockingFailureException.class
    )
    public void decreaseStock(String isbn, int quantity) {
        BookStock bookStock = bookStockRepository.findByIsbn(isbn)
                .orElseThrow(BookStockNotFoundException::new);
        bookStock.decreaseStock(Stock.of(quantity));

        if (bookStock.isSoldOut()) {
            // TODO: Book-API에 품절 상태 업데이트 요청
        }
    }

    @Retryable(
            maxAttempts = 5,
            backoff = @Backoff(delay = 100, multiplier = 1.5),
            retryFor = OptimisticLockingFailureException.class
    )
    public void increaseStock(String isbn, int quantity) {
        BookStock bookStock = bookStockRepository.findByIsbn(isbn)
                .orElseThrow(BookStockNotFoundException::new);
        bookStock.increaseStock(Stock.of(quantity));
    }

    public BookStockResponse createStock(String isbn, int quantity) {
        if (bookStockRepository.existsByIsbn(isbn)) {
            throw new DuplicateBookStockException();
        }
        BookStock bookStock = new BookStock(isbn, Stock.of(quantity));
        bookStockRepository.save(bookStock);
        return BookStockResponse.from(bookStock);
    }

    @Recover
    public void recoverDecreaseStock(OptimisticLockingFailureException ex, String isbn, int quantity) {
        log.error("재고 차감 최종 실패 - ISBN: {}, 수량: {}, 재시도 횟수 초과", isbn, quantity, ex);
        throw new StockProcessingFailedException(isbn, quantity);
    }

    @Recover
    public void recoverIncreaseStock(OptimisticLockingFailureException ex, String isbn, int quantity) {
        log.error("재고 증가 최종 실패 - ISBN: {}, 수량: {}, 재시도 횟수 초과", isbn, quantity, ex);
        throw new StockProcessingFailedException(isbn, quantity);
    }

    public List<BookStockResponse> createStocks(List<CreateStockRequest> createStockRequest) {
        List<String> isbns = createStockRequest.stream()
                .map(CreateStockRequest::getIsbn)
                .toList();

        // 이미 존재하는 ISBN 조회
        List<String> existingIsbns = bookStockRepository.findByIsbnIn(isbns).stream()
                .map(BookStock::getIsbn)
                .toList();

        // 중복되지 않은 것만 필터링
        List<BookStock> bookStocks = createStockRequest.stream()
                .filter(request -> !existingIsbns.contains(request.getIsbn()))
                .map(request -> new BookStock(request.getIsbn(), Stock.of(request.getStock())))
                .toList();

        List<BookStock> savedStocks = bookStockRepository.saveAll(bookStocks);

        return savedStocks.stream()
                .map(BookStockResponse::from)
                .toList();
    }

    public void updateStock(String isbn, int quantity) {
        BookStock bookStock = bookStockRepository.findByIsbn(isbn).orElseThrow(BookStockNotFoundException::new);
        bookStock.update(Stock.of(quantity));
    }
}
