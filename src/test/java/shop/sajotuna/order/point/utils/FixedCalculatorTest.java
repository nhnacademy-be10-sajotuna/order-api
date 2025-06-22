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
class FixedCalculatorTest {

    @Mock
    private PointPolicyService policyService;

    @InjectMocks
    private FixedCalculator calculator;

    @Test
    void supports_supportedTypes_returnTrue() {
        assertTrue(calculator.supports(PointPolicyType.REVIEW));
        assertTrue(calculator.supports(PointPolicyType.REVIEW_WITH_IMAGE));
        assertTrue(calculator.supports(PointPolicyType.REGISTER));
    }

    @Test
    void supports_unsupportedType_returnFalse() {
        assertFalse(calculator.supports(PointPolicyType.PURCHASE));
        assertFalse(calculator.supports(PointPolicyType.RETURNED));
    }

    @Test
    void calculate_returnsFixedPointFromPolicy() {
        PointEvent event = mock(PointEvent.class);
        when(event.getType()).thenReturn(PointPolicyType.REVIEW);

        PointPolicy policy = mock(PointPolicy.class);
        when(policyService.getPointPolicy(PointPolicyType.REVIEW)).thenReturn(policy);
        when(policy.getFixedPoint()).thenReturn(42);

        int result = calculator.calculate(event);
        assertEquals(42, result);
        verify(policyService).getPointPolicy(PointPolicyType.REVIEW);
        verify(policy).getFixedPoint();
    }
}
