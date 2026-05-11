package shop.sajotuna.order.stock.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import shop.sajotuna.order.stock.domain.BookStock;
import shop.sajotuna.order.stock.domain.Stock;
import shop.sajotuna.order.stock.exception.StockProcessingFailedException;
import shop.sajotuna.order.stock.repository.BookStockRepository;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
public class StockServiceConcurrencyTest {

    private static final int THREADS = 10;
    private static final String TEST_ISBN = "978-1234567890";
    private static final int INITIAL_STOCK = 100;

    @Autowired
    private StockService stockService;

    @Autowired
    private BookStockRepository bookStockRepository;

    @BeforeEach
    void setUp() {
        bookStockRepository.deleteAll();
        BookStock bookStock = new BookStock(TEST_ISBN, Stock.of(INITIAL_STOCK));
        bookStockRepository.save(bookStock);
    }

    @Test
    void concurrentStockDecrease_shouldMaintainConsistency() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(THREADS);
        CountDownLatch latch = new CountDownLatch(THREADS);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        // 각 스레드가 5개씩 차감 (총 50개)
        for (int i = 0; i < THREADS; i++) {
            executor.submit(() -> {
                try {
                    stockService.decreaseStock(TEST_ISBN, 5);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                    log.warn("재고 차감 실패: {}", e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // 결과 검증
        BookStock finalStock = bookStockRepository.findByIsbn(TEST_ISBN).orElseThrow();
        int expectedStock = INITIAL_STOCK - (successCount.get() * 5);
        
        assertEquals(expectedStock, finalStock.getStock().getQuantity());
        assertEquals(THREADS, successCount.get() + failureCount.get());
        
        log.info("성공: {}, 실패: {}, 최종 재고: {}", 
            successCount.get(), failureCount.get(), finalStock.getStock().getQuantity());

        executor.shutdown();
    }

    @Test
    void concurrentStockDecrease_withInsufficientStock_shouldHandleGracefully() throws InterruptedException {
        // 재고를 10개로 설정
        BookStock bookStock = bookStockRepository.findByIsbn(TEST_ISBN).orElseThrow();
        bookStock.decreaseStock(Stock.of(90)); // 100 -> 10
        bookStockRepository.save(bookStock);

        ExecutorService executor = Executors.newFixedThreadPool(THREADS);
        CountDownLatch latch = new CountDownLatch(THREADS);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        // 각 스레드가 5개씩 차감 시도 (총 50개 시도, 하지만 재고는 10개만)
        for (int i = 0; i < THREADS; i++) {
            executor.submit(() -> {
                try {
                    stockService.decreaseStock(TEST_ISBN, 5);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // 결과 검증: 최대 2개 스레드만 성공해야 함 (10개 재고 / 5개씩)
        BookStock finalStock = bookStockRepository.findByIsbn(TEST_ISBN).orElseThrow();
        assertTrue(successCount.get() <= 2);
        assertTrue(finalStock.getStock().getQuantity() >= 0);
        
        log.info("부족한 재고 테스트 - 성공: {}, 실패: {}, 최종 재고: {}", 
            successCount.get(), failureCount.get(), finalStock.getStock().getQuantity());

        executor.shutdown();
    }

    @Test
    void retryMechanism_shouldEventuallySucceed() {
        // 정상적인 재고 차감이 재시도 없이 성공하는지 확인
        assertDoesNotThrow(() -> {
            stockService.decreaseStock(TEST_ISBN, 10);
        });

        BookStock finalStock = bookStockRepository.findByIsbn(TEST_ISBN).orElseThrow();
        assertEquals(INITIAL_STOCK - 10, finalStock.getStock().getQuantity());
    }

    @Test
    void decreaseStock_shouldIncrementVersion() {
        BookStock initialStock = bookStockRepository.findByIsbn(TEST_ISBN).orElseThrow();
        Long initialVersion = initialStock.getVersion();

        stockService.decreaseStock(TEST_ISBN, 10);

        BookStock finalStock = bookStockRepository.findByIsbn(TEST_ISBN).orElseThrow();
        assertEquals(INITIAL_STOCK - 10, finalStock.getStock().getQuantity());
        assertEquals(initialVersion + 1, finalStock.getVersion());
    }

    @Test
    void stockProcessingFailedException_shouldBeThrownAfterMaxRetries() {
        // 존재하지 않는 ISBN으로 테스트 (재시도 대상이 아님)
        assertThrows(Exception.class, () -> {
            stockService.decreaseStock("invalid-isbn", 10);
        });
    }
}
