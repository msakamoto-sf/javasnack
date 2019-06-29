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

/* see:
 * http://stackoverflow.com/questions/5939575/generics-and-class-assubclass
 * http://waman.hatenablog.com/entry/20101007/1286440563
 */
public class TestClassAsSubclass {

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
    public void testAsSubclassSuccess() {
        Class<C1C1> inspector = C1C1.class;
        assertThat(inspector.asSubclass(C1C1.class)).isEqualTo(C1C1.class);
        assertThat(inspector.asSubclass(C1.class)).isEqualTo(C1C1.class);
        assertThat(inspector.asSubclass(P1.class)).isEqualTo(C1C1.class);

        Class<IMPIC1> inspector2 = IMPIC1.class;
        assertThat(inspector2.asSubclass(IMPIC1.class)).isEqualTo(IMPIC1.class);
        assertThat(inspector2.asSubclass(IC1.class)).isEqualTo(IMPIC1.class);
        assertThat(inspector2.asSubclass(IP1.class)).isEqualTo(IMPIC1.class);
    }

    @Test
    public void testAsSubclassFailure() {
        assertThatThrownBy(() -> {
            Class<C1C1> inspector = C1C1.class;
            inspector.asSubclass(C2C1.class);
        }).isInstanceOf(ClassCastException.class);
    }

    @Test
    public void testAsSubclassFailure2() {
        assertThatThrownBy(() -> {
            Class<C1C1> inspector = C1C1.class;
            inspector.asSubclass(C2.class);
        }).isInstanceOf(ClassCastException.class);
    }

    @Test
    public void testAsSubclassFailure3() {
        assertThatThrownBy(() -> {
            Class<C1C1> inspector = C1C1.class;
            inspector.asSubclass(P2.class);
        }).isInstanceOf(ClassCastException.class);
    }

    @Test
    public void testAsSubclassFailure4() {
        assertThatThrownBy(() -> {
            Class<P1> inspector = P1.class;
            inspector.asSubclass(C1C1.class);
        }).isInstanceOf(ClassCastException.class);
    }

    @Test
    public void testAsSubclassFailure5() {
        assertThatThrownBy(() -> {
            Class<P1> inspector = P1.class;
            inspector.asSubclass(C1.class);
        }).isInstanceOf(ClassCastException.class);
    }

    @Test
    public void testAsSubclassFailure6() {
        assertThatThrownBy(() -> {
            Class<IP1> inspector = IP1.class;
            inspector.asSubclass(IMPIC1.class);
        }).isInstanceOf(ClassCastException.class);
    }

    @Test
    public void testAsSubclassFailure7() {
        assertThatThrownBy(() -> {
            Class<IP1> inspector = IP1.class;
            inspector.asSubclass(IC1.class);
        }).isInstanceOf(ClassCastException.class);
    }
}
