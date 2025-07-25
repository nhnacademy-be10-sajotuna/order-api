package shop.sajotuna.order.stock.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import shop.sajotuna.order.stock.controller.request.CreateStockRequest;
import shop.sajotuna.order.stock.controller.request.StockRequest;
import shop.sajotuna.order.stock.controller.request.UpdateStockRequest;
import shop.sajotuna.order.stock.controller.response.BookStockResponse;
import shop.sajotuna.order.stock.exception.BookStockNotFoundException;
import shop.sajotuna.order.stock.exception.InsufficientStockException;
import shop.sajotuna.order.stock.service.DuplicateBookStockException;
import shop.sajotuna.order.stock.service.StockService;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StockController.class)
@ActiveProfiles("test")
class StockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private StockService stockService;

    @Test
    @DisplayName("재고 증가 성공")
    void increaseStock_success() throws Exception {
        // given
        StockRequest request = new StockRequest("9781234567890", 5);

        // when & then
        mockMvc.perform(put("/api/stocks/increase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(stockService).increaseStock("9781234567890", 5);
    }

    @Test
    @DisplayName("재고 증가 실패 - 책을 찾을 수 없음")
    void increaseStock_bookNotFound() throws Exception {
        // given
        StockRequest request = new StockRequest("9781234567890", 5);
        doThrow(new BookStockNotFoundException()).when(stockService)
                .increaseStock("9781234567890", 5);

        // when & then
        mockMvc.perform(put("/api/stocks/increase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(stockService).increaseStock("9781234567890", 5);
    }

    @Test
    @DisplayName("재고 증가 실패 - 유효하지 않은 요청")
    void increaseStock_invalidRequest() throws Exception {
        // given
        StockRequest request = new StockRequest("", 0); // 빈 ISBN, 0 수량

        // when & then
        mockMvc.perform(put("/api/stocks/increase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(stockService, never()).increaseStock(anyString(), anyInt());
    }

    @Test
    @DisplayName("재고 감소 성공")
    void decreaseStock_success() throws Exception {
        // given
        StockRequest request = new StockRequest("9781234567890", 3);

        // when & then
        mockMvc.perform(put("/api/stocks/decrease")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(stockService).decreaseStock("9781234567890", 3);
    }

    @Test
    @DisplayName("재고 감소 실패 - 재고 부족")
    void decreaseStock_insufficientStock() throws Exception {
        // given
        StockRequest request = new StockRequest("9781234567890", 10);
        doThrow(new InsufficientStockException()).when(stockService)
                .decreaseStock("9781234567890", 10);

        // when & then
        mockMvc.perform(put("/api/stocks/decrease")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(stockService).decreaseStock("9781234567890", 10);
    }

    @Test
    @DisplayName("재고 감소 실패 - 책을 찾을 수 없음")
    void decreaseStock_bookNotFound() throws Exception {
        // given
        StockRequest request = new StockRequest("9781234567890", 5);
        doThrow(new BookStockNotFoundException()).when(stockService)
                .decreaseStock("9781234567890", 5);

        // when & then
        mockMvc.perform(put("/api/stocks/decrease")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(stockService).decreaseStock("9781234567890", 5);
    }

    @Test
    @DisplayName("재고 생성 성공")
    void createStock_success() throws Exception {
        // given
        CreateStockRequest request = new CreateStockRequest("9781234567890", 10);
        BookStockResponse response = new BookStockResponse("9781234567890", 10);
        
        when(stockService.createStock("9781234567890", 10)).thenReturn(response);

        // when & then
        mockMvc.perform(post("/api/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isbn").value("9781234567890"))
                .andExpect(jsonPath("$.stockQuantity").value(10));

        verify(stockService).createStock("9781234567890", 10);
    }

    @Test
    @DisplayName("재고 생성 실패 - 중복된 ISBN")
    void createStock_duplicateIsbn() throws Exception {
        // given
        CreateStockRequest request = new CreateStockRequest("9781234567890", 10);
        doThrow(new DuplicateBookStockException()).when(stockService)
                .createStock("9781234567890", 10);

        // when & then
        mockMvc.perform(post("/api/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(stockService).createStock("9781234567890", 10);
    }

    @Test
    @DisplayName("재고 생성 실패 - 유효하지 않은 요청")
    void createStock_invalidRequest() throws Exception {
        // given
        CreateStockRequest request = new CreateStockRequest("", -1); // 빈 ISBN, 음수 수량

        // when & then
        mockMvc.perform(post("/api/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(stockService, never()).createStock(anyString(), anyInt());
    }

    @Test
    @DisplayName("여러 재고 생성 성공")
    void createStocks_success() throws Exception {
        // given
        List<CreateStockRequest> requests = List.of(
                new CreateStockRequest("9781234567890", 10),
                new CreateStockRequest("9781234567891", 5)
        );
        
        // StockService.createStocks returns List<BookStockResponse> but controller ignores it
        when(stockService.createStocks(anyList())).thenReturn(List.of());

        // when & then
        mockMvc.perform(post("/api/stocks/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requests)))
                .andExpect(status().isCreated());

        verify(stockService).createStocks(anyList());
    }

    @Test
    @DisplayName("재고 업데이트 성공")
    void updateStock_success() throws Exception {
        // given
        UpdateStockRequest request = new UpdateStockRequest("9781234567890", 15);

        // when & then
        mockMvc.perform(put("/api/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        verify(stockService).updateStock("9781234567890", 15);
    }

    @Test
    @DisplayName("재고 업데이트 실패 - 책을 찾을 수 없음")
    void updateStock_bookNotFound() throws Exception {
        // given
        UpdateStockRequest request = new UpdateStockRequest("9781234567890", 15);
        doThrow(new BookStockNotFoundException()).when(stockService)
                .updateStock("9781234567890", 15);

        // when & then
        mockMvc.perform(put("/api/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(stockService).updateStock("9781234567890", 15);
    }

    @Test
    @DisplayName("재고 업데이트 실패 - 유효하지 않은 요청")
    void updateStock_invalidRequest() throws Exception {
        // given
        UpdateStockRequest request = new UpdateStockRequest("", 0); // 빈 ISBN, 0 수량

        // when & then
        mockMvc.perform(put("/api/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(stockService, never()).updateStock(anyString(), anyInt());
    }
}