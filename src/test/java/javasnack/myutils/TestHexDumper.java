// APL 2.0 / MIT dual license.
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
/*
 * MIT License
 * 
 * Copyright (c) 2017 "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package javasnack.myutils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TestHexDumper {
    static final byte[] BYTES_00_FF;

    static {
        BYTES_00_FF = new byte[256];
        for (int i = 0; i <= 0xFF; i++) {
            BYTES_00_FF[i] = (byte) i;
        }
    }

    String createDummy(String prefix, boolean toUpperCase, String separator) {
        char[] seeds = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        StringBuilder r = new StringBuilder();
        int k = 0;
        for (int i = 0; i < seeds.length; i++) {
            for (int j = 0; j < seeds.length; j++, k++) {
                StringBuilder sb = new StringBuilder();
                sb.append(seeds[i]);
                sb.append(seeds[j]);
                String t = sb.toString();
                if (toUpperCase) {
                    t = t.toUpperCase();
                }
                r.append(prefix);
                r.append(t);
                if (k < (BYTES_00_FF.length - 1)) {
                    r.append(separator);
                }
            }
        }
        return r.toString();
    }

    @Test
    public void testSimple() {
        HexDumper dumper = new HexDumper();
        String r = dumper.dump(null);
        assertEquals("", r);
        r = dumper.dump(new byte[] {});
        assertEquals("", r);
        r = dumper.dump(new byte[] { 0 });
        assertEquals("00", r);
        r = dumper.dump(new byte[] { 0, 1 });
        assertEquals("0001", r);
        r = dumper.dump(BYTES_00_FF);
        assertEquals(createDummy("", false, ""), r);
    }

    @Test
    public void testSimplePrefix() {
        HexDumper dumper = new HexDumper();
        dumper.setPrefix("0x");
        String r = dumper.dump(BYTES_00_FF);
        assertEquals(createDummy("0x", false, ""), r);
    }

    @Test
    public void testSimpleUpper() {
        HexDumper dumper = new HexDumper();
        dumper.setToUpperCase(true);
        String r = dumper.dump(BYTES_00_FF);
        assertEquals(createDummy("", true, ""), r);
    }

    @Test
    public void testSimpleSeparator() {
        HexDumper dumper = new HexDumper();
        dumper.setSeparator(",");
        String r = dumper.dump(BYTES_00_FF);
        assertEquals(createDummy("", false, ","), r);
    }

    @Test
    public void testPrefixUpperSeparator() {
        HexDumper dumper = new HexDumper();
        dumper.setPrefix("0x");
        dumper.setToUpperCase(true);
        dumper.setSeparator(", ");
        String r = dumper.dump(BYTES_00_FF);
        assertEquals(createDummy("0x", true, ", "), r);
    }
}
