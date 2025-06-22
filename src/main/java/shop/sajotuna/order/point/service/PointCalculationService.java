package shop.sajotuna.order.point.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.sajotuna.order.point.controller.request.PointEvent;
import shop.sajotuna.order.point.exception.PointPolicyNotFoundException;
import shop.sajotuna.order.point.utils.PointCalculator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointCalculationService {

    private final List<PointCalculator> calculators;

    public int calculatePoint(PointEvent event) {
        return calculators.stream()
                .filter(calculator -> calculator.supports(event.getType()))
                .findFirst()
                .orElseThrow(() -> new PointPolicyNotFoundException(event.getType()))
                .calculate(event);
    }
}
