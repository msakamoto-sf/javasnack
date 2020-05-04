/*
 * Copyright 2013 "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
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

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.Buffer;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.InvalidMarkException;

import org.junit.jupiter.api.Test;

public class TestByteBuffers {

    void assertPRL(final Buffer b, final int position, final int remaining, final int limit) {
        assertThat(b.position()).isEqualTo(position);
        assertThat(b.remaining()).isEqualTo(remaining);
        assertThat(b.limit()).isEqualTo(limit);
    }

    @Test
    public void demoPutFlipGetClear() {
        ByteBuffer b = ByteBuffer.allocate(12);
        assertThat(b.capacity()).isEqualTo(12);
        assertPRL(b, 0, 12, 12);
        ByteBuffer b2 = b.limit(10);
        assertThat(b).isEqualTo(b2);
        assertThat(b2.capacity()).isEqualTo(12);
        assertPRL(b, 0, 10, 10);
        assertPRL(b2, 0, 10, 10);

        b.put((byte) 1);
        assertPRL(b, 1, 9, 10);
        assertPRL(b2, 1, 9, 10);
        b.put((byte) 2);
        assertPRL(b, 2, 8, 10);
        assertPRL(b2, 2, 8, 10);
        b.putChar('a');
        assertPRL(b, 4, 6, 10);
        assertPRL(b2, 4, 6, 10);
        b.putShort((short) 3);
        assertPRL(b, 6, 4, 10);
        assertPRL(b2, 6, 4, 10);
        b.putInt(4);
        assertPRL(b, 10, 0, 10);
        assertPRL(b2, 10, 0, 10);
        assertThatThrownBy(() -> {
            b.put((byte) 5);
        }).isInstanceOf(BufferOverflowException.class);
        assertPRL(b, 10, 0, 10);
        assertPRL(b2, 10, 0, 10);

        ByteBuffer b3 = b.flip();
        assertThat(b).isEqualTo(b3);
        assertPRL(b, 0, 10, 10);
        assertPRL(b3, 0, 10, 10);
        assertThat(b.get()).isEqualTo((byte) 1);
        assertPRL(b, 1, 9, 10);
        assertPRL(b3, 1, 9, 10);
        assertThat(b3.get()).isEqualTo((byte) 2);
        assertPRL(b, 2, 8, 10);
        assertPRL(b3, 2, 8, 10);
        assertThat(b.getChar()).isEqualTo('a');
        assertPRL(b, 4, 6, 10);
        assertPRL(b3, 4, 6, 10);
        assertThat(b.getShort()).isEqualTo((short) 3);
        assertPRL(b, 6, 4, 10);
        assertPRL(b3, 6, 4, 10);
        assertThat(b.getInt()).isEqualTo((int) 4);
        assertPRL(b, 10, 0, 10);
        assertPRL(b3, 10, 0, 10);
        assertThatThrownBy(() -> {
            b.get();
        }).isInstanceOf(BufferUnderflowException.class);
        assertPRL(b, 10, 0, 10);
        assertPRL(b3, 10, 0, 10);

        ByteBuffer b4 = b.clear();
        assertThat(b).isEqualTo(b4);
        assertPRL(b, 0, 12, 12);
        assertPRL(b2, 0, 12, 12);
        assertPRL(b3, 0, 12, 12);
        assertPRL(b4, 0, 12, 12);
    }

    @Test
    public void demoPutFlipGetRewindGetAgain() {
        ByteBuffer b = ByteBuffer.allocate(12);
        b.put((byte) 1);
        b.put((byte) 2);
        b.putChar('a');
        b.putShort((short) 3);
        assertPRL(b, 6, 6, 12);

        ByteBuffer b2 = b.flip();
        assertThat(b).isEqualTo(b2);
        assertPRL(b, 0, 6, 6);
        assertPRL(b2, 0, 6, 6);
        assertThat(b2.get()).isEqualTo((byte) 1);
        assertThat(b2.get()).isEqualTo((byte) 2);
        assertThat(b2.getChar()).isEqualTo('a');
        assertPRL(b, 4, 2, 6);
        assertPRL(b2, 4, 2, 6);
        assertThatThrownBy(() -> {
            b.get();
            b.get();
            b.get();
        }).isInstanceOf(BufferUnderflowException.class);

        ByteBuffer b3 = b2.rewind();
        assertThat(b).isEqualTo(b3);
        assertPRL(b, 0, 6, 6);
        assertPRL(b2, 0, 6, 6);
        assertPRL(b3, 0, 6, 6);
        assertThat(b3.get()).isEqualTo((byte) 1);
        assertThat(b3.get()).isEqualTo((byte) 2);
        assertThat(b3.getChar()).isEqualTo('a');
        assertPRL(b, 4, 2, 6);
        assertPRL(b2, 4, 2, 6);
        assertPRL(b3, 4, 2, 6);
        assertThatThrownBy(() -> {
            b.get();
            b.get();
            b.get();
        }).isInstanceOf(BufferUnderflowException.class);
    }

    @Test
    public void demoMarkAndResetThenUpdateLimit() {
        ByteBuffer b = ByteBuffer.allocate(20);
        assertThatThrownBy(() -> {
            b.reset();
        }).isInstanceOf(InvalidMarkException.class);
        b.put((byte) 1);
        b.put((byte) 2);
        ByteBuffer b2 = b.mark();
        assertThat(b).isEqualTo(b2);
        b2.putChar('a');
        b2.putShort((short) 3);
        assertPRL(b, 6, 14, 20);
        assertPRL(b2, 6, 14, 20);

        ByteBuffer b3 = b.reset();
        assertThat(b).isEqualTo(b3);
        assertPRL(b, 2, 18, 20);
        assertPRL(b2, 2, 18, 20);
        assertPRL(b3, 2, 18, 20);
        b3.putChar('b');
        b3.putShort((short) 4);

        b.flip();
        assertPRL(b, 0, 6, 6);
        assertThat(b.get()).isEqualTo((byte) 1);
        assertThat(b.get()).isEqualTo((byte) 2);
        assertThat(b.getChar()).isEqualTo('b');
        assertThat(b.getShort()).isEqualTo((short) 4);
        assertPRL(b, 6, 0, 6);

        b.rewind();
        assertPRL(b, 0, 6, 6);
        b.limit(b.capacity());
        assertPRL(b, 0, 20, 20);
    }

    @Test
    public void testSlice() {
        ByteBuffer b = ByteBuffer.allocate(20);
        b.put((byte) 1);
        b.put((byte) 2);
        ByteBuffer b2 = b.slice();
        assertThat(b).isEqualTo(b2); // !?
        assertPRL(b, 2, 18, 20);
        assertPRL(b2, 0, 18, 18);

        b2.putChar('a');
        b2.putShort((short) 3);
        assertPRL(b, 2, 18, 20);
        assertPRL(b2, 4, 14, 18);

        assertThat(b.getChar()).isEqualTo('a');
        assertThat(b.getShort()).isEqualTo((short) 3);
        assertPRL(b, 6, 14, 20);
        assertPRL(b2, 4, 14, 18);
    }

    @Test
    public void testDuplicate() {
        ByteBuffer b = ByteBuffer.allocate(20);
        b.put((byte) 1);
        b.put((byte) 2);
        ByteBuffer b2 = b.duplicate();
        assertThat(b).isEqualTo(b2); // !?
        assertPRL(b, 2, 18, 20);
        assertPRL(b2, 2, 18, 20);

        b2.putChar('a');
        b2.putShort((short) 3);
        assertPRL(b, 2, 18, 20);
        assertPRL(b2, 6, 14, 20);

        assertThat(b.getChar()).isEqualTo('a');
        assertThat(b.getShort()).isEqualTo((short) 3);
        assertPRL(b, 6, 14, 20);
        assertPRL(b2, 6, 14, 20);
    }

    @Test
    public void testByteOrderAndBulkGetPut() {
        ByteBuffer b = ByteBuffer.allocate(20);
        assertThat(b.order()).isEqualTo(ByteOrder.BIG_ENDIAN);
        b.put((byte) 1);
        b.put((byte) 2);
        b.put((byte) 3);
        b.put((byte) 4);
        b.putShort((short) 0x0506);
        b.flip();
        assertThat(b.getShort()).isEqualTo((short) 0x0102);
        assertThat(b.getShort()).isEqualTo((short) 0x0304);
        assertThat(b.getShort()).isEqualTo((short) 0x0506);

        b.clear();
        b.order(ByteOrder.LITTLE_ENDIAN);
        b.put((byte) 1);
        b.put((byte) 2);
        b.put((byte) 3);
        b.put((byte) 4);
        b.putShort((short) 0x0605);
        b.flip();
        assertThat(b.getShort()).isEqualTo((short) 0x0201);
        assertThat(b.getShort()).isEqualTo((short) 0x0403);
        assertThat(b.getShort()).isEqualTo((short) 0x0605);
        assertPRL(b, 6, 0, 6);

        b.flip();
        assertPRL(b, 0, 6, 6);
        b.order(ByteOrder.BIG_ENDIAN);
        assertThatThrownBy(() -> {
            byte[] bytes0 = new byte[7];
            b.get(bytes0);
        }).isInstanceOf(BufferUnderflowException.class);
        byte[] bytes1 = new byte[6];
        b.get(bytes1);
        assertPRL(b, 6, 0, 6); // get(byte[]) updates position
        assertThat(bytes1).isEqualTo(new byte[] { 1, 2, 3, 4, 5, 6 });

        b.flip();
        assertPRL(b, 0, 6, 6);
        assertThatThrownBy(() -> {
            byte[] bytes0 = new byte[] { 1, 2, 3, 4, 5, 6, 7 };
            b.put(bytes0);
        }).isInstanceOf(BufferOverflowException.class);
        byte[] bytes2 = new byte[] { 8, 7, 9, 0, 1, 2 };
        b.put(bytes2);
        assertPRL(b, 6, 0, 6); // put(byte[]) updates position
        b.flip();
        byte[] bytes3 = new byte[6];
        b.get(bytes3);
        assertThat(bytes3).isEqualTo(bytes2);
    }

    @Test
    public void bufferOverflowMustOccurr() {
        ByteBuffer b = ByteBuffer.allocate(2);
        b.put((byte) 1);
        b.put((byte) 2);
        assertThat(b.position()).isEqualTo(2);
        assertThatThrownBy(() -> {
            b.put((byte) 3);
        }).isInstanceOf(BufferOverflowException.class);
    }

    @Test
    public void usageOfByteArrayOutputStream() throws UnsupportedEncodingException {
        // Allocate initial size as 2 byte.
        ByteArrayOutputStream o = new ByteArrayOutputStream(2);
        o.write((byte) 0);
        o.write((byte) 1);
        assertThat(o.size()).isEqualTo(2);
        // Add more 1 byte.
        o.write((byte) 2);
        // Size must expand 1.
        assertThat(o.size()).isEqualTo(3);

        // get string
        String s = o.toString("ISO-8859-1");
        // get byte array from string
        byte[] r = s.getBytes("ISO-8859-1");
        // original toByteArray() must be equal to r.
        assertThat(r).isEqualTo(o.toByteArray());
    }
}
