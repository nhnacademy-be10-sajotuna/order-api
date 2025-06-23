package shop.sajotuna.order.point.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.sajotuna.order.point.controller.request.PointEvent;
import shop.sajotuna.order.point.domain.PointPolicy;
import shop.sajotuna.order.point.domain.PointPolicyType;
import shop.sajotuna.order.point.service.PointPolicyService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseCalculatorTest {

    @Mock
    private PointPolicyService policyService;

    @InjectMocks
    private PurchaseCalculator calculator;

    @Test
    void supports_onlyPurchaseType() {
        assertTrue(calculator.supports(PointPolicyType.PURCHASE));
        assertFalse(calculator.supports(PointPolicyType.REVIEW));
        assertFalse(calculator.supports(PointPolicyType.RETURNED));
    }

    @Test
    void calculate_usesPolicyCalculatePoint() {
        PointEvent event = mock(PointEvent.class);
        when(event.getType()).thenReturn(PointPolicyType.PURCHASE);
        when(event.getTotalPrice()).thenReturn(2000);

        PointPolicy policy = mock(PointPolicy.class);
        when(policyService.getPointPolicy(PointPolicyType.PURCHASE)).thenReturn(policy);
        when(policy.calculatePoint(2000)).thenReturn(99);

        int result = calculator.calculate(event);
        assertEquals(99, result);
        verify(policyService).getPointPolicy(PointPolicyType.PURCHASE);
        verify(policy).calculatePoint(2000);
    }
}
