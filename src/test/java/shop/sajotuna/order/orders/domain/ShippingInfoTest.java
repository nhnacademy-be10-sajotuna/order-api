package shop.sajotuna.order.orders.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.springframework.cglib.core.Local;
import org.springframework.test.util.ReflectionTestUtils;
import shop.sajotuna.order.common.exception.NullValueException;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ShippingInfo 도메인 테스트")
class ShippingInfoTest {

    @Nested
    @DisplayName("배송 정보 생성 테스트")
    class CreateShippingInfoTest {

        @Test
        @DisplayName("유효한 정보로 배송 정보를 생성한다")
        void create_validInfo_success() {
            // given
            String recipientName = "김수령";
            String recipientPhoneNumber = "010-9876-5432";
            String recipientEmail = "recipient@example.com";
            String recipientAddress = "서울시 강남구 테헤란로 123";
            LocalDate expectedDeliveryDate = LocalDate.now().plusDays(3);

            // when
            ShippingInfo shippingInfo = ShippingInfo.create(
                    recipientName, recipientPhoneNumber, recipientEmail, recipientAddress, expectedDeliveryDate
            );

            // then
            assertThat(shippingInfo).isNotNull();
            assertThat(shippingInfo.getRecipientName()).isEqualTo(recipientName);
            assertThat(shippingInfo.getRecipientPhoneNumber()).isEqualTo(recipientPhoneNumber);
            assertThat(shippingInfo.getRecipientEmail()).isEqualTo(recipientEmail);
            assertThat(shippingInfo.getRecipientAddress()).isEqualTo(recipientAddress);
            assertThat(shippingInfo.getExpectedDeliveryDate()).isEqualTo(expectedDeliveryDate);
            assertThat(shippingInfo.getShippingStartDate()).isNull();
            assertThat(shippingInfo.getShippingEndDate()).isNull();
        }

        @Test
        @DisplayName("예상 배송일이 null인 경우 기본값으로 2일 후 설정")
        void create_nullExpectedDeliveryDate_setsDefault() {
            // given
            // when
            ShippingInfo shippingInfo = ShippingInfo.create(
                    "김수령", "010-9876-5432", "recipient@example.com", "서울시 강남구 테헤란로 123", null
            );

            // then
            assertThat(shippingInfo.getExpectedDeliveryDate()).isEqualTo(LocalDate.now().plusDays(2));
        }

        @Test
        @DisplayName("영문 수령인 이름으로 배송 정보를 생성한다")
        void create_englishRecipientName_success() {
            // given
            String recipientName = "John Smith";

            // when
            ShippingInfo shippingInfo = ShippingInfo.create(
                    recipientName, "010-9876-5432", "john@example.com", "Seoul Gangnam-gu", LocalDate.now().plusDays(3)
            );

            // then
            assertThat(shippingInfo.getRecipientName()).isEqualTo(recipientName);
        }
    }

    @Nested
    @DisplayName("수령인 이름 검증 테스트")
    class RecipientNameValidationTest {

        @Test
        @DisplayName("null 수령인 이름으로 생성 시 예외가 발생한다")
        void create_nullRecipientName_throwsException() {
            // when & then
            assertThatThrownBy(() -> ShippingInfo.create(
                    null, "010-9876-5432", "recipient@example.com", "서울시 강남구", LocalDate.now().plusDays(3)
            ))
                    .isInstanceOf(NullValueException.class)
                    .hasMessage("이름은 필수입니다.");
        }

        @Test
        @DisplayName("빈 문자열 수령인 이름으로 생성 시 예외가 발생한다")
        void create_emptyRecipientName_throwsException() {
            // when & then
            assertThatThrownBy(() -> ShippingInfo.create(
                    "", "010-9876-5432", "recipient@example.com", "서울시 강남구", LocalDate.now().plusDays(3)
            ))
                    .isInstanceOf(NullValueException.class)
                    .hasMessage("이름은 필수입니다.");
        }

        @Test
        @DisplayName("1글자 수령인 이름으로 생성 시 예외가 발생한다")
        void create_singleCharacterRecipientName_throwsException() {
            // when & then
            assertThatThrownBy(() -> ShippingInfo.create(
                    "김", "010-9876-5432", "recipient@example.com", "서울시 강남구", LocalDate.now().plusDays(3)
            ))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("이름은 최소 2자 이상이어야 합니다.");
        }

        @Test
        @DisplayName("51글자 수령인 이름으로 생성 시 예외가 발생한다")
        void create_tooLongRecipientName_throwsException() {
            // given
            String longName = "a".repeat(51);

            // when & then
            assertThatThrownBy(() -> ShippingInfo.create(
                    longName, "010-9876-5432", "recipient@example.com", "서울시 강남구", LocalDate.now().plusDays(3)
            ))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("이름은 50자를 초과할 수 없습니다.");
        }

        @Test
        @DisplayName("특수문자가 포함된 수령인 이름으로 생성 시 예외가 발생한다")
        void create_recipientNameWithSpecialCharacters_throwsException() {
            // when & then
            assertThatThrownBy(() -> ShippingInfo.create(
                    "김수령123", "010-9876-5432", "recipient@example.com", "서울시 강남구", LocalDate.now().plusDays(3)
            ))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("이름은 한글, 영문, 공백만 입력 가능합니다.");
        }
    }

    @Nested
    @DisplayName("수령인 전화번호 검증 테스트")
    class RecipientPhoneNumberValidationTest {

        @Test
        @DisplayName("null 수령인 전화번호로 생성 시 예외가 발생한다")
        void create_nullRecipientPhoneNumber_throwsException() {
            // when & then
            assertThatThrownBy(() -> ShippingInfo.create(
                    "김수령", null, "recipient@example.com", "서울시 강남구", LocalDate.now().plusDays(3)
            ))
                    .isInstanceOf(NullValueException.class)
                    .hasMessage("전화번호는 필수입니다.");
        }

        @Test
        @DisplayName("잘못된 형식의 수령인 전화번호로 생성 시 예외가 발생한다")
        void create_invalidRecipientPhoneNumber_throwsException() {
            // when & then
            assertThatThrownBy(() -> ShippingInfo.create(
                    "김수령", "010-123-5678", "recipient@example.com", "서울시 강남구", LocalDate.now().plusDays(3)
            ))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("전화번호는 010-1234-5678 형식이어야 합니다.");
        }
    }

    @Nested
    @DisplayName("수령인 이메일 검증 테스트")
    class RecipientEmailValidationTest {

        @Test
        @DisplayName("null 수령인 이메일로 생성 시 예외가 발생한다")
        void create_nullRecipientEmail_throwsException() {
            // when & then
            assertThatThrownBy(() -> ShippingInfo.create(
                    "김수령", "010-9876-5432", null, "서울시 강남구", LocalDate.now().plusDays(3)
            ))
                    .isInstanceOf(NullValueException.class)
                    .hasMessage("이메일은 필수입니다.");
        }

        @Test
        @DisplayName("잘못된 형식의 수령인 이메일로 생성 시 예외가 발생한다")
        void create_invalidRecipientEmail_throwsException() {
            // when & then
            assertThatThrownBy(() -> ShippingInfo.create(
                    "김수령", "010-9876-5432", "invalid-email", "서울시 강남구", LocalDate.now().plusDays(3)
            ))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("올바른 이메일 형식이 아닙니다.");
        }

        @Test
        @DisplayName("101글자 수령인 이메일로 생성 시 예외가 발생한다")
        void create_tooLongRecipientEmail_throwsException() {
            // given
            String longEmail = "a".repeat(90) + "@example.com"; // 101글자

            // when & then
            assertThatThrownBy(() -> ShippingInfo.create(
                    "김수령", "010-9876-5432", longEmail, "서울시 강남구", LocalDate.now().plusDays(3)
            ))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("이메일은 100자를 초과할 수 없습니다.");
        }
    }

    @Nested
    @DisplayName("수령인 주소 검증 테스트")
    class RecipientAddressValidationTest {

        @Test
        @DisplayName("null 수령인 주소로 생성 시 예외가 발생한다")
        void create_nullRecipientAddress_throwsException() {
            // when & then
            assertThatThrownBy(() -> ShippingInfo.create(
                    "김수령", "010-9876-5432", "recipient@example.com", null, LocalDate.now().plusDays(3)
            ))
                    .isInstanceOf(NullValueException.class)
                    .hasMessage("배송 주소를 입력하세요.");
        }

        @Test
        @DisplayName("빈 문자열 수령인 주소로 생성 시 예외가 발생한다")
        void create_emptyRecipientAddress_throwsException() {
            // when & then
            assertThatThrownBy(() -> ShippingInfo.create(
                    "김수령", "010-9876-5432", "recipient@example.com", "", LocalDate.now().plusDays(3)
            ))
                    .isInstanceOf(NullValueException.class)
                    .hasMessage("배송 주소를 입력하세요.");
        }

        @Test
        @DisplayName("공백만 있는 수령인 주소로 생성 시 예외가 발생한다")
        void create_blankRecipientAddress_throwsException() {
            // when & then
            assertThatThrownBy(() -> ShippingInfo.create(
                    "김수령", "010-9876-5432", "recipient@example.com", "   ", LocalDate.now().plusDays(3)
            ))
                    .isInstanceOf(NullValueException.class)
                    .hasMessage("배송 주소를 입력하세요.");
        }

        @Test
        @DisplayName("200글자 수령인 주소로 배송 정보를 생성한다")
        void create_maxLengthRecipientAddress_success() {
            // given
            String maxLengthAddress = "a".repeat(200);

            // when
            ShippingInfo shippingInfo = ShippingInfo.create(
                    "김수령", "010-9876-5432", "recipient@example.com", maxLengthAddress, LocalDate.now().plusDays(3)
            );

            // then
            assertThat(shippingInfo.getRecipientAddress()).isEqualTo(maxLengthAddress);
        }
    }

    @Nested
    @DisplayName("예상 배송일 검증 테스트")
    class ExpectedDeliveryDateValidationTest {

        @Test
        @DisplayName("현재 시간보다 1일 후 배송일로 생성 시 예외가 발생한다")
        void create_deliveryDateTooEarly_throwsException() {
            // given
            LocalDate tooEarlyDate = LocalDate.now().plusDays(1);

            // when & then
            assertThatThrownBy(() -> ShippingInfo.create(
                    "김수령", "010-9876-5432", "recipient@example.com", "서울시 강남구", tooEarlyDate
            ))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("배송 날짜는 최소 2일 이후로 설정해야 합니다.");
        }

        @Test
        @DisplayName("현재 시간보다 정확히 2일 후 배송일로 배송 정보를 생성한다")
        void create_exactlyTwoDaysLater_success() {
            // given
            LocalDate exactlyTwoDaysLater = LocalDate.now().plusDays(2);

            // when
            ShippingInfo shippingInfo = ShippingInfo.create(
                    "김수령", "010-9876-5432", "recipient@example.com", "서울시 강남구", exactlyTwoDaysLater
            );

            // then
            assertThat(shippingInfo.getExpectedDeliveryDate()).isEqualTo(exactlyTwoDaysLater);
        }

        @Test
        @DisplayName("현재 시간보다 과거 배송일로 생성 시 예외가 발생한다")
        void create_pastDeliveryDate_throwsException() {
            // given
            LocalDate pastDate = LocalDate.now().minusDays(1);

            // when & then
            assertThatThrownBy(() -> ShippingInfo.create(
                    "김수령", "010-9876-5432", "recipient@example.com", "서울시 강남구", pastDate
            ))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("배송 날짜는 최소 2일 이후로 설정해야 합니다.");
        }

        @Test
        @DisplayName("한 달 후 배송일로 배송 정보를 생성한다")
        void create_oneMonthLater_success() {
            // given
            LocalDate oneMonthLater = LocalDate.now().plusMonths(1);

            // when
            ShippingInfo shippingInfo = ShippingInfo.create(
                    "김수령", "010-9876-5432", "recipient@example.com", "서울시 강남구", oneMonthLater
            );

            // then
            assertThat(shippingInfo.getExpectedDeliveryDate()).isEqualTo(oneMonthLater);
        }
    }

    @Nested
    @DisplayName("배송 시작/종료 테스트")
    class ShippingStartEndTest {

        @Test
        @DisplayName("배송을 시작한다")
        void startShipping_success() {
            // given
            ShippingInfo shippingInfo = createTestShippingInfo();
            LocalDateTime beforeStart = LocalDateTime.now();

            // when
            ReflectionTestUtils.invokeMethod(shippingInfo, "startShipping");

            // then
            LocalDateTime afterStart = LocalDateTime.now();
            assertThat(shippingInfo.getShippingStartDate()).isBetween(beforeStart, afterStart);
            assertThat(shippingInfo.getShippingEndDate()).isNull();
        }

        @Test
        @DisplayName("배송을 종료한다")
        void endShipping_success() {
            // given
            ShippingInfo shippingInfo = createTestShippingInfo();
            ReflectionTestUtils.invokeMethod(shippingInfo, "startShipping");
            LocalDateTime beforeEnd = LocalDateTime.now();

            // when
            ReflectionTestUtils.invokeMethod(shippingInfo, "endShipping");

            // then
            LocalDateTime afterEnd = LocalDateTime.now();
            assertThat(shippingInfo.getShippingStartDate()).isNotNull();
            assertThat(shippingInfo.getShippingEndDate()).isBetween(beforeEnd, afterEnd);
        }

        @Test
        @DisplayName("배송 시작 전에 배송을 종료한다")
        void endShipping_beforeStart_success() {
            // given
            ShippingInfo shippingInfo = createTestShippingInfo();
            LocalDateTime beforeEnd = LocalDateTime.now();

            // when
            ReflectionTestUtils.invokeMethod(shippingInfo, "endShipping");

            // then
            LocalDateTime afterEnd = LocalDateTime.now();
            assertThat(shippingInfo.getShippingStartDate()).isNull();
            assertThat(shippingInfo.getShippingEndDate()).isBetween(beforeEnd, afterEnd);
        }
    }

    @Nested
    @DisplayName("동등성 및 해시코드 테스트")
    class EqualsAndHashCodeTest {

        @Test
        @DisplayName("같은 정보를 가진 ShippingInfo는 동등하다")
        void equals_withSameValues_returnsTrue() {
            // given
            LocalDate expectedDate = LocalDate.now().plusDays(3);
            ShippingInfo shippingInfo1 = ShippingInfo.create(
                    "김수령", "010-9876-5432", "recipient@example.com", "서울시 강남구", expectedDate
            );
            ShippingInfo shippingInfo2 = ShippingInfo.create(
                    "김수령", "010-9876-5432", "recipient@example.com", "서울시 강남구", expectedDate
            );

            // when & then
            assertThat(shippingInfo1).isEqualTo(shippingInfo2);
            assertThat(shippingInfo1.hashCode()).isEqualTo(shippingInfo2.hashCode());
        }

        @Test
        @DisplayName("다른 정보를 가진 ShippingInfo는 동등하지 않다")
        void equals_withDifferentValues_returnsFalse() {
            // given
            LocalDate expectedDate = LocalDate.now().plusDays(3);
            ShippingInfo shippingInfo1 = ShippingInfo.create(
                    "김수령", "010-9876-5432", "recipient@example.com", "서울시 강남구", expectedDate
            );
            ShippingInfo shippingInfo2 = ShippingInfo.create(
                    "이수령", "010-9876-5432", "recipient@example.com", "서울시 강남구", expectedDate
            );

            // when & then
            assertThat(shippingInfo1).isNotEqualTo(shippingInfo2);
        }
    }

    @Nested
    @DisplayName("toString 테스트")
    class ToStringTest {

        @Test
        @DisplayName("ShippingInfo의 문자열 표현을 반환한다")
        void toString_success() {
            // given
            ShippingInfo shippingInfo = createTestShippingInfo();

            // when
            String result = shippingInfo.toString();

            // then
            assertThat(result).contains("recipientName");
            assertThat(result).contains("recipientPhoneNumber");
            assertThat(result).contains("recipientEmail");
            assertThat(result).contains("recipientAddress");
            assertThat(result).contains("expectedDeliveryDate");
        }
    }

    @Nested
    @DisplayName("복합 시나리오 테스트")
    class ComplexScenarioTest {

        @Test
        @DisplayName("배송 시작과 종료를 순서대로 진행한다")
        void fullShippingProcess() {
            // given
            ShippingInfo shippingInfo = createTestShippingInfo();

            // when - 배송 시작
            ReflectionTestUtils.invokeMethod(shippingInfo, "startShipping");
            LocalDateTime startTime = shippingInfo.getShippingStartDate();

            // 시간 경과 시뮬레이션을 위한 잠시 대기
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // when - 배송 종료
            ReflectionTestUtils.invokeMethod(shippingInfo, "endShipping");
            LocalDateTime endTime = shippingInfo.getShippingEndDate();

            // then
            assertThat(startTime).isNotNull();
            assertThat(endTime).isNotNull();
            assertThat(endTime).isAfterOrEqualTo(startTime);
        }

        @Test
        @DisplayName("최소 길이 값들로 배송 정보를 생성한다")
        void create_minimumValues_success() {
            // given
            String minName = "김수"; // 2글자
            String phoneNumber = "010-1234-5678";
            String minEmail = "a@b.co"; // 최소 형식
            String minAddress = "서울"; // 최소 주소
            LocalDate minDeliveryDate = LocalDate.now().plusDays(2);

            // when
            ShippingInfo shippingInfo = ShippingInfo.create(minName, phoneNumber, minEmail, minAddress, minDeliveryDate);

            // then
            assertThat(shippingInfo).isNotNull();
            assertThat(shippingInfo.getRecipientName()).isEqualTo(minName);
            assertThat(shippingInfo.getRecipientAddress()).isEqualTo(minAddress);
        }
    }

    // 테스트 헬퍼 메소드
    private ShippingInfo createTestShippingInfo() {
        return ShippingInfo.create(
                "김수령",
                "010-9876-5432",
                "recipient@example.com",
                "서울시 강남구 테헤란로 123",
                LocalDate.now().plusDays(3)
        );
    }
}