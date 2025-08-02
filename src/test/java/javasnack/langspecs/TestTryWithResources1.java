/*
 * Copyright 2016 "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
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
import static org.assertj.core.api.Assertions.catchThrowable;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

/**
 * Basic try-with-resources examples.
 */
/* see:
 * http://docs.oracle.com/javase/jp/7/technotes/guides/language/try-with-resources.html
 * http://d.hatena.ne.jp/chiheisen/20110724/1311484252
 * 
 * Advanced Topics (resource leak : not tested)
 * http://qiita.com/nesheep5/items/6a68d862c5902e5994a4
 * http://mike-neck.hatenadiary.com/entry/2015/04/12/210000
 * 
 * テストコードの性質上、宣言しても使わない変数が大量に発生するため、
 * クラス単位で unused warning を抑制する。
 */
@SuppressWarnings("unused")
public class TestTryWithResources1 {

    @Test
    public void testSingleResourceClose() throws IOException {
        class Closeable1 implements Closeable {
            final AtomicInteger ai;

            public Closeable1(AtomicInteger ai) {
                this.ai = ai;
            }

            @Override
            public void close() throws IOException {
                ai.addAndGet(1);
            }
        }

        final AtomicInteger ai = new AtomicInteger(0);
        int dummy = 0;
        try (Closeable1 c1 = new Closeable1(ai)) {
            if (Math.random() > 1.0) {
                dummy++; // avoid JIT optimization (not to skip try-with-resources block)
            }
        }
        assertThat(ai.get()).isEqualTo(1);
    }

    @Test
    public void testMultipleResourceClose() throws IOException {
        class Closeable1 implements Closeable {
            final String name;
            final List<String> messageBox;

            public Closeable1(String name, List<String> messageBox) {
                this.name = name;
                this.messageBox = messageBox;
            }

            @Override
            public void close() throws IOException {
                this.messageBox.add("CLOSED:" + this.name);
            }
        }

        List<String> messageBox = new ArrayList<>();
        int dummy = 0;
        try (Closeable1 c1 = new Closeable1("res1", messageBox);
                Closeable1 c2 = new Closeable1("res2", messageBox);
                Closeable1 c3 = new Closeable1("res3", messageBox);) {
            if (Math.random() > 1.0) {
                dummy++; // avoid JIT optimization (not to skip try-with-resources block)
            }
        }
        // close() WAS CALLED, ALREADY.
        assertThat(messageBox).hasSize(3);
        assertThat(messageBox.get(0)).isEqualTo("CLOSED:res3");
        assertThat(messageBox.get(1)).isEqualTo("CLOSED:res2");
        assertThat(messageBox.get(2)).isEqualTo("CLOSED:res1");
    }

    @Test
    public void testCloseThrowsIOException() throws IOException {
        class Closeable1 implements Closeable {

            @Override
            public void close() throws IOException {
                int r = (int) (Math.random() * 10);
                if (r < 1000) {
                    // always true, let's throw !! :)
                    throw new IOException("dummy");
                }
            }
        }

        assertThatThrownBy(() -> {
            int dummy = 0;
            try (Closeable1 c1 = new Closeable1();) {
                if (Math.random() > 1.0) {
                    dummy++; // avoid JIT optimization (not to skip try-with-resources block)
                }
            }
        }).isInstanceOf(IOException.class);
    }

    @Test
    public void testAutoCloseThrowsException() throws Exception {
        class Closeable1 implements AutoCloseable {

            @Override
            public void close() throws Exception {
                int r = (int) (Math.random() * 10);
                if (r < 1000) {
                    // always true, let's throw !! :)
                    throw new Exception("dummy");
                }
            }
        }

        assertThatThrownBy(() -> {
            int dummy = 0;
            try (Closeable1 c1 = new Closeable1();) {
                if (Math.random() > 1.0) {
                    dummy++; // avoid JIT optimization (not to skip try-with-resources block)
                }
            }
        }).isInstanceOf(Exception.class);
    }

    @Test
    public void testResourceOperationThrowsException() {
        class Closeable1 implements Closeable {
            public void throwSomething() throws Exception {
                int r = (int) (Math.random() * 10);
                if (r < 1000) {
                    // always true, let's throw !! :)
                    throw new Exception("dummy");
                }
            }

            @Override
            public void close() throws IOException {
                // dummy
            }
        }

        final Throwable thrown0 = catchThrowable(() -> {
            try (Closeable1 c1 = new Closeable1();) {
                c1.throwSomething();
            }
        });
        assertThat(thrown0).hasMessage("dummy");
        final Throwable[] suppressed = thrown0.getSuppressed();
        assertThat(suppressed).hasSize(0);
    }

    @Test
    public void testSingleResourceOperationAndCloseThrowExceptions() {
        class Closeable1 implements Closeable {
            public void throwSomething() throws Exception {
                int r = (int) (Math.random() * 10);
                if (r < 1000) {
                    // always true, let's throw !! :)
                    throw new Exception("dummy1");
                }
            }

            @Override
            public void close() throws IOException {
                int r = (int) (Math.random() * 10);
                if (r < 1000) {
                    // always true, let's throw !! :)
                    throw new IOException("dummy2");
                }
            }
        }

        final Throwable thrown0 = catchThrowable(() -> {
            try (Closeable1 c1 = new Closeable1();) {
                c1.throwSomething();
            }
        });
        assertThat(thrown0).hasMessage("dummy1");
        final Throwable[] suppressed = thrown0.getSuppressed();
        assertThat(suppressed).hasSize(1);
        assertThat(suppressed[0].getMessage()).isEqualTo("dummy2");
    }

    @Test
    public void testMultipleResourceOperationAndCloseThrowExceptions() {
        class Closeable1 implements Closeable {
            final String name;
            final List<String> messageBox;

            public Closeable1(String name, List<String> messageBox) {
                this.name = name;
                this.messageBox = messageBox;
            }

            public void throwSomething() throws Exception {
                int r = (int) (Math.random() * 10);
                if (r < 1000) {
                    // always true, let's throw !! :)
                    throw new Exception("dummy1:" + this.name);
                }
            }

            @Override
            public void close() throws IOException {
                this.messageBox.add("CLOSED:" + this.name);
                int r = (int) (Math.random() * 10);
                if (r < 1000) {
                    // always true, let's throw !! :)
                    throw new IOException("dummy2:" + this.name);
                }
            }
        }

        List<String> messageBox = new ArrayList<>();
        final Throwable thrown0 = catchThrowable(() -> {
            try (Closeable1 c1 = new Closeable1("res1", messageBox);
                    Closeable1 c2 = new Closeable1("res2", messageBox);
                    Closeable1 c3 = new Closeable1("res3", messageBox);) {
                c2.throwSomething();
            }
        });
        assertThat(thrown0).hasMessage("dummy1:res2");
        final Throwable[] suppressed = thrown0.getSuppressed();
        assertThat(suppressed).hasSize(3);
        assertThat(suppressed[0].getMessage()).isEqualTo("dummy2:res3");
        assertThat(suppressed[1].getMessage()).isEqualTo("dummy2:res2");
        assertThat(suppressed[2].getMessage()).isEqualTo("dummy2:res1");

        // close() WAS CALLED, ALREADY.
        assertThat(messageBox).hasSize(3);
        assertThat(messageBox.get(0)).isEqualTo("CLOSED:res3");
        assertThat(messageBox.get(1)).isEqualTo("CLOSED:res2");
        assertThat(messageBox.get(2)).isEqualTo("CLOSED:res1");
    }
}
