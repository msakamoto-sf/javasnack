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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.testng.annotations.Test;

/**
 * Basic try-with-resources examples.
 * 
 * @see http://docs.oracle.com/javase/jp/7/technotes/guides/language/try-with-resources.html
 * @see http://d.hatena.ne.jp/chiheisen/20110724/1311484252
 * 
 * Advanced Topics (resource leak : not tested)
 * @see http://qiita.com/nesheep5/items/6a68d862c5902e5994a4
 * @see http://mike-neck.hatenadiary.com/entry/2015/04/12/210000
 */
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
        @SuppressWarnings("unused")
        int dummy = 0;
        try (Closeable1 c1 = new Closeable1(ai)) {
            if (Math.random() > 1.0) {
                dummy++; // avoid JIT optimization (not to skip try-with-resources block)
            }
        }
        assertEquals(ai.get(), 1);
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
        @SuppressWarnings("unused")
        int dummy = 0;
        try (Closeable1 c1 = new Closeable1("res1", messageBox);
                Closeable1 c2 = new Closeable1("res2", messageBox);
                Closeable1 c3 = new Closeable1("res3", messageBox);) {
            if (Math.random() > 1.0) {
                dummy++; // avoid JIT optimization (not to skip try-with-resources block)
            }
        }
        // close() WAS CALLED, ALREADY.
        assertEquals(messageBox.size(), 3);
        assertEquals(messageBox.get(0), "CLOSED:res3");
        assertEquals(messageBox.get(1), "CLOSED:res2");
        assertEquals(messageBox.get(2), "CLOSED:res1");
    }

    @Test(expectedExceptions = { java.io.IOException.class })
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
        @SuppressWarnings("unused")
        int dummy = 0;
        try (Closeable1 c1 = new Closeable1();) {
            if (Math.random() > 1.0) {
                dummy++; // avoid JIT optimization (not to skip try-with-resources block)
            }
        }
    }

    @Test(expectedExceptions = { java.lang.Exception.class })
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
        @SuppressWarnings("unused")
        int dummy = 0;
        try (Closeable1 c1 = new Closeable1();) {
            if (Math.random() > 1.0) {
                dummy++; // avoid JIT optimization (not to skip try-with-resources block)
            }
        }
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
        try {
            try (Closeable1 c1 = new Closeable1();) {
                c1.throwSomething();
            }
            fail(); // NEVER REACH HERE.
        } catch (Exception e) {
            // REACHE HERE:
            assertEquals(e.getMessage(), "dummy");
            Throwable[] suppressed = e.getSuppressed();
            assertEquals(suppressed.length, 0);
        }
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
        try {
            try (Closeable1 c1 = new Closeable1();) {
                c1.throwSomething();
            }
            fail(); // NEVER REACH HERE.
        } catch (Exception e) {
            // REACHE HERE:
            assertEquals(e.getMessage(), "dummy1");
            Throwable[] suppressed = e.getSuppressed();
            assertEquals(suppressed.length, 1);
            assertEquals(suppressed[0].getMessage(), "dummy2");
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
        try {
            try (Closeable1 c1 = new Closeable1("res1", messageBox);
                    Closeable1 c2 = new Closeable1("res2", messageBox);
                    Closeable1 c3 = new Closeable1("res3", messageBox);) {
                c2.throwSomething();
            }
            fail(); // NEVER REACH HERE.
        } catch (Exception e) {
            // REACHE HERE:

            // close() WAS CALLED, ALREADY.
            assertEquals(messageBox.size(), 3);
            assertEquals(messageBox.get(0), "CLOSED:res3");
            assertEquals(messageBox.get(1), "CLOSED:res2");
            assertEquals(messageBox.get(2), "CLOSED:res1");

            assertEquals(e.getMessage(), "dummy1:res2");
            Throwable[] suppressed = e.getSuppressed();
            assertEquals(suppressed.length, 3);
            assertEquals(suppressed[0].getMessage(), "dummy2:res3");
            assertEquals(suppressed[1].getMessage(), "dummy2:res2");
            assertEquals(suppressed[2].getMessage(), "dummy2:res1");
        }
    }
}
