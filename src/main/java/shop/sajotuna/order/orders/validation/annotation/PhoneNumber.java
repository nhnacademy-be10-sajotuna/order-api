package shop.sajotuna.order.orders.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import shop.sajotuna.order.orders.validation.validator.PhoneNumberValidator;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PhoneNumberValidator.class)
@Documented
public @interface PhoneNumber {
    String message() default "올바른 전화번호 형식이 아닙니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

