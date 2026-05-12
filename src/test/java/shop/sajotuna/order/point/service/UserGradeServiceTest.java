package shop.sajotuna.order.point.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.orders.service.pricing.OrderTotalPriceService;
import shop.sajotuna.order.point.controller.response.GradePointPolicyResponse;
import shop.sajotuna.order.point.domain.Grade;
import shop.sajotuna.order.point.domain.GradePointPolicy;
import shop.sajotuna.order.point.domain.UserGrade;
import shop.sajotuna.order.point.repository.GradePointPolicyQueryRepository;
import shop.sajotuna.order.point.repository.GradePointPolicyRepository;
import shop.sajotuna.order.point.repository.UserGradeRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        userGradeService = new UserGradeService(
                gradePointPolicyRepository,
                orderTotalPriceService,
                gradePointPolicyQueryRepository,
                userGradeRepository
        );
    }

    @Test
    @DisplayName("getUserGrade returns stored grade without recalculation")
    void getUserGrade_existingUser_success() {
        Long userId = 1L;
        GradePointPolicy royalPolicy = new GradePointPolicy(2L, Grade.ROYAL, Money.of(100_000), Money.of(200_000), 2);
        UserGrade userGrade = UserGrade.builder()
                .id(1L)
                .userId(userId)
                .grade(royalPolicy)
                .build();

        when(userGradeRepository.findByUserId(userId)).thenReturn(Optional.of(userGrade));

        GradePointPolicyResponse response = userGradeService.getUserGrade(userId);

        assertThat(response.getGrade()).isEqualTo(Grade.ROYAL);
        assertThat(response.getPointRate()).isEqualTo(2);
        verify(orderTotalPriceService, never()).calculateTotalOrderAmount(userId);
        verify(gradePointPolicyQueryRepository, never()).findApplicablePolicy(any(Integer.class));
        verify(userGradeRepository, never()).save(any(UserGrade.class));
    }

    @Test
    @DisplayName("getUserGrade returns default grade when user grade is not stored")
    void getUserGrade_newUser_defaultGrade() {
        Long userId = 2L;
        GradePointPolicy generalPolicy = new GradePointPolicy(1L, Grade.GENERAL, Money.of(0), Money.of(100_000), 1);

        when(userGradeRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(gradePointPolicyRepository.findByGrade(Grade.GENERAL)).thenReturn(generalPolicy);

        GradePointPolicyResponse response = userGradeService.getUserGrade(userId);

        assertThat(response.getGrade()).isEqualTo(Grade.GENERAL);
        assertThat(response.getPointRate()).isEqualTo(1);
        verify(orderTotalPriceService, never()).calculateTotalOrderAmount(userId);
        verify(userGradeRepository, never()).save(any(UserGrade.class));
    }

    @Test
    @DisplayName("updateGrade recalculates grade from recent order amount")
    void updateGrade_existingUser_success() {
        Long userId = 1L;
        GradePointPolicy generalPolicy = new GradePointPolicy(1L, Grade.GENERAL, Money.of(0), Money.of(100_000), 1);
        GradePointPolicy royalPolicy = new GradePointPolicy(2L, Grade.ROYAL, Money.of(100_000), Money.of(200_000), 2);
        UserGrade userGrade = UserGrade.builder()
                .id(1L)
                .userId(userId)
                .grade(generalPolicy)
                .build();

        when(userGradeRepository.findByUserId(userId)).thenReturn(Optional.of(userGrade));
        when(orderTotalPriceService.calculateTotalOrderAmount(userId)).thenReturn(Money.of(100_000));
        when(gradePointPolicyQueryRepository.findApplicablePolicy(100_000)).thenReturn(royalPolicy);

        userGradeService.updateGrade(userId);

        assertThat(userGrade.getGrade()).isEqualTo(royalPolicy);
        verify(userGradeRepository).save(userGrade);
    }

    @Test
    @DisplayName("updateGrade creates default user grade before recalculation")
    void updateGrade_newUser_success() {
        Long userId = 2L;
        GradePointPolicy generalPolicy = new GradePointPolicy(1L, Grade.GENERAL, Money.of(0), Money.of(100_000), 1);
        UserGrade newUserGrade = UserGrade.createForRegisterUser(userId, generalPolicy);

        when(userGradeRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(gradePointPolicyRepository.findByGrade(Grade.GENERAL)).thenReturn(generalPolicy);
        when(orderTotalPriceService.calculateTotalOrderAmount(userId)).thenReturn(Money.zero());
        when(gradePointPolicyQueryRepository.findApplicablePolicy(0)).thenReturn(generalPolicy);

        userGradeService.updateGrade(userId);

        verify(userGradeRepository).save(any(UserGrade.class));
    }
}
