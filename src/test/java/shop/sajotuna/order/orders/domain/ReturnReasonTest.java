package shop.sajotuna.order.orders.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import shop.sajotuna.order.orders.exception.TimeOutException;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ReturnReason 도메인 테스트")
class ReturnReasonTest {

    @Nested
    @DisplayName("반품 사유별 속성 테스트")
    class ReturnReasonPropertiesTest {

        @Test
        @DisplayName("UNUSED 반품 사유의 속성을 확인한다")
        void unused_properties() {
            // given
            ReturnReason returnReason = ReturnReason.UNUSED;

            // when & then
            assertThat(returnReason.getMaxDays()).isEqualTo(10);
            assertThat(returnReason.isDeductShippingFee()).isTrue();
        }

        @Test
        @DisplayName("DAMAGED 반품 사유의 속성을 확인한다")
        void damaged_properties() {
            // given
            ReturnReason returnReason = ReturnReason.DAMAGED;

            // when & then
            assertThat(returnReason.getMaxDays()).isEqualTo(30);
            assertThat(returnReason.isDeductShippingFee()).isFalse();
        }

        @Test
        @DisplayName("DEFECTIVE 반품 사유의 속성을 확인한다")
        void defective_properties() {
            // given
            ReturnReason returnReason = ReturnReason.DEFECTIVE;

            // when & then
            assertThat(returnReason.getMaxDays()).isEqualTo(30);
            assertThat(returnReason.isDeductShippingFee()).isFalse();
        }
    }

    @Nested
    @DisplayName("반품 기간 검증 테스트 - UNUSED")
    class UnusedReturnPeriodValidationTest {

        @Test
        @DisplayName("UNUSED - 발송일로부터 1일 후 반품 요청 시 성공한다")
        void unused_validateReturnPeriod_1dayAfter_success() {
            // given
            ReturnReason returnReason = ReturnReason.UNUSED;
            LocalDateTime shippingDate = LocalDateTime.now().minusDays(1);

            // when & then - 예외가 발생하지 않음
            returnReason.validateReturnPeriod(shippingDate, LocalDateTime.now());
        }

        @Test
        @DisplayName("UNUSED - 발송일로부터 10일 후 반품 요청 시 성공한다")
        void unused_validateReturnPeriod_10daysAfter_success() {
            // given
            ReturnReason returnReason = ReturnReason.UNUSED;
            LocalDateTime shippingDate = LocalDateTime.now().minusDays(10);

            // when & then - 예외가 발생하지 않음
            returnReason.validateReturnPeriod(shippingDate, LocalDateTime.now());
        }

        @Test
        @DisplayName("UNUSED - 발송일로부터 11일 후 반품 요청 시 예외가 발생한다")
        void unused_validateReturnPeriod_11daysAfter_throwsException() {
            // given
            ReturnReason returnReason = ReturnReason.UNUSED;
            LocalDateTime shippingDate = LocalDateTime.now().minusDays(11);

            // when & then
            assertThatThrownBy(() -> returnReason.validateReturnPeriod(shippingDate, LocalDateTime.now()))
                    .isInstanceOf(TimeOutException.class);
        }

        @Test
        @DisplayName("UNUSED - 발송일 당일 반품 요청 시 성공한다")
        void unused_validateReturnPeriod_sameDay_success() {
            // given
            ReturnReason returnReason = ReturnReason.UNUSED;
            LocalDateTime shippingDate = LocalDateTime.now();

            // when & then - 예외가 발생하지 않음
            returnReason.validateReturnPeriod(shippingDate, LocalDateTime.now());
        }
    }

    @Nested
    @DisplayName("반품 기간 검증 테스트 - DAMAGED")
    class DamagedReturnPeriodValidationTest {

        @Test
        @DisplayName("DAMAGED - 발송일로부터 1일 후 반품 요청 시 성공한다")
        void damaged_validateReturnPeriod_1dayAfter_success() {
            // given
            ReturnReason returnReason = ReturnReason.DAMAGED;
            LocalDateTime shippingDate = LocalDateTime.now().minusDays(1);

            // when & then - 예외가 발생하지 않음
            returnReason.validateReturnPeriod(shippingDate, LocalDateTime.now());
        }

        @Test
        @DisplayName("DAMAGED - 발송일로부터 30일 후 반품 요청 시 성공한다")
        void damaged_validateReturnPeriod_30daysAfter_success() {
            // given
            ReturnReason returnReason = ReturnReason.DAMAGED;
            LocalDateTime shippingDate = LocalDateTime.now().minusDays(30);

            // when & then - 예외가 발생하지 않음
            returnReason.validateReturnPeriod(shippingDate, LocalDateTime.now());
        }

        @Test
        @DisplayName("DAMAGED - 발송일로부터 31일 후 반품 요청 시 예외가 발생한다")
        void damaged_validateReturnPeriod_31daysAfter_throwsException() {
            // given
            ReturnReason returnReason = ReturnReason.DAMAGED;
            LocalDateTime shippingDate = LocalDateTime.now().minusDays(31);

            // when & then
            assertThatThrownBy(() -> returnReason.validateReturnPeriod(shippingDate, LocalDateTime.now()))
                    .isInstanceOf(TimeOutException.class);
        }

        @Test
        @DisplayName("DAMAGED - 발송일로부터 15일 후 반품 요청 시 성공한다")
        void damaged_validateReturnPeriod_15daysAfter_success() {
            // given
            ReturnReason returnReason = ReturnReason.DAMAGED;
            LocalDateTime shippingDate = LocalDateTime.now().minusDays(15);

            // when & then - 예외가 발생하지 않음
            returnReason.validateReturnPeriod(shippingDate, LocalDateTime.now());
        }
    }

    @Nested
    @DisplayName("반품 기간 검증 테스트 - DEFECTIVE")
    class DefectiveReturnPeriodValidationTest {

        @Test
        @DisplayName("DEFECTIVE - 발송일로부터 1일 후 반품 요청 시 성공한다")
        void defective_validateReturnPeriod_1dayAfter_success() {
            // given
            ReturnReason returnReason = ReturnReason.DEFECTIVE;
            LocalDateTime shippingDate = LocalDateTime.now().minusDays(1);

            // when & then - 예외가 발생하지 않음
            returnReason.validateReturnPeriod(shippingDate, LocalDateTime.now());
        }

        @Test
        @DisplayName("DEFECTIVE - 발송일로부터 30일 후 반품 요청 시 성공한다")
        void defective_validateReturnPeriod_30daysAfter_success() {
            // given
            ReturnReason returnReason = ReturnReason.DEFECTIVE;
            LocalDateTime shippingDate = LocalDateTime.now().minusDays(30);

            // when & then - 예외가 발생하지 않음
            returnReason.validateReturnPeriod(shippingDate, LocalDateTime.now());
        }

        @Test
        @DisplayName("DEFECTIVE - 발송일로부터 31일 후 반품 요청 시 예외가 발생한다")
        void defective_validateReturnPeriod_31daysAfter_throwsException() {
            // given
            ReturnReason returnReason = ReturnReason.DEFECTIVE;
            LocalDateTime shippingDate = LocalDateTime.now().minusDays(31);

            // when & then
            assertThatThrownBy(() -> returnReason.validateReturnPeriod(shippingDate, LocalDateTime.now()))
                    .isInstanceOf(TimeOutException.class);
        }

        @Test
        @DisplayName("DEFECTIVE - 발송일로부터 20일 후 반품 요청 시 성공한다")
        void defective_validateReturnPeriod_20daysAfter_success() {
            // given
            ReturnReason returnReason = ReturnReason.DEFECTIVE;
            LocalDateTime shippingDate = LocalDateTime.now().minusDays(20);

            // when & then - 예외가 발생하지 않음
            returnReason.validateReturnPeriod(shippingDate, LocalDateTime.now());
        }
    }

    @Nested
    @DisplayName("경계값 테스트")
    class BoundaryValueTest {

        @Test
        @DisplayName("UNUSED - 발송일로부터 정확히 10일 후 23시간 59분 59초에 반품 요청 시 성공한다")
        void unused_validateReturnPeriod_exactly10Days_success() {
            // given
            ReturnReason returnReason = ReturnReason.UNUSED;
            LocalDateTime shippingDate = LocalDateTime.now().minusDays(10).minusHours(23).minusMinutes(59).minusSeconds(59);

            // when & then - 예외가 발생하지 않음
            returnReason.validateReturnPeriod(shippingDate, LocalDateTime.now());
        }

        @Test
        @DisplayName("DAMAGED/DEFECTIVE - 발송일로부터 정확히 30일 후 23시간 59분 59초에 반품 요청 시 성공한다")
        void damaged_validateReturnPeriod_exactly30Days_success() {
            // given
            ReturnReason returnReason = ReturnReason.DAMAGED;
            LocalDateTime shippingDate = LocalDateTime.now().minusDays(30).minusHours(23).minusMinutes(59).minusSeconds(59);

            // when & then - 예외가 발생하지 않음
            returnReason.validateReturnPeriod(shippingDate, LocalDateTime.now());
        }

        @Test
        @DisplayName("미래 발송일에 대해 반품 요청 시 성공한다")
        void validateReturnPeriod_futureShippingDate_success() {
            // given
            ReturnReason returnReason = ReturnReason.UNUSED;
            LocalDateTime futureShippingDate = LocalDateTime.now().plusDays(1);

            // when & then - 예외가 발생하지 않음 (음수 일수는 0보다 작으므로 통과)
            returnReason.validateReturnPeriod(futureShippingDate, LocalDateTime.now());
        }
    }

    @Nested
    @DisplayName("시간 계산 정확성 테스트")
    class TimeCalculationAccuracyTest {

        @Test
        @DisplayName("시간 단위는 무시하고 일 단위로만 계산한다")
        void validateReturnPeriod_ignoresTimeComponent() {
            // given
            ReturnReason returnReason = ReturnReason.UNUSED;
            LocalDateTime now = LocalDateTime.of(2023, 12, 15, 14, 30, 45);
            LocalDateTime shippingDate = LocalDateTime.of(2023, 12, 5, 8, 15, 20); // 10일 전, 다른 시간

            // when & then - 일 단위로만 계산하므로 10일로 인식되어 성공
            returnReason.validateReturnPeriod(shippingDate, now);
        }

        @Test
        @DisplayName("월을 넘나드는 경우에도 정확히 계산한다")
        void validateReturnPeriod_acrossMonths() {
            // given
            ReturnReason returnReason = ReturnReason.DAMAGED;
            LocalDateTime now = LocalDateTime.of(2024, 1, 15, 10, 0, 0);
            LocalDateTime shippingDate = LocalDateTime.of(2023, 12, 17, 10, 0, 0); // 29일 전

            // when & then - 29일이므로 성공
            returnReason.validateReturnPeriod(shippingDate, now);
        }
    }

    @Nested
    @DisplayName("배송비 차감 여부 테스트")
    class ShippingFeeDeductionTest {

        @Test
        @DisplayName("배송비 차감 사유들을 확인한다")
        void checkShippingFeeDeduction() {
            // when & then
            assertThat(ReturnReason.UNUSED.isDeductShippingFee()).isTrue();
            assertThat(ReturnReason.DAMAGED.isDeductShippingFee()).isFalse();
            assertThat(ReturnReason.DEFECTIVE.isDeductShippingFee()).isFalse();
        }
    }
}