/*
 * Copyright 2020 "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package javasnack.annotation;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

/* reference:
 * - Javaアノテーションメモ(Hishidama's Java annotation Memo)
 *   - https://www.ne.jp/asahi/hishidama/home/tech/java/annotation.html
 * - いまさら聞けない「Javadoc」と「アノテーション」入門 (1/4)：【改訂版】Eclipseではじめるプログラミング（22） - ＠IT
 *   - https://www.atmarkit.co.jp/ait/articles/1105/19/news127.html
 * - いまさらJavaのアノテーションを復習する - Qiita
 *   - https://qiita.com/tkj06/items/a9fe1881dc965893a5f4
 */
public class TestAnnotationDemo {

    @SomeMark1
    @SomeType0a
    @SomeType0b
    @SomeType1
    private static class Sample1 {

        @SomeField1
        @SomeMark2
        public final int v1;

        @SomeField2
        @SomeField3
        public final int v2;

        @SomeConstructor1
        @SomeMark2
        public Sample1(final int v1, final int v2) {
            this.v1 = v1;
            this.v2 = v2;
        }

        @SomeConstructor2
        @SomeConstructor3
        public Sample1() {
            this.v1 = 1;
            this.v2 = -1;
        }

        @SomeMethod1
        @SomeMark3
        public int max() {
            return (v1 > v2 ? v1 : v2);
        }

        @SomeMethod2
        @SomeMethod3
        public int plus3(
                final int v3,
                @SomeParameter1 final int v4,
                @SomeParameter2 @SomeParameter3 final int v5) {
            return v1 + v2 + v3 + v4 + v5;
        }
    }

    Set<Class<?>> a2acs(final Annotation[] annotations) {
        return Stream.of(annotations).map(a -> a.annotationType()).collect(Collectors.toSet());
    }

    @Test
    public void testGetAnnotationClassesDemo() throws NoSuchFieldException, NoSuchMethodException, SecurityException {
        final var acs1 = a2acs(Sample1.class.getAnnotations());
        assertThat(acs1).hasSize(2);
        assertThat(acs1.contains(SomeMark1.class)).isTrue();
        assertThat(acs1.contains(SomeType1.class)).isTrue();
        // SomeType0a => RetentionPolicy.SOURCE, not found in reflections
        // SomeType0b => RetentionPolicy.CLASS, not found in reflections

        final Constructor<Sample1> c1 = Sample1.class.getConstructor(int.class, int.class);
        final var acsc1 = a2acs(c1.getAnnotations());
        assertThat(acsc1).hasSize(2);
        assertThat(acsc1.contains(SomeConstructor1.class)).isTrue();
        assertThat(acsc1.contains(SomeMark2.class)).isTrue();

        final Constructor<Sample1> c2 = Sample1.class.getConstructor();
        final var acsc2 = a2acs(c2.getAnnotations());
        assertThat(acsc2).hasSize(2);
        assertThat(acsc2.contains(SomeConstructor2.class)).isTrue();
        assertThat(acsc2.contains(SomeConstructor3.class)).isTrue();

        final Field f1 = Sample1.class.getField("v1");
        final var acsf1 = a2acs(f1.getAnnotations());
        assertThat(acsf1).hasSize(2);
        assertThat(acsf1.contains(SomeField1.class)).isTrue();
        assertThat(acsf1.contains(SomeMark2.class)).isTrue();

        final Field f2 = Sample1.class.getField("v2");
        final var acsf2 = a2acs(f2.getAnnotations());
        assertThat(acsf2).hasSize(2);
        assertThat(acsf2.contains(SomeField2.class)).isTrue();
        assertThat(acsf2.contains(SomeField3.class)).isTrue();

        final Method m1 = Sample1.class.getMethod("max");
        final var acsm1 = a2acs(m1.getAnnotations());
        assertThat(acsm1).hasSize(2);
        assertThat(acsm1.contains(SomeMethod1.class)).isTrue();
        assertThat(acsm1.contains(SomeMark3.class)).isTrue();

        final Method m2 = Sample1.class.getMethod("plus3", int.class, int.class, int.class);
        final var acsm2 = a2acs(m2.getAnnotations());
        assertThat(acsm2).hasSize(2);
        assertThat(acsm2.contains(SomeMethod2.class)).isTrue();
        assertThat(acsm2.contains(SomeMethod3.class)).isTrue();

        final Parameter[] paramOfM2 = m2.getParameters();
        assertThat(paramOfM2.length).isEqualTo(3);
        assertThat(paramOfM2[0].getAnnotations().length).isEqualTo(0);
        final var acsm2pv4 = a2acs(paramOfM2[1].getAnnotations());
        assertThat(acsm2pv4).hasSize(1);
        assertThat(acsm2pv4.contains(SomeParameter1.class)).isTrue();
        final var acsm2pv5 = a2acs(paramOfM2[2].getAnnotations());
        assertThat(acsm2pv5).hasSize(2);
        assertThat(acsm2pv5.contains(SomeParameter2.class)).isTrue();
        assertThat(acsm2pv5.contains(SomeParameter3.class)).isTrue();
    }

    @SomeType1
    @SomeInheritableType1
    interface AnnotatedInterfaceA {
        @SomeMethod1
        @SomeInheritableMethod1
        default int defaultMethod1(@SomeParameter1 int v1, @SomeParameter2 int v2) {
            return v1 + v2;
        }

        @SomeMethod2
        @SomeInheritableMethod2
        int interfaceMethod1(@SomeParameter3 int v3);

        @SomeMethod3
        @SomeInheritableMethod3
        int interfaceMethod2(@SomeParameter3 int v4);
    }

    interface NotAnnotatedInterfaceB {
    }

    @SomeType2
    @SomeInheritableType2
    private abstract static class AbstractClassA {
        @SomeField1
        public int m1;

        @SomeMethod3
        @SomeInheritableMethod3
        public abstract int abstractMethod1(@SomeParameter1 int v1);

        @SomeMethod1
        @SomeInheritableMethod1
        public abstract int abstractMethod2(@SomeParameter2 int v2);
    }

    @SomeType3
    private static class ExtendClassB extends AbstractClassA implements AnnotatedInterfaceA, NotAnnotatedInterfaceB {

        @SomeMark1
        @Override
        public int interfaceMethod1(@SomeMark3 int v3) {
            return v3;
        }

        @Override
        public int interfaceMethod2(int v4) {
            return v4;
        }

        @SomeMark2
        @Override
        public int abstractMethod1(@SomeMark3 int v1) {
            return v1;
        }

        @Override
        public int abstractMethod2(int v2) {
            return v2;
        }
    }

    @Test
    public void testGetAnnotationClassesDemo2() throws NoSuchFieldException, NoSuchMethodException, SecurityException {
        final var acs1 = a2acs(ExtendClassB.class.getAnnotations());
        assertThat(acs1).hasSize(2);
        assertThat(acs1.contains(SomeType3.class)).isTrue();
        assertThat(acs1.contains(SomeInheritableType2.class)).isTrue();
        // :( lost AnnotatedInterfaceA's SomeInheritableType1 annotation...

        final var acs2 = a2acs(ExtendClassB.class.getDeclaredAnnotations());
        assertThat(acs2).hasSize(1);
        assertThat(acs2.contains(SomeType3.class)).isTrue();

        // we can get "implements" interface types, but can't extract that's @Inherited annotations.
        final AnnotatedType[] atypes1 = ExtendClassB.class.getAnnotatedInterfaces();
        assertThat(atypes1.length).isEqualTo(2);
        assertThat(atypes1[0].getType().getTypeName()).isEqualTo(AnnotatedInterfaceA.class.getTypeName());
        final var acsi1 = a2acs(atypes1[0].getAnnotations());
        assertThat(acsi1).hasSize(0);
        final var acsi2 = a2acs(atypes1[0].getDeclaredAnnotations());
        assertThat(acsi2).hasSize(0);
        assertThat(atypes1[1].getType().getTypeName()).isEqualTo(NotAnnotatedInterfaceB.class.getTypeName());

        // we can get "extends" superclass type, but can't extract that's @Inherited annotations.
        final AnnotatedType atype2 = ExtendClassB.class.getAnnotatedSuperclass();
        assertThat(atype2.getType().getTypeName()).isEqualTo(AbstractClassA.class.getTypeName());
        final var acss1 = a2acs(atype2.getAnnotations());
        assertThat(acss1).hasSize(0);
        final var acss2 = a2acs(atype2.getDeclaredAnnotations());
        assertThat(acss2).hasSize(0);

        // super class field annotation remains :)
        final Field f1 = ExtendClassB.class.getField("m1");
        final var acsf1 = a2acs(f1.getAnnotations());
        assertThat(acsf1).hasSize(1);
        assertThat(acsf1.contains(SomeField1.class)).isTrue();

        // we can get "implements" interface default method annotation either @Inherited or not @Inherited.
        final Method defaultMethod1 = ExtendClassB.class.getMethod("defaultMethod1", int.class, int.class);
        final var acsDefaultMethod1 = a2acs(defaultMethod1.getAnnotations());
        assertThat(acsDefaultMethod1).hasSize(2);
        assertThat(acsDefaultMethod1.contains(SomeMethod1.class)).isTrue();
        assertThat(acsDefaultMethod1.contains(SomeInheritableMethod1.class)).isTrue();
        final var acsDefaultMethod1D = a2acs(defaultMethod1.getDeclaredAnnotations());
        assertThat(acsDefaultMethod1D).hasSize(2);
        assertThat(acsDefaultMethod1D.contains(SomeMethod1.class)).isTrue();
        assertThat(acsDefaultMethod1D.contains(SomeInheritableMethod1.class)).isTrue();

        Parameter[] parameters = defaultMethod1.getParameters();
        assertThat(parameters.length).isEqualTo(2);
        var acsParameters = a2acs(parameters[0].getAnnotations());
        assertThat(acsParameters).hasSize(1);
        assertThat(acsParameters.contains(SomeParameter1.class)).isTrue();
        acsParameters = a2acs(parameters[1].getAnnotations());
        assertThat(acsParameters).hasSize(1);
        assertThat(acsParameters.contains(SomeParameter2.class)).isTrue();

        /* totally, "implements"/"extends" method annotaions are lost.
         * (neither @Inherited nor not @Inherited)
         */

        final Method interfaceMethod1 = ExtendClassB.class.getMethod("interfaceMethod1", int.class);
        final var acsInterfaceMethod1 = a2acs(interfaceMethod1.getAnnotations());
        assertThat(acsInterfaceMethod1).hasSize(1);
        assertThat(acsInterfaceMethod1.contains(SomeMark1.class)).isTrue();
        final var acsInterfaceMethod1D = a2acs(interfaceMethod1.getDeclaredAnnotations());
        assertThat(acsInterfaceMethod1D).hasSize(1);
        assertThat(acsInterfaceMethod1D.contains(SomeMark1.class)).isTrue();

        parameters = interfaceMethod1.getParameters();
        assertThat(parameters.length).isEqualTo(1);
        acsParameters = a2acs(parameters[0].getAnnotations());
        assertThat(acsParameters).hasSize(1);
        assertThat(acsParameters.contains(SomeMark3.class)).isTrue();

        final Method interfaceMethod2 = ExtendClassB.class.getMethod("interfaceMethod2", int.class);
        final var acsInterfaceMethod2 = a2acs(interfaceMethod2.getAnnotations());
        assertThat(acsInterfaceMethod2).hasSize(0);
        final var acsInterfaceMethod2D = a2acs(interfaceMethod2.getDeclaredAnnotations());
        assertThat(acsInterfaceMethod2D).hasSize(0);

        parameters = interfaceMethod2.getParameters();
        assertThat(parameters.length).isEqualTo(1);
        acsParameters = a2acs(parameters[0].getAnnotations());
        assertThat(acsParameters).hasSize(0);

        final Method abstractMethod1 = ExtendClassB.class.getMethod("abstractMethod1", int.class);
        final var acsAbstractMethod1 = a2acs(abstractMethod1.getAnnotations());
        assertThat(acsAbstractMethod1).hasSize(1);
        assertThat(acsAbstractMethod1.contains(SomeMark2.class)).isTrue();
        final var acsAbstractMethod1D = a2acs(abstractMethod1.getDeclaredAnnotations());
        assertThat(acsAbstractMethod1D).hasSize(1);
        assertThat(acsAbstractMethod1D.contains(SomeMark2.class)).isTrue();

        parameters = abstractMethod1.getParameters();
        assertThat(parameters.length).isEqualTo(1);
        acsParameters = a2acs(parameters[0].getAnnotations());
        assertThat(acsParameters).hasSize(1);
        assertThat(acsParameters.contains(SomeMark3.class)).isTrue();

        final Method abstractMethod2 = ExtendClassB.class.getMethod("abstractMethod2", int.class);
        final var acsAbstractMethod2 = a2acs(abstractMethod2.getAnnotations());
        assertThat(acsAbstractMethod2).hasSize(0);
        final var acsAbstractMethod2D = a2acs(abstractMethod2.getDeclaredAnnotations());
        assertThat(acsAbstractMethod2D).hasSize(0);

        parameters = abstractMethod2.getParameters();
        assertThat(parameters.length).isEqualTo(1);
        acsParameters = a2acs(parameters[0].getAnnotations());
        assertThat(acsParameters).hasSize(0);
    }

    private static class AnnotationValuesDemo {
        @SomeFieldWithString(label = "aaa")
        public int withString1;

        @SomeFieldWithString(label = "bbb")
        public int withString2;

        @SomeFieldWithStringDefault(label = "ccc")
        public int withStringDefault1;

        @SomeFieldWithStringDefault()
        public int withStringDefault2;

        @SomeFieldWithStrings(labels = "ddd")
        public int withStrings1;

        @SomeFieldWithStrings(labels = { "eee", "fff" })
        public int withStrings2;

        @SomeFieldWithStringsDefault(labels = "ggg")
        public int withStringsDefault1;

        @SomeFieldWithStringsDefault(labels = { "hhh", "iii" })
        public int withStringsDefault2;

        @SomeFieldWithStringsDefault
        public int withStringsDefault3;

        @SomeFieldWithStringValue(value = "jjj")
        public int withStringValue1;

        @SomeFieldWithStringValue("kkk")
        public int withStringValue2;

        @SomeFieldWithStringValueDefault(value = "lll")
        public int withStringValueDefault1;

        @SomeFieldWithStringValueDefault("mmm")
        public int withStringValueDefault2;

        @SomeFieldWithStringValueDefault
        public int withStringValueDefault3;

        @SomeFieldWithIntAndStrings(intValue = 100, stringValues = { "nnn", "ooo" })
        public int withIntAndStrings1;

        @SomeFieldWithIntAndStrings(intValue = 200, stringValues = "ppp")
        public int withIntAndStrings2;

        @SomeFieldWithIntDefaultAndStrings(intValue = 300, stringValues = { "qqq", "rrr" })
        public int withIntDefaultAndStrings1;

        @SomeFieldWithIntDefaultAndStrings(stringValues = "ttt")
        public int withIntDefaultAndStrings2;
    }

    private static Field getFieldForAVD(final String name) throws NoSuchFieldException, SecurityException {
        return AnnotationValuesDemo.class.getField(name);
    }

    @Test
    public void testGetAnnotationValuesDemo() throws NoSuchFieldException, SecurityException {
        final var a1 = (SomeFieldWithString) getFieldForAVD("withString1").getAnnotation(SomeFieldWithString.class);
        assertThat(a1.label()).isEqualTo("aaa");
        final var a2 = (SomeFieldWithString) getFieldForAVD("withString2").getAnnotation(SomeFieldWithString.class);
        assertThat(a2.label()).isEqualTo("bbb");

        final var a3 = (SomeFieldWithStringDefault) getFieldForAVD("withStringDefault1")
                .getAnnotation(SomeFieldWithStringDefault.class);
        assertThat(a3.label()).isEqualTo("ccc");
        final var a4 = (SomeFieldWithStringDefault) getFieldForAVD("withStringDefault2")
                .getAnnotation(SomeFieldWithStringDefault.class);
        assertThat(a4.label()).isEqualTo("hello");

        final var a5 = (SomeFieldWithStrings) getFieldForAVD("withStrings1").getAnnotation(SomeFieldWithStrings.class);
        assertThat(a5.labels()).isEqualTo(new String[] { "ddd" });
        final var a6 = (SomeFieldWithStrings) getFieldForAVD("withStrings2").getAnnotation(SomeFieldWithStrings.class);
        assertThat(a6.labels()).isEqualTo(new String[] { "eee", "fff" });

        final var a7 = (SomeFieldWithStringsDefault) getFieldForAVD("withStringsDefault1")
                .getAnnotation(SomeFieldWithStringsDefault.class);
        assertThat(a7.labels()).isEqualTo(new String[] { "ggg" });

        final var a8 = (SomeFieldWithStringsDefault) getFieldForAVD("withStringsDefault2")
                .getAnnotation(SomeFieldWithStringsDefault.class);
        assertThat(a8.labels()).isEqualTo(new String[] { "hhh", "iii" });

        final var a9 = (SomeFieldWithStringsDefault) getFieldForAVD("withStringsDefault3")
                .getAnnotation(SomeFieldWithStringsDefault.class);
        assertThat(a9.labels()).isEqualTo(new String[] { "hello", "world" });

        final var a10 = (SomeFieldWithStringValue) getFieldForAVD("withStringValue1")
                .getAnnotation(SomeFieldWithStringValue.class);
        assertThat(a10.value()).isEqualTo("jjj");
        final var a11 = (SomeFieldWithStringValue) getFieldForAVD("withStringValue2")
                .getAnnotation(SomeFieldWithStringValue.class);
        assertThat(a11.value()).isEqualTo("kkk");

        final var a12 = (SomeFieldWithStringValueDefault) getFieldForAVD("withStringValueDefault1")
                .getAnnotation(SomeFieldWithStringValueDefault.class);
        assertThat(a12.value()).isEqualTo("lll");
        final var a13 = (SomeFieldWithStringValueDefault) getFieldForAVD("withStringValueDefault2")
                .getAnnotation(SomeFieldWithStringValueDefault.class);
        assertThat(a13.value()).isEqualTo("mmm");
        final var a14 = (SomeFieldWithStringValueDefault) getFieldForAVD("withStringValueDefault3")
                .getAnnotation(SomeFieldWithStringValueDefault.class);
        assertThat(a14.value()).isEqualTo("helloworld");

        final var a15 = (SomeFieldWithIntAndStrings) getFieldForAVD("withIntAndStrings1")
                .getAnnotation(SomeFieldWithIntAndStrings.class);
        assertThat(a15.intValue()).isEqualTo(100);
        assertThat(a15.stringValues()).isEqualTo(new String[] { "nnn", "ooo" });
        final var a16 = (SomeFieldWithIntAndStrings) getFieldForAVD("withIntAndStrings2")
                .getAnnotation(SomeFieldWithIntAndStrings.class);
        assertThat(a16.intValue()).isEqualTo(200);
        assertThat(a16.stringValues()).isEqualTo(new String[] { "ppp" });

        final var a17 = (SomeFieldWithIntDefaultAndStrings) getFieldForAVD("withIntDefaultAndStrings1")
                .getAnnotation(SomeFieldWithIntDefaultAndStrings.class);
        assertThat(a17.intValue()).isEqualTo(300);
        assertThat(a17.stringValues()).isEqualTo(new String[] { "qqq", "rrr" });
        final var a18 = (SomeFieldWithIntDefaultAndStrings) getFieldForAVD("withIntDefaultAndStrings2")
                .getAnnotation(SomeFieldWithIntDefaultAndStrings.class);
        assertThat(a18.intValue()).isEqualTo(-1);
        assertThat(a18.stringValues()).isEqualTo(new String[] { "ttt" });
    }
}
