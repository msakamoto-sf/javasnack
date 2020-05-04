package javasnack.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@SomeComposableMark("goodmorning")
@SomeComposableType
@Retention(RUNTIME)
@Target(TYPE)
@interface SomeComposedType2 {
    String value() default "bar";
}
