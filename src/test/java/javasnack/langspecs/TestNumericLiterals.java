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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/* see:
 * https://docs.oracle.com/javase/tutorial/java/nutsandbolts/datatypes.html
 * http://docs.oracle.com/javase/8/docs/technotes/guides/language/binary-literals.html
 * http://docs.oracle.com/javase/8/docs/technotes/guides/language/underscores-literals.html
 */
public class TestNumericLiterals {

    @Test
    public void binaryLiteral() {
        assertThat(0b0).isEqualTo((byte) 0);
        assertThat(0B1).isEqualTo((byte) 1);
        assertThat(0b10).isEqualTo((byte) 2);
        assertThat(0B11).isEqualTo((byte) 3);
        assertThat(0b100).isEqualTo((byte) 4);
        assertThat(0B0111_1111).isEqualTo(0x7F);
        assertThat(0b1000_0000).isEqualTo(0x80);
        assertThat(0B1111_1111).isEqualTo(0xFF);
        assertThat(0b10000000_00000000_00000000_00000000).isEqualTo(Integer.MIN_VALUE);
        assertThat(0B11111111_11111111_11111111_11111111).isEqualTo(-1);
        assertThat(0b10000000_00000000_00000000_00000000_00000000_00000000_00000000_00000000L)
                .isEqualTo(Long.MIN_VALUE);
        assertThat(0b11111111_11111111_11111111_11111111_11111111_11111111_11111111_11111111L).isEqualTo(-1L);
    }

    @Test
    public void hexLiteral() {
        assertThat(0x0).isEqualTo(0);
        assertThat(0x1).isEqualTo(1);
        assertThat(0xF).isEqualTo(15);
        assertThat(0x7F).isEqualTo(127);
        assertThat(0x80).isEqualTo(128);
        assertThat(0xff).isEqualTo(255);
        assertThat(0x7F_ff).isEqualTo(Short.MAX_VALUE);
        assertThat(0xCAFE_babeL).isEqualTo(3_405_691_582L);
    }

    @Test
    public void octalLiteral() {
        assertThat(00).isEqualTo(0);
        assertThat(01).isEqualTo(1);
        assertThat(07).isEqualTo(7);
        // octal リテラルの意図的なデモのため、PMD:AvoidUsingOctalValues の警告を行単位で無効化
        // see: https://pmd.github.io/latest/pmd_userdocs_suppressing_warnings.html#nopmd-comment
        assertThat(010).isEqualTo(8); // NOPMD
        assertThat(020).isEqualTo(16); // NOPMD  
    }

    @Test
    public void underscoredLiteral() {
        assertThat(1_1).isEqualTo(11);
        assertThat(100_200_300).isEqualTo(100200300);
        assertThat(100_200_300L).isEqualTo(100200300L);
        assertThat(1.23_456).isEqualTo(1.23456);
        assertThat(1.23_456F).isEqualTo(1.23456f);
        //assertThat(3.14_159).isEqualTo(3.14159); // this cause SpotBugs' CNT_ROUGH_CONSTANT_VALUE
        //assertThat(3.14_159F).isEqualTo(3.14159f); // this cause SpotBugs' CNT_ROUGH_CONSTANT_VALUE
    }
}
