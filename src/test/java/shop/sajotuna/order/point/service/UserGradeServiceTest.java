package shop.sajotuna.order.point.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.orders.service.OrderTotalPriceService;
import shop.sajotuna.order.point.controller.response.GradePointPolicyResponse;
import shop.sajotuna.order.point.domain.Grade;
import shop.sajotuna.order.point.domain.GradePointPolicy;
import shop.sajotuna.order.point.domain.UserGrade;
import shop.sajotuna.order.point.repository.GradePointPolicyQueryRepository;
import shop.sajotuna.order.point.repository.GradePointPolicyRepository;
import shop.sajotuna.order.point.repository.UserGradeRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserGradeServiceTest {

    @Mock
    private GradePointPolicyRepository gradePointPolicyRepository;

    @Mock
    private OrderTotalPriceService orderTotalPriceService;

    @Mock
    private GradePointPolicyQueryRepository gradePointPolicyQueryRepository;

    @Mock
    private UserGradeRepository userGradeRepository;

    private UserGradeService userGradeService;

    @BeforeEach
    void setUp() {
        userGradeService = new UserGradeService(gradePointPolicyRepository, orderTotalPriceService, gradePointPolicyQueryRepository, userGradeRepository);
    }

    @Test
    @DisplayName("findAndUpdateGrade - 기존 사용자 등급 업데이트 성공")
    void findAndUpdateGrade_existingUser_success() {
        // given
        Long userId = 1L;
        Money totalOrderAmount = Money.of(100000);
        
        GradePointPolicy generalPolicy = new GradePointPolicy(1L, Grade.GENERAL, Money.of(0), Money.of(100000), 1);
        GradePointPolicy royalPolicy = new GradePointPolicy(2L, Grade.ROYAL, Money.of(100000), Money.of(200000), 2);
        
        UserGrade existingUserGrade = UserGrade.builder()
                .id(1L)
                .userId(userId)
                .grade(generalPolicy)
                .build();
        
        UserGrade updatedUserGrade = UserGrade.builder()
                .id(1L)
                .userId(userId)
                .grade(royalPolicy)
                .build();

        when(userGradeRepository.findByUserId(userId)).thenReturn(Optional.of(existingUserGrade));
        when(orderTotalPriceService.calculateTotalOrderAmount(userId)).thenReturn(totalOrderAmount);
        when(gradePointPolicyQueryRepository.findApplicablePolicy(totalOrderAmount.getAmount())).thenReturn(royalPolicy);
        when(userGradeRepository.save(any(UserGrade.class))).thenReturn(updatedUserGrade);

        // when
        GradePointPolicyResponse response = userGradeService.findAndUpdateGrade(userId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getGrade()).isEqualTo(Grade.ROYAL);
        assertThat(response.getPointRate()).isEqualTo(2);
        assertThat(response.getMinTotalOrderPrice()).isEqualTo(100000);
        assertThat(response.getMaxTotalOrderPrice()).isEqualTo(200000);

        verify(userGradeRepository).findByUserId(userId);
        verify(orderTotalPriceService).calculateTotalOrderAmount(userId);
        verify(gradePointPolicyQueryRepository).findApplicablePolicy(totalOrderAmount.getAmount());
        verify(userGradeRepository).save(any(UserGrade.class));
    }

    @Test
    @DisplayName("findAndUpdateGrade - 신규 사용자 기본 등급 설정")
    void findAndUpdateGrade_newUser_createWithDefaultGrade() {
        // given
        Long userId = 2L;
        Money totalOrderAmount = Money.of(10000);
        
        GradePointPolicy generalPolicy = new GradePointPolicy(1L, Grade.GENERAL, Money.of(0), Money.of(100000), 1);
        
        UserGrade newUserGrade = UserGrade.builder()
                .id(2L)
                .userId(userId)
                .grade(generalPolicy)
                .build();

        when(userGradeRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(gradePointPolicyRepository.findByGrade(Grade.GENERAL)).thenReturn(generalPolicy);
        when(orderTotalPriceService.calculateTotalOrderAmount(userId)).thenReturn(totalOrderAmount);
        when(gradePointPolicyQueryRepository.findApplicablePolicy(totalOrderAmount.getAmount())).thenReturn(generalPolicy);
        when(userGradeRepository.save(any(UserGrade.class))).thenReturn(newUserGrade);

        // when
        GradePointPolicyResponse response = userGradeService.findAndUpdateGrade(userId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getGrade()).isEqualTo(Grade.GENERAL);
        assertThat(response.getPointRate()).isEqualTo(1);
        assertThat(response.getMinTotalOrderPrice()).isEqualTo(0);
        assertThat(response.getMaxTotalOrderPrice()).isEqualTo(100000);

        verify(userGradeRepository).findByUserId(userId);
        verify(gradePointPolicyRepository).findByGrade(Grade.GENERAL);
        verify(orderTotalPriceService).calculateTotalOrderAmount(userId);
        verify(gradePointPolicyQueryRepository).findApplicablePolicy(totalOrderAmount.getAmount());
        verify(userGradeRepository).save(any(UserGrade.class));
    }

    @Test
    @DisplayName("findAndUpdateGrade - 등급 변경 없는 경우")
    void findAndUpdateGrade_noGradeChange() {
        // given
        Long userId = 4L;
        Money totalOrderAmount = Money.of(30000);
        
        GradePointPolicy generalPolicy = new GradePointPolicy(1L, Grade.GENERAL, Money.of(0), Money.of(100000), 1);
        
        UserGrade existingUserGrade = UserGrade.builder()
                .id(4L)
                .userId(userId)
                .grade(generalPolicy)
                .build();

        when(userGradeRepository.findByUserId(userId)).thenReturn(Optional.of(existingUserGrade));
        when(orderTotalPriceService.calculateTotalOrderAmount(userId)).thenReturn(totalOrderAmount);
        when(gradePointPolicyQueryRepository.findApplicablePolicy(totalOrderAmount.getAmount())).thenReturn(generalPolicy);
        when(userGradeRepository.save(any(UserGrade.class))).thenReturn(existingUserGrade);

        // when
        GradePointPolicyResponse response = userGradeService.findAndUpdateGrade(userId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getGrade()).isEqualTo(Grade.GENERAL);
        assertThat(response.getPointRate()).isEqualTo(1);
    }

    @Test
    @DisplayName("findAndUpdateGrade - 최고 등급(PLATINUM) 달성")
    void findAndUpdateGrade_platinumGrade() {
        // given
        Long userId = 5L;
        Money totalOrderAmount = Money.of(500000);
        
        GradePointPolicy royalPolicy = new GradePointPolicy(2L, Grade.ROYAL, Money.of(100000), Money.of(200000), 2);
        GradePointPolicy platinumPolicy = new GradePointPolicy(4L, Grade.PLATINUM, Money.of(300000), Money.of(Integer.MAX_VALUE), 3);
        
        UserGrade existingUserGrade = UserGrade.builder()
                .id(5L)
                .userId(userId)
                .grade(royalPolicy)
                .build();
        
        UserGrade platinumUserGrade = UserGrade.builder()
                .id(5L)
                .userId(userId)
                .grade(platinumPolicy)
                .build();

        when(userGradeRepository.findByUserId(userId)).thenReturn(Optional.of(existingUserGrade));
        when(orderTotalPriceService.calculateTotalOrderAmount(userId)).thenReturn(totalOrderAmount);
        when(gradePointPolicyQueryRepository.findApplicablePolicy(totalOrderAmount.getAmount())).thenReturn(platinumPolicy);
        when(userGradeRepository.save(any(UserGrade.class))).thenReturn(platinumUserGrade);

        // when
        GradePointPolicyResponse response = userGradeService.findAndUpdateGrade(userId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getGrade()).isEqualTo(Grade.PLATINUM);
        assertThat(response.getPointRate()).isEqualTo(3);
        assertThat(response.getMinTotalOrderPrice()).isEqualTo(300000);
        assertThat(response.getMaxTotalOrderPrice()).isEqualTo(Integer.MAX_VALUE);
    }

}