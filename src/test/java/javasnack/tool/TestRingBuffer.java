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

package javasnack.tool;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

/* バッファの先頭と後ろが連続した構造の、ring or circular バッファ。
 * 検索するといろいろな実装例がある:
 * http://www.java2s.com/Tutorial/Java/0140__Collections/CircularBuffer.htm
 * http://www.vias.org/javacourse/chap16_04.html
 * http://blog.k11i.biz/2013/03/java-blockingqueue.html
 * http://stackoverflow.com/questions/7266042/java-ring-buffer
 * http://www.museful.net/2012/software-development/circulararraylist-for-java
 * http://www.bohyoh.com/Books/MeikaiJavaAlgo/EX/ALGOEX0908.html
 * http://www.bohyoh.com/Books/JAlgoData/EX/ALGOEX0908.html
 * 
 * こちらは非常に単純な、1バイトずつread/writeするring bufferの作例デモ。
 */
public class TestRingBuffer {

    static class ToyByteArrayRingBuffer {
        final byte[] data;
        final int size;
        int readCursor = 0;
        int writeCursor = 0;

        ToyByteArrayRingBuffer(byte[] data) {
            if (data.length == 0) {
                throw new IllegalArgumentException("zero size array is not allowed.");
            }
            this.data = data;
            this.size = data.length;
        }

        byte readByte() {
            final int i = readCursor % size;
            readCursor++;
            return data[i];
        }

        void writeByte(final byte v) {
            final int i = writeCursor % size;
            writeCursor++;
            data[i] = v;
        }
    }

    @Test
    public void testToyByteArrayRingBuffer() {
        final byte[] bytes3 = { 1, 2, 3 };
        final var buf3 = new ToyByteArrayRingBuffer(bytes3);
        assertThat(buf3.readByte()).isEqualTo((byte) 1);
        assertThat(buf3.readByte()).isEqualTo((byte) 2);
        assertThat(buf3.readByte()).isEqualTo((byte) 3);
        assertThat(buf3.readByte()).isEqualTo((byte) 1);
        assertThat(buf3.readByte()).isEqualTo((byte) 2);
        assertThat(buf3.readByte()).isEqualTo((byte) 3);
        buf3.writeByte((byte) 4);
        buf3.writeByte((byte) 5);
        buf3.writeByte((byte) 6);
        buf3.writeByte((byte) 7);
        assertThat(buf3.readByte()).isEqualTo((byte) 7);
        assertThat(buf3.readByte()).isEqualTo((byte) 5);
        assertThat(buf3.readByte()).isEqualTo((byte) 6);
        assertThat(buf3.readByte()).isEqualTo((byte) 7);
        assertThat(buf3.readByte()).isEqualTo((byte) 5);
        assertThat(buf3.readByte()).isEqualTo((byte) 6);

        final var buf1 = new ToyByteArrayRingBuffer(new byte[] { 1 });
        assertThat(buf1.readByte()).isEqualTo((byte) 1);
        assertThat(buf1.readByte()).isEqualTo((byte) 1);
        assertThat(buf1.readByte()).isEqualTo((byte) 1);
        buf1.writeByte((byte) 2);
        assertThat(buf1.readByte()).isEqualTo((byte) 2);
        assertThat(buf1.readByte()).isEqualTo((byte) 2);
        buf1.writeByte((byte) 2);
        buf1.writeByte((byte) 2);
        assertThat(buf1.readByte()).isEqualTo((byte) 2);
        assertThat(buf1.readByte()).isEqualTo((byte) 2);

        final var buf2 = new ToyByteArrayRingBuffer(new byte[] { 1, 2 });
        assertThat(buf2.readByte()).isEqualTo((byte) 1);
        assertThat(buf2.readByte()).isEqualTo((byte) 2);
        assertThat(buf2.readByte()).isEqualTo((byte) 1);
        buf2.writeByte((byte) 3);
        assertThat(buf2.readByte()).isEqualTo((byte) 2);
        assertThat(buf2.readByte()).isEqualTo((byte) 3);
        buf2.writeByte((byte) 4);
        buf2.writeByte((byte) 5);
        buf2.writeByte((byte) 6);
        assertThat(buf2.readByte()).isEqualTo((byte) 6);
        assertThat(buf2.readByte()).isEqualTo((byte) 5);

        assertThatThrownBy(() -> {
            new ToyByteArrayRingBuffer(new byte[0]);
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("zero size array is not allowed.");
    }
}
