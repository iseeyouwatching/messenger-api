package ru.hits.messengerapi.common;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация, необходимая для доступности классов из модуля common в других модулях.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(CommonConfig.class)
public @interface EnableCommonClasses {
}
