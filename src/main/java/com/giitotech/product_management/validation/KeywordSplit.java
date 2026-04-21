package com.giitotech.product_management.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = KeywordSplitValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface KeywordSplit {
    String message() default "検索キーワードは10以下にしてください。";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
