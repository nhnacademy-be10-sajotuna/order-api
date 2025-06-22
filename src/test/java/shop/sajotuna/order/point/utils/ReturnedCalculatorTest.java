package shop.sajotuna.order.point.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.sajotuna.order.point.controller.request.PointEvent;
import shop.sajotuna.order.point.domain.PointPolicyType;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReturnedCalculatorTest {

    @InjectMocks
    private ReturnedCalculator calculator;

    @Test
    void supports_onlyReturnedType() {
        assertTrue(calculator.supports(PointPolicyType.RETURNED));
        assertFalse(calculator.supports(PointPolicyType.PURCHASE));
        assertFalse(calculator.supports(PointPolicyType.REVIEW));
    }

    @Test
    void calculate_returnsEventTotalPrice() {
        PointEvent event = mock(PointEvent.class);
        when(event.getTotalPrice()).thenReturn(1234);

        int result = calculator.calculate(event);
        assertEquals(1234, result);
        verify(event).getTotalPrice();
    }
}
