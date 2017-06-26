/*
 * Copyright 2017 "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
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

import org.testng.annotations.Test;

/**
 * @see https://docs.oracle.com/javase/tutorial/java/nutsandbolts/datatypes.html
 * @see http://docs.oracle.com/javase/8/docs/technotes/guides/language/binary-literals.html
 * @see http://docs.oracle.com/javase/8/docs/technotes/guides/language/underscores-literals.html
 */
public class TestNumericLiterals {

    @Test
    public void binaryLiteral() {
        assertEquals(0b0, (byte) 0);
        assertEquals(0B1, (byte) 1);
        assertEquals(0b10, (byte) 2);
        assertEquals(0B11, (byte) 3);
        assertEquals(0b100, (byte) 4);
        assertEquals(0B0111_1111, 0x7F);
        assertEquals(0b1000_0000, 0x80);
        assertEquals(0B1111_1111, 0xFF);
        assertEquals(0b10000000_00000000_00000000_00000000, Integer.MIN_VALUE);
        assertEquals(0B11111111_11111111_11111111_11111111, -1);
        assertEquals(0b10000000_00000000_00000000_00000000_00000000_00000000_00000000_00000000L, Long.MIN_VALUE);
        assertEquals(0b11111111_11111111_11111111_11111111_11111111_11111111_11111111_11111111L, -1L);
    }

    @Test
    public void hexLiteral() {
        assertEquals(0x0, 0);
        assertEquals(0x1, 1);
        assertEquals(0xF, 15);
        assertEquals(0x7F, 127);
        assertEquals(0x80, 128);
        assertEquals(0xff, 255);
        assertEquals(0x7F_ff, Short.MAX_VALUE);
        assertEquals(0xCAFE_babeL, 3_405_691_582L);
    }

    @Test
    public void octalLiteral() {
        assertEquals(00, 0);
        assertEquals(01, 1);
        assertEquals(07, 7);
        assertEquals(010, 8);
        assertEquals(020, 16);
    }

    @Test
    public void underscoredLiteral() {
        assertEquals(1_1, 11);
        assertEquals(100_200_300, 100200300);
        assertEquals(100_200_300L, 100200300l);
        assertEquals(3.14_159, 3.14159);
        assertEquals(3.14_159F, 3.14159f);
    }
}
