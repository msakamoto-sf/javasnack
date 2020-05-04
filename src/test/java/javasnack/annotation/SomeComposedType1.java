package javasnack.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@SomeComposableMark
@SomeComposableType("xxx")
@Retention(RUNTIME)
@Target(TYPE)
@interface SomeComposedType1 {
    String value() default "foo";
}
