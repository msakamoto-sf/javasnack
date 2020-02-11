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

package javasnack.classgraph;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.AnnotationParameterValueList;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;

/* reference:
 * - classgraph/classgraph: An uber-fast, ultra-lightweight, parallelized Java classpath scanner and module scanner.
 *   - https://github.com/classgraph/classgraph
 * - Code examples · classgraph/classgraph Wiki
 *   - https://github.com/classgraph/classgraph/wiki/Code-examples
 * - 特定のアノテーションが付与されたクラスを実行時に抽出したい - 覚えたら書く
 *   - https://blog.y-yuki.net/entry/2017/01/31/124000
 */
public class TestClassGraphDemo {

    void assertSingleClassName(final ClassInfoList classInfoList, final Class<?> expectedClazz) {
        assertThat(classInfoList).hasSize(1);
        final ClassInfo classInfo = classInfoList.get(0);
        assertThat(classInfo.getName()).isEqualTo(expectedClazz.getName());
    }

    @Test
    public void testScanClassesDemo() {
        try (ScanResult scanResult = new ClassGraph()
                .verbose()
                .enableAllInfo()
                .whitelistPackages(this.getClass().getPackageName())
                .scan()) {

            ClassInfoList classInfoList = scanResult.getClassesWithAnnotation(SomeMark1.class.getName());
            assertThat(classInfoList).hasSize(1);
            ClassInfo classInfo = classInfoList.get(0);
            assertThat(classInfo.getName()).isEqualTo(ConcreteClass1.class.getName());
            AnnotationInfo annotationInfo = classInfo.getAnnotationInfo(SomeMark1.class.getName());
            AnnotationParameterValueList parameterValues = annotationInfo.getParameterValues();
            assertThat(parameterValues).hasSize(2);
            assertThat(parameterValues.getValue("value")).isEqualTo("mark1");
            assertThat(parameterValues.getValue("extra")).isEqualTo("extra1");

            classInfoList = scanResult.getClassesWithAnnotation(SomeMark2.class.getName());
            assertThat(classInfoList).hasSize(1);
            classInfo = classInfoList.get(0);
            assertThat(classInfo.getName()).isEqualTo(ConcreteClass2.class.getName());
            annotationInfo = classInfo.getAnnotationInfo(SomeMark2.class.getName());
            parameterValues = annotationInfo.getParameterValues();
            assertThat(parameterValues).hasSize(2);
            assertThat(parameterValues.getValue("value")).isEqualTo("mark2");
            assertThat(parameterValues.getValue("extra")).isEqualTo("extra2");

            classInfoList = scanResult.getClassesWithAnnotation(SomeType1.class.getName());
            assertThat(classInfoList).hasSize(1);
            classInfo = classInfoList.get(0);
            assertThat(classInfo.getName()).isEqualTo(ConcreteClass1.class.getName());
            annotationInfo = classInfo.getAnnotationInfo(SomeType1.class.getName());
            parameterValues = annotationInfo.getParameterValues();
            assertThat(parameterValues).hasSize(0);

            classInfoList = scanResult.getClassesWithAnnotation(SomeType2.class.getName());
            assertThat(classInfoList).hasSize(1);
            classInfo = classInfoList.get(0);
            assertThat(classInfo.getName()).isEqualTo(ConcreteClass2.class.getName());
            annotationInfo = classInfo.getAnnotationInfo(SomeType2.class.getName());
            parameterValues = annotationInfo.getParameterValues();
            assertThat(parameterValues).hasSize(0);

            assertSingleClassName(
                    scanResult.getClassesImplementing(SomeInterface1.class.getName()),
                    ConcreteClass1.class);
            assertSingleClassName(
                    scanResult.getClassesImplementing(SomeInterface2.class.getName()),
                    ConcreteClass2.class);

            assertSingleClassName(
                    scanResult.getSubclasses(SomeAbstractClass1.class.getName()),
                    ConcreteClass1.class);
            assertSingleClassName(
                    scanResult.getSubclasses(SomeAbstractClass2.class.getName()),
                    ConcreteClass2.class);

            assertSingleClassName(
                    scanResult.getClassesWithFieldAnnotation(SomeMark1.class.getName()),
                    ConcreteClass1.class);
            assertSingleClassName(
                    scanResult.getClassesWithFieldAnnotation(SomeMark2.class.getName()),
                    ConcreteClass2.class);

            assertSingleClassName(
                    scanResult.getClassesWithFieldAnnotation(SomeField1.class.getName()),
                    ConcreteClass1.class);
            assertSingleClassName(
                    scanResult.getClassesWithFieldAnnotation(SomeField2.class.getName()),
                    ConcreteClass2.class);

            assertSingleClassName(
                    scanResult.getClassesWithMethodAnnotation(SomeMark1.class.getName()),
                    ConcreteClass1.class);
            assertSingleClassName(
                    scanResult.getClassesWithMethodAnnotation(SomeMark2.class.getName()),
                    ConcreteClass2.class);

            assertSingleClassName(
                    scanResult.getClassesWithMethodAnnotation(SomeMethod1.class.getName()),
                    ConcreteClass1.class);
            assertSingleClassName(
                    scanResult.getClassesWithMethodAnnotation(SomeMethod2.class.getName()),
                    ConcreteClass2.class);
        }
    }
}
