package shop.sajotuna.order.point.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.sajotuna.order.point.controller.request.PointEvent;
import shop.sajotuna.order.point.domain.PointPolicyType;
import shop.sajotuna.order.point.exception.PointPolicyNotFoundException;
import shop.sajotuna.order.point.service.PointCalculationService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointCalculationServiceTest {

    @Mock
    private PointCalculator calculatorA;

    @Mock
    private PointCalculator calculatorB;

    @InjectMocks
    private PointCalculationService service;

    @Test
    void calculatePoint_whenCalculatorSupports_thenReturnsCalculatedValue() {
        PointEvent event = new PointEvent(1L, PointPolicyType.PURCHASE, 1000);

        when(calculatorA.supports(PointPolicyType.PURCHASE)).thenReturn(true);
        when(calculatorA.calculate(event)).thenReturn(200);

        service = new PointCalculationService(List.of(calculatorA, calculatorB));

        int result = service.calculatePoint(event);

        assertEquals(200, result);
        verify(calculatorA).calculate(event);
        verifyNoInteractions(calculatorB);
    }

    @Test
    void calculatePoint_whenNoCalculatorSupports_thenThrowsException() {
        PointEvent event = new PointEvent(1L, PointPolicyType.REVIEW, null);

        when(calculatorA.supports(PointPolicyType.REVIEW)).thenReturn(false);
        when(calculatorB.supports(PointPolicyType.REVIEW)).thenReturn(false);

        service = new PointCalculationService(List.of(calculatorA, calculatorB));

        PointPolicyNotFoundException ex = assertThrows(PointPolicyNotFoundException.class,
                () -> service.calculatePoint(event));

        assertTrue(ex.getMessage().contains("Point policy not found for type: REVIEW"));
        verify(calculatorA).supports(PointPolicyType.REVIEW);
        verify(calculatorB).supports(PointPolicyType.REVIEW);
    }
}
