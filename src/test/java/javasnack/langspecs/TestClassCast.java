/*
 * Copyright 2015 "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
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

package javasnack.langspecs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

public class TestClassCast {

    class P1 {
    }

    class C1 extends P1 {
    }

    class C1C1 extends C1 {
    }

    class P2 {
    }

    class C2 extends P2 {
    }

    class C2C1 extends C2 {
    }

    interface IP1 {
    }

    interface IC1 extends IP1 {
    }

    class IMPIC1 implements IC1 {
    }

    interface IP2 {
    }

    interface IC2 extends IP2 {
    }

    class IMPIC2 implements IC2 {
    }

    @Test
    public void castSuccess() {
        Object o1 = new C1C1();
        Object p1 = P1.class.cast(o1);
        assertThat(p1.getClass().getSimpleName()).isEqualTo("C1C1");
        assertThat(p1.getClass().getName()).isEqualTo("javasnack.langspecs.TestClassCast$C1C1");
        assertThat(p1.getClass().getCanonicalName()).isEqualTo("javasnack.langspecs.TestClassCast.C1C1");

        Object c1 = new C1();
        Object c1b = c1.getClass().cast(o1);
        assertThat(c1b.getClass().getSimpleName()).isEqualTo("C1C1");
        assertThat(c1b.getClass().getName()).isEqualTo("javasnack.langspecs.TestClassCast$C1C1");
        assertThat(c1b.getClass().getCanonicalName()).isEqualTo("javasnack.langspecs.TestClassCast.C1C1");

        Object o2 = new C2C1();
        Object p2 = P2.class.cast(o2);
        assertThat(p2.getClass().getSimpleName()).isEqualTo("C2C1");
        assertThat(p2.getClass().getName()).isEqualTo("javasnack.langspecs.TestClassCast$C2C1");
        assertThat(p2.getClass().getCanonicalName()).isEqualTo("javasnack.langspecs.TestClassCast.C2C1");

        Object c2 = new C2();
        Object c2b = c2.getClass().cast(o2);
        assertThat(c2b.getClass().getSimpleName()).isEqualTo("C2C1");
        assertThat(c2b.getClass().getName()).isEqualTo("javasnack.langspecs.TestClassCast$C2C1");
        assertThat(c2b.getClass().getCanonicalName()).isEqualTo("javasnack.langspecs.TestClassCast.C2C1");

        Object imp1 = new IMPIC1();
        IP1.class.cast(imp1);
        IC1.class.cast(imp1);
        Object imp2 = new IMPIC2();
        IP2.class.cast(imp2);
        IC2.class.cast(imp2);
    }

    @Test
    public void castFailure1() {
        assertThatThrownBy(() -> {
            P1 p1 = new P1();
            P2.class.cast(p1);
        }).isInstanceOf(ClassCastException.class);
    }

    @Test
    public void castFailure2() {
        assertThatThrownBy(() -> {
            P2 p2 = new C2C1();
            C1C1 c1c1 = new C1C1();
            c1c1.getClass().cast(p2);
        }).isInstanceOf(ClassCastException.class);
    }

    @Test
    public void castFailure3() {
        assertThatThrownBy(() -> {
            IP1 ip1 = new IMPIC1();
            IC2.class.cast(ip1);
        }).isInstanceOf(ClassCastException.class);
    }
}
