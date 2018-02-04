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

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

/**
 * @see http://www.java2s.com/Tutorial/Java/0140__Collections/CircularBuffer.htm
 * @see http://www.vias.org/javacourse/chap16_04.html
 * @see http://blog.k11i.biz/2013/03/java-blockingqueue.html
 * @see http://stackoverflow.com/questions/7266042/java-ring-buffer
 * @see https://commons.apache.org/proper/commons-collections/javadocs/api-3.2.1/org/apache/commons/collections/buffer/CircularFifoBuffer.html
 * @see http://www.museful.net/2012/software-development/circulararraylist-for-java
 * @see http://www.bohyoh.com/Books/MeikaiJavaAlgo/EX/ALGOEX0908.html
 * @see http://www.bohyoh.com/Books/JAlgoData/EX/ALGOEX0908.html
 */
public class TestRingBuffer {

    class ArrayRingBuffer<T> {
        final List<T> buffer;
        final int capacity;

        public ArrayRingBuffer(int capacity) {
            this.capacity = capacity;
            this.buffer = new ArrayList<>(capacity);
        }

        public T add(T newElement) {
            T r = null;
            if (0 == buffer.size()) {
                return r;
            }
            if (this.capacity == buffer.size()) {
                r = buffer.get(0);
                buffer.add(0, newElement);
                return r;
            }
            buffer.add(newElement);
            return r;
        }
    }

    @Test(expectedExceptions = { java.lang.IllegalStateException.class })
    public void testAsSubclassFailure7() {
    }

    @Test(expectedExceptions = { java.lang.IndexOutOfBoundsException.class })
    public void testAsSubclassFailure8() {
    }
}
