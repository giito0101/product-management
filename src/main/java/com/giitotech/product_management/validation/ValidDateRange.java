package com.giitotech.product_management.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = DateRangeValidator.class)
@Target(ElementType.TYPE) // クラスレベルで使用
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDateRange {
    String message() default "開始日か終了日が不正です";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
