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
import static org.assertj.core.api.Assertions.fail;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * try-with-resources with "catch" examples.
 */
/* see:
 * http://docs.oracle.com/javase/jp/7/technotes/guides/language/try-with-resources.html
 * http://d.hatena.ne.jp/sardine/20130402
 */
public class TestTryWithResources2 {

    @Test
    public void testCloseThrowsIOException() {
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

        @SuppressWarnings("unused")
        int dummy = 0;
        try (Closeable1 c1 = new Closeable1();) {
            if (Math.random() > 1.0) {
                dummy++; // avoid JIT optimization (not to skip try-with-resources block)
            }
        } catch (Exception e) {
            assertThat(e.getClass()).isEqualTo(IOException.class);
            assertThat(e.getMessage()).isEqualTo("dummy");
            assertThat(e.getSuppressed()).hasSize(0);
        }
    }

    @Test
    public void testAutoCloseThrowsException() {
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

        @SuppressWarnings("unused")
        int dummy = 0;
        try (Closeable1 c1 = new Closeable1();) {
            if (Math.random() > 1.0) {
                dummy++; // avoid JIT optimization (not to skip try-with-resources block)
            }
        } catch (Exception e) {
            assertThat(e.getClass()).isEqualTo(Exception.class);
            assertThat(e.getMessage()).isEqualTo("dummy");
            assertThat(e.getSuppressed()).hasSize(0);
        }
    }

    @Test
    public void testResourceOperationThrowsException() throws IOException {
        class Closeable1 implements Closeable {
            public void throwSomething() throws IllegalArgumentException {
                int r = (int) (Math.random() * 10);
                if (r < 1000) {
                    // always true, let's throw !! :)
                    throw new IllegalArgumentException("dummy");
                }
            }

            @Override
            public void close() throws IOException {
                // dummy
            }
        }

        try {
            try (Closeable1 c1 = new Closeable1();) {
                c1.throwSomething();
            }
            fail("NEVER REACH HERE.");
        } catch (IllegalArgumentException e) {
            // REACHE HERE:
            assertThat(e.getMessage()).isEqualTo("dummy");
            Throwable[] suppressed = e.getSuppressed();
            assertThat(suppressed).hasSize(0);
        }
    }

    @Test
    public void testSingleResourceOperationAndCloseThrowExceptions() {
        class Closeable1 implements Closeable {
            public void throwSomething() throws IllegalArgumentException {
                int r = (int) (Math.random() * 10);
                if (r < 1000) {
                    // always true, let's throw !! :)
                    throw new IllegalArgumentException("dummy1");
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

        try {
            try (Closeable1 c1 = new Closeable1();) {
                c1.throwSomething();
            }
            fail("NEVER REACH HERE.");
        } catch (IOException e) {
            fail("NEVER REACH HERE, TOO.");
        } catch (IllegalArgumentException e) {
            // REACHE HERE:
            assertThat(e.getMessage()).isEqualTo("dummy1");
            Throwable[] suppressed = e.getSuppressed();
            assertThat(suppressed).hasSize(1);
            assertThat(suppressed[0].getMessage()).isEqualTo("dummy2");
            assertThat(suppressed[0].getClass()).isEqualTo(IOException.class);
        }
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
        try (Closeable1 c1 = new Closeable1("res1", messageBox);
                Closeable1 c2 = new Closeable1("res2", messageBox);
                Closeable1 c3 = new Closeable1("res3", messageBox);) {
            c2.throwSomething();
            fail("NEVER REACH HERE.");
        } catch (IOException e) {
            fail("NEVER REACH HERE, TOO.");
        } catch (Exception e) {
            // REACHE HERE:

            // close() WAS CALLED, ALREADY.
            assertThat(messageBox).hasSize(3);
            assertThat(messageBox.get(0)).isEqualTo("CLOSED:res3");
            assertThat(messageBox.get(1)).isEqualTo("CLOSED:res2");
            assertThat(messageBox.get(2)).isEqualTo("CLOSED:res1");

            assertThat(e.getMessage()).isEqualTo("dummy1:res2");
            Throwable[] suppressed = e.getSuppressed();
            assertThat(suppressed).hasSize(3);
            assertThat(suppressed[0].getMessage()).isEqualTo("dummy2:res3");
            assertThat(suppressed[1].getMessage()).isEqualTo("dummy2:res2");
            assertThat(suppressed[2].getMessage()).isEqualTo("dummy2:res1");
        }
    }
}
