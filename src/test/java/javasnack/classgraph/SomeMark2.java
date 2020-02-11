package javasnack.classgraph;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

@Retention(RUNTIME)
@interface SomeMark2 {
    String value() default "mark2";

    String extra() default "extra2";
}
