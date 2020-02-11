package javasnack.classgraph;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

@Retention(RUNTIME)
@interface SomeMark1 {
    String value() default "mark1";

    String extra() default "extra1";
}
