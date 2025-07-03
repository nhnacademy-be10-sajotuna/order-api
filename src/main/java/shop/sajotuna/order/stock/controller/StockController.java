package shop.sajotuna.order.stock.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.sajotuna.order.stock.controller.request.CreateStockRequest;
import shop.sajotuna.order.stock.controller.request.StockRequest;
import shop.sajotuna.order.stock.controller.response.BookStockResponse;
import shop.sajotuna.order.stock.service.StockService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stocks")
public class StockController {

    private final StockService stockService;

    @PutMapping("/increase")
    public ResponseEntity<Void> increaseStock(@RequestBody @Valid StockRequest stockRequest) {
        stockService.increaseStock(stockRequest.getIsbn(), stockRequest.getQuantity());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/decrease")
    public ResponseEntity<Void> decreaseStock(@RequestBody @Valid StockRequest decreaseStockRequest) {
        stockService.decreaseStock(decreaseStockRequest.getIsbn(), decreaseStockRequest.getQuantity());
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<BookStockResponse> createStock(@RequestBody @Valid CreateStockRequest createStockRequest) {
        return ResponseEntity.ok(stockService.createStock(createStockRequest.getIsbn(), createStockRequest.getQuantity()));
    }
}
