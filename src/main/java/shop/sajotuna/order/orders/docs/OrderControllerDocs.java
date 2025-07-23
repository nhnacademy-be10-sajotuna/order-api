package shop.sajotuna.order.orders.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import shop.sajotuna.order.orders.controller.dto.response.OrderDetailResponse;
import shop.sajotuna.order.orders.controller.dto.response.OrderFormResponse;
import shop.sajotuna.order.orders.controller.dto.response.OrderInfoResponse;

@Tag(name = "주문 API", description = "주문 조회 및 관리 API")
public interface OrderControllerDocs {

    @Operation(
        summary = "주문 기본 정보 조회",
        description = "주문 번호로 주문의 기본 정보를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "주문 정보 조회 성공",
            content = @Content(schema = @Schema(implementation = OrderInfoResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "주문을 찾을 수 없음",
            content = @Content
        )
    })
    ResponseEntity<OrderInfoResponse> getOrderInfo(
        @Parameter(description = "주문 번호", example = "ORD20241122001") 
        String orderNumber
    );

    @Operation(
        summary = "주문 상세 정보 조회",
        description = "주문 ID로 주문의 상세 정보를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "주문 상세 정보 조회 성공",
            content = @Content(schema = @Schema(implementation = OrderDetailResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "주문을 찾을 수 없음",
            content = @Content
        )
    })
    ResponseEntity<OrderDetailResponse> getOrder(
        @Parameter(description = "주문 ID", example = "1") 
        Long orderId
    );

    @Operation(
        summary = "비회원 주문 상세 정보 조회",
        description = "비회원이 주문 번호로 주문의 상세 정보를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "비회원 주문 상세 정보 조회 성공",
            content = @Content(schema = @Schema(implementation = OrderDetailResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "주문을 찾을 수 없음",
            content = @Content
        )
    })
    ResponseEntity<OrderDetailResponse> getGuestOrder(
        @Parameter(description = "주문 번호", example = "ORD20241122001") 
        String orderNumber
    );

    @Operation(
        summary = "회원 주문 내역 조회",
        description = "회원의 모든 주문 내역을 페이징하여 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "회원 주문 내역 조회 성공",
            content = @Content(schema = @Schema(implementation = Page.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 파라미터",
            content = @Content
        )
    })
    ResponseEntity<Page<OrderInfoResponse>> getUserOrder(
        @Parameter(description = "사용자 ID", example = "123", required = true) 
        Long userId,
        @Parameter(description = "페이징 정보") 
        Pageable pageable
    );

    @Operation(
        summary = "주문 폼 정보 조회",
        description = "주문 작성을 위한 폼 정보를 조회합니다. 회원인 경우 기본 정보가 포함됩니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "주문 폼 정보 조회 성공",
            content = @Content(schema = @Schema(implementation = OrderFormResponse.class))
        )
    })
    ResponseEntity<OrderFormResponse> getOrderForm(
        @Parameter(description = "사용자 ID (선택사항, 회원인 경우 제공)", example = "123") 
        Long userId
    );
}