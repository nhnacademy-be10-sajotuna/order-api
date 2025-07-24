package shop.sajotuna.order.orders.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import shop.sajotuna.order.common.exception.NullValueException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Orderer 도메인 테스트")
class OrdererTest {

    @Nested
    @DisplayName("주문자 생성 테스트")
    class CreateOrdererTest {

        @Test
        @DisplayName("유효한 정보로 회원 주문자를 생성한다")
        void createOrderer_validUserOrderer_success() {
            // given
            Long userId = 1L;
            String ordererName = "홍길동";
            String ordererPhoneNumber = "010-1234-5678";
            String ordererEmail = "test@example.com";

            // when
            Orderer orderer = Orderer.createOrderer(userId, ordererName, ordererPhoneNumber, ordererEmail);

            // then
            assertThat(orderer).isNotNull();
            assertThat(orderer.getUserId()).isEqualTo(userId);
            assertThat(orderer.getOrdererName()).isEqualTo(ordererName);
            assertThat(orderer.getOrdererPhoneNumber()).isEqualTo(ordererPhoneNumber);
            assertThat(orderer.getOrdererEmail()).isEqualTo(ordererEmail);
            assertThat(orderer.isUserOrder()).isTrue();
        }

        @Test
        @DisplayName("유효한 정보로 비회원 주문자를 생성한다")
        void createOrderer_validGuestOrderer_success() {
            // given
            Long userId = null;
            String ordererName = "김철수";
            String ordererPhoneNumber = "010-9876-5432";
            String ordererEmail = "guest@example.com";

            // when
            Orderer orderer = Orderer.createOrderer(userId, ordererName, ordererPhoneNumber, ordererEmail);

            // then
            assertThat(orderer).isNotNull();
            assertThat(orderer.getUserId()).isNull();
            assertThat(orderer.getOrdererName()).isEqualTo(ordererName);
            assertThat(orderer.getOrdererPhoneNumber()).isEqualTo(ordererPhoneNumber);
            assertThat(orderer.getOrdererEmail()).isEqualTo(ordererEmail);
            assertThat(orderer.isUserOrder()).isFalse();
        }

        @Test
        @DisplayName("영문 이름으로 주문자를 생성한다")
        void createOrderer_englishName_success() {
            // given
            Long userId = 1L;
            String ordererName = "John Smith";
            String ordererPhoneNumber = "010-1111-2222";
            String ordererEmail = "john@example.com";

            // when
            Orderer orderer = Orderer.createOrderer(userId, ordererName, ordererPhoneNumber, ordererEmail);

            // then
            assertThat(orderer).isNotNull();
            assertThat(orderer.getOrdererName()).isEqualTo(ordererName);
        }
    }

    @Nested
    @DisplayName("주문자 ID 검증 테스트")
    class UserIdValidationTest {

        @Test
        @DisplayName("양수 사용자 ID로 주문자를 생성한다")
        void createOrderer_positiveUserId_success() {
            // given
            Long userId = 999L;

            // when
            Orderer orderer = Orderer.createOrderer(userId, "홍길동", "010-1234-5678", "test@example.com");

            // then
            assertThat(orderer.getUserId()).isEqualTo(userId);
            assertThat(orderer.isUserOrder()).isTrue();
        }

        @Test
        @DisplayName("0인 사용자 ID로 주문자 생성 시 예외가 발생한다")
        void createOrderer_zeroUserId_throwsException() {
            // given
            Long userId = 0L;

            // when & then
            assertThatThrownBy(() -> Orderer.createOrderer(userId, "홍길동", "010-1234-5678", "test@example.com"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("주문자 ID는 양수여야 합니다.");
        }

        @Test
        @DisplayName("음수 사용자 ID로 주문자 생성 시 예외가 발생한다")
        void createOrderer_negativeUserId_throwsException() {
            // given
            Long userId = -1L;

            // when & then
            assertThatThrownBy(() -> Orderer.createOrderer(userId, "홍길동", "010-1234-5678", "test@example.com"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("주문자 ID는 양수여야 합니다.");
        }
    }

    @Nested
    @DisplayName("주문자 이름 검증 테스트")
    class OrdererNameValidationTest {

        @Test
        @DisplayName("null 이름으로 주문자 생성 시 예외가 발생한다")
        void createOrderer_nullName_throwsException() {
            // when & then
            assertThatThrownBy(() -> Orderer.createOrderer(1L, null, "010-1234-5678", "test@example.com"))
                    .isInstanceOf(NullValueException.class)
                    .hasMessage("이름은 필수입니다.");
        }

        @Test
        @DisplayName("빈 문자열 이름으로 주문자 생성 시 예외가 발생한다")
        void createOrderer_emptyName_throwsException() {
            // when & then
            assertThatThrownBy(() -> Orderer.createOrderer(1L, "", "010-1234-5678", "test@example.com"))
                    .isInstanceOf(NullValueException.class)
                    .hasMessage("이름은 필수입니다.");
        }

        @Test
        @DisplayName("공백만 있는 이름으로 주문자 생성 시 예외가 발생한다")
        void createOrderer_blankName_throwsException() {
            // when & then
            assertThatThrownBy(() -> Orderer.createOrderer(1L, "   ", "010-1234-5678", "test@example.com"))
                    .isInstanceOf(NullValueException.class)
                    .hasMessage("이름은 필수입니다.");
        }

        @Test
        @DisplayName("1글자 이름으로 주문자 생성 시 예외가 발생한다")
        void createOrderer_singleCharacterName_throwsException() {
            // when & then
            assertThatThrownBy(() -> Orderer.createOrderer(1L, "김", "010-1234-5678", "test@example.com"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("이름은 최소 2자 이상이어야 합니다.");
        }

        @Test
        @DisplayName("51글자 이름으로 주문자 생성 시 예외가 발생한다")
        void createOrderer_tooLongName_throwsException() {
            // given
            String longName = "a".repeat(51);

            // when & then
            assertThatThrownBy(() -> Orderer.createOrderer(1L, longName, "010-1234-5678", "test@example.com"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("이름은 50자를 초과할 수 없습니다.");
        }

        @Test
        @DisplayName("50글자 이름으로 주문자를 생성한다")
        void createOrderer_fiftyCharacterName_success() {
            // given
            String maxLengthName = "a".repeat(50);

            // when
            Orderer orderer = Orderer.createOrderer(1L, maxLengthName, "010-1234-5678", "test@example.com");

            // then
            assertThat(orderer.getOrdererName()).isEqualTo(maxLengthName);
        }

        @Test
        @DisplayName("특수문자가 포함된 이름으로 주문자 생성 시 예외가 발생한다")
        void createOrderer_nameWithSpecialCharacters_throwsException() {
            // when & then
            assertThatThrownBy(() -> Orderer.createOrderer(1L, "홍길동123", "010-1234-5678", "test@example.com"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("이름은 한글, 영문, 공백만 입력 가능합니다.");

            assertThatThrownBy(() -> Orderer.createOrderer(1L, "홍길동!", "010-1234-5678", "test@example.com"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("이름은 한글, 영문, 공백만 입력 가능합니다.");
        }

        @Test
        @DisplayName("한글과 영문이 섞인 이름으로 주문자를 생성한다")
        void createOrderer_mixedKoreanEnglishName_success() {
            // given
            String mixedName = "홍 John";

            // when
            Orderer orderer = Orderer.createOrderer(1L, mixedName, "010-1234-5678", "test@example.com");

            // then
            assertThat(orderer.getOrdererName()).isEqualTo(mixedName);
        }
    }

    @Nested
    @DisplayName("전화번호 검증 테스트")
    class PhoneNumberValidationTest {

        @Test
        @DisplayName("null 전화번호로 주문자 생성 시 예외가 발생한다")
        void createOrderer_nullPhoneNumber_throwsException() {
            // when & then
            assertThatThrownBy(() -> Orderer.createOrderer(1L, "홍길동", null, "test@example.com"))
                    .isInstanceOf(NullValueException.class)
                    .hasMessage("전화번호는 필수입니다.");
        }

        @Test
        @DisplayName("빈 문자열 전화번호로 주문자 생성 시 예외가 발생한다")
        void createOrderer_emptyPhoneNumber_throwsException() {
            // when & then
            assertThatThrownBy(() -> Orderer.createOrderer(1L, "홍길동", "", "test@example.com"))
                    .isInstanceOf(NullValueException.class)
                    .hasMessage("전화번호는 필수입니다.");
        }

        @Test
        @DisplayName("잘못된 형식의 전화번호로 주문자 생성 시 예외가 발생한다")
        void createOrderer_invalidPhoneNumberFormat_throwsException() {
            // when & then
            assertThatThrownBy(() -> Orderer.createOrderer(1L, "홍길동", "010-123-5678", "test@example.com"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("전화번호는 010-1234-5678 형식이어야 합니다.");

            assertThatThrownBy(() -> Orderer.createOrderer(1L, "홍길동", "011-1234-5678", "test@example.com"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("전화번호는 010-1234-5678 형식이어야 합니다.");

            assertThatThrownBy(() -> Orderer.createOrderer(1L, "홍길동", "01012345678", "test@example.com"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("전화번호는 010-1234-5678 형식이어야 합니다.");
        }

        @Test
        @DisplayName("공백이 포함된 전화번호는 트림 후 검증한다")
        void createOrderer_phoneNumberWithSpaces_success() {
            // given
            String phoneNumberWithSpaces = "  010-1234-5678  ";

            // when
            Orderer orderer = Orderer.createOrderer(1L, "홍길동", phoneNumberWithSpaces, "test@example.com");

            // then
            assertThat(orderer.getOrdererPhoneNumber()).isEqualTo("  010-1234-5678  ");
        }
    }

    @Nested
    @DisplayName("이메일 검증 테스트")
    class EmailValidationTest {

        @Test
        @DisplayName("null 이메일로 주문자 생성 시 예외가 발생한다")
        void createOrderer_nullEmail_throwsException() {
            // when & then
            assertThatThrownBy(() -> Orderer.createOrderer(1L, "홍길동", "010-1234-5678", null))
                    .isInstanceOf(NullValueException.class)
                    .hasMessage("이메일은 필수입니다.");
        }

        @Test
        @DisplayName("빈 문자열 이메일로 주문자 생성 시 예외가 발생한다")
        void createOrderer_emptyEmail_throwsException() {
            // when & then
            assertThatThrownBy(() -> Orderer.createOrderer(1L, "홍길동", "010-1234-5678", ""))
                    .isInstanceOf(NullValueException.class)
                    .hasMessage("이메일은 필수입니다.");
        }

        @Test
        @DisplayName("잘못된 형식의 이메일로 주문자 생성 시 예외가 발생한다")
        void createOrderer_invalidEmailFormat_throwsException() {
            // when & then
            assertThatThrownBy(() -> Orderer.createOrderer(1L, "홍길동", "010-1234-5678", "invalid-email"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("올바른 이메일 형식이 아닙니다.");

            assertThatThrownBy(() -> Orderer.createOrderer(1L, "홍길동", "010-1234-5678", "test@"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("올바른 이메일 형식이 아닙니다.");

            assertThatThrownBy(() -> Orderer.createOrderer(1L, "홍길동", "010-1234-5678", "@example.com"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("올바른 이메일 형식이 아닙니다.");
        }

        @Test
        @DisplayName("101글자 이메일로 주문자 생성 시 예외가 발생한다")
        void createOrderer_tooLongEmail_throwsException() {
            // given
            String longEmail = "a".repeat(90) + "@example.com"; // 101글자

            // when & then
            assertThatThrownBy(() -> Orderer.createOrderer(1L, "홍길동", "010-1234-5678", longEmail))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("이메일은 100자를 초과할 수 없습니다.");
        }

        @Test
        @DisplayName("100글자 이메일로 주문자를 생성한다")
        void createOrderer_hundredCharacterEmail_success() {
            // given
            String maxLengthEmail = "a".repeat(89) + "@tttest.com"; // 100글자

            // when
            Orderer orderer = Orderer.createOrderer(1L, "홍길동", "010-1234-5678", maxLengthEmail);

            // then
            assertThat(orderer.getOrdererEmail()).isEqualTo(maxLengthEmail);
        }

        @Test
        @DisplayName("다양한 유효한 이메일 형식으로 주문자를 생성한다")
        void createOrderer_validEmailFormats_success() {
            // given & when & then
            assertThat(Orderer.createOrderer(1L, "홍길동", "010-1234-5678", "test@example.com").getOrdererEmail())
                    .isEqualTo("test@example.com");

            assertThat(Orderer.createOrderer(1L, "홍길동", "010-1234-5678", "test.user@example.co.kr").getOrdererEmail())
                    .isEqualTo("test.user@example.co.kr");

            assertThat(Orderer.createOrderer(1L, "홍길동", "010-1234-5678", "test+tag@example.com").getOrdererEmail())
                    .isEqualTo("test+tag@example.com");

            assertThat(Orderer.createOrderer(1L, "홍길동", "010-1234-5678", "test_user@example-domain.com").getOrdererEmail())
                    .isEqualTo("test_user@example-domain.com");
        }
    }

    @Nested
    @DisplayName("isUserOrder 메소드 테스트")
    class IsUserOrderTest {

        @Test
        @DisplayName("userId가 있으면 회원 주문이다")
        void isUserOrder_withUserId_returnsTrue() {
            // given
            Orderer orderer = Orderer.createOrderer(1L, "홍길동", "010-1234-5678", "test@example.com");

            // when & then
            assertThat(orderer.isUserOrder()).isTrue();
        }

        @Test
        @DisplayName("userId가 null이면 비회원 주문이다")
        void isUserOrder_withoutUserId_returnsFalse() {
            // given
            Orderer orderer = Orderer.createOrderer(null, "홍길동", "010-1234-5678", "test@example.com");

            // when & then
            assertThat(orderer.isUserOrder()).isFalse();
        }
    }

    @Nested
    @DisplayName("동등성 및 해시코드 테스트")
    class EqualsAndHashCodeTest {

        @Test
        @DisplayName("같은 정보를 가진 Orderer는 동등하다")
        void equals_withSameValues_returnsTrue() {
            // given
            Orderer orderer1 = Orderer.createOrderer(1L, "홍길동", "010-1234-5678", "test@example.com");
            Orderer orderer2 = Orderer.createOrderer(1L, "홍길동", "010-1234-5678", "test@example.com");

            // when & then
            assertThat(orderer1).isEqualTo(orderer2);
            assertThat(orderer1.hashCode()).isEqualTo(orderer2.hashCode());
        }

        @Test
        @DisplayName("다른 정보를 가진 Orderer는 동등하지 않다")
        void equals_withDifferentValues_returnsFalse() {
            // given
            Orderer orderer1 = Orderer.createOrderer(1L, "홍길동", "010-1234-5678", "test@example.com");
            Orderer orderer2 = Orderer.createOrderer(2L, "홍길동", "010-1234-5678", "test@example.com");

            // when & then
            assertThat(orderer1).isNotEqualTo(orderer2);
        }
    }
}