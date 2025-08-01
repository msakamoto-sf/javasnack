/*
 * Copyright 2020 "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
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
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.IllegalFormatConversionException;
import java.util.Locale;
import java.util.MissingFormatArgumentException;

import org.junit.jupiter.api.Test;

/* see javadoc:
 * - java.lang.String#format
 * - java.util.Formatter
 */
public class TestStringFormat {

    @Test
    public void expandStrings() {
        assertThat(String.format("%s", "xxx", "yyy")).isEqualTo("xxx");

        final Throwable thrown0 = catchThrowable(() -> String.format("%s %s", "xxx"));
        assertThat(thrown0).isInstanceOf(MissingFormatArgumentException.class)
                .hasMessageContaining("Format specifier '%s'");

        assertThat(String.format("aaa %s bbb %s ccc", "xxx", "yyy")).isEqualTo("aaa xxx bbb yyy ccc");
        assertThat(String.format("%.5s", "123456789")).isEqualTo("12345");
        assertThat(String.format("%.5s", "123")).isEqualTo("123");
        assertThat(String.format("%10s", "123")).isEqualTo("       123");
        assertThat(String.format("%10.5s", "123")).isEqualTo("       123");
        assertThat(String.format("%10.5s", "123456789")).isEqualTo("     12345");
        assertThat(String.format("%-10s", "123")).isEqualTo("123       ");
        assertThat(String.format("%-10.5s", "123")).isEqualTo("123       ");
        assertThat(String.format("%-10.5s", "123456789")).isEqualTo("12345     ");

        assertThat(String.format("%%s")).isEqualTo("%s");
        assertThat(String.format("abc%ndef")).isEqualTo("abc" + System.lineSeparator() + "def");
    }

    @Test
    public void argumentsByArray() {
        assertThat(String.format("%2$s, %1$s, %3$s", new Object[] { "aaa", "bbb", "ccc", "ddd" }))
                .isEqualTo("bbb, aaa, ccc");
    }

    @Test
    public void argumentPosition() {
        assertThat(String.format("[%2$-10.5s],[%1$s],[%2$.5s],", "xxx", "123456789"))
                .isEqualTo("[12345     ],[xxx],[12345],");
    }

    @Test
    public void percentAndNewline() {
        assertThat(String.format("aaa %% %n bbb")).isEqualTo("aaa % " + System.getProperty("line.separator") + " bbb");
    }

    @Test
    public void expandBoolean() {
        assertThat(String.format("%b, %B", true, true)).isEqualTo("true, TRUE");
        assertThat(String.format("%b, %B", false, false)).isEqualTo("false, FALSE");
        assertThat(String.format("%b, %B", "true", "true")).isEqualTo("true, TRUE");
        // oops !! string variables are converted boolean true X(
        assertThat(String.format("%b, %B", "false", "false")).isEqualTo("true, TRUE");
    }

    @Test
    public void expandIntegers() {
        assertThat(String.format("%d, %d", 1, -2)).isEqualTo("1, -2");
        assertThat(String.format("%o, %o", 9, -10)).isEqualTo("11, 37777777766");
        assertThat(String.format("%x, %X", 10, 11)).isEqualTo("a, B");
        assertThat(String.format("%+d, %+d", 1, -2)).isEqualTo("+1, -2");
        assertThat(String.format("%+(d, %+(d", 1, -2)).isEqualTo("+1, (2)");
        assertThat(String.format("%3d, %3d", 1, -2)).isEqualTo("  1,  -2");
        assertThat(String.format("%03d, %03d", 1, -2)).isEqualTo("001, -02");
        assertThat(String.format("%-4d, %-4d", 1, -2)).isEqualTo("1   , -2  ");
        assertThat(String.format("%02x, %02X", 10, 11)).isEqualTo("0a, 0B");
        assertThat(String.format("%#05o, %#05o", 9, 10)).isEqualTo("00011, 00012");
        assertThat(String.format("%#05x, %#05X", 10, 11)).isEqualTo("0x00a, 0X00B");
        assertThat(String.format("%#04x, %#04X", 10, 11)).isEqualTo("0x0a, 0X0B");

        assertThat(String.format(Locale.US, "%,d %,d", 1_234, 1_234_567)).isEqualTo("1,234 1,234,567");
        assertThat(String.format(Locale.US, "%,20d %,20d", 1_234, 1_234_567))
                .isEqualTo("               1,234            1,234,567");
        assertThat(String.format(Locale.US, "%,-20d %,-20d", 1_234, 1_234_567))
                .isEqualTo("1,234                1,234,567           ");
    }

    @Test
    public void expandFloats() {
        assertThat(String.format("%f", Math.E)).isEqualTo("2.718282");
        assertThat(String.format("[%+10.3f]", Math.E)).isEqualTo("[    +2.718]");
        assertThat(String.format("%f", Math.E)).isEqualTo("2.718282");
    }

    @Test
    public void expandNumbersButStringGiven() {
        final Exception e = assertThrows(IllegalFormatConversionException.class, () -> String.format("%d", "123"));
        assertEquals("d != java.lang.String", e.getMessage());
    }

    @Test
    public void expandDateTime() {
        final LocalDateTime ldt1 = LocalDateTime.of(2010, 1, 2, 3, 4, 5);
        assertThat(String.format("%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS", ldt1)).isEqualTo("2010-01-02 03:04:05");
        assertThat(String.format("%1$tF %1$tT", ldt1)).isEqualTo("2010-01-02 03:04:05");
        final Exception e = assertThrows(IllegalFormatConversionException.class, () -> String.format("%1$tc", ldt1));
        assertEquals("Z != java.time.LocalDateTime", e.getMessage());

        final ZoneOffset zo1 = ZoneOffset.of("+09:00");
        final OffsetDateTime odt1 = OffsetDateTime.of(ldt1, zo1);
        assertThat(String.format(Locale.US, "%1$tc", odt1)).isEqualTo("Sat Jan 02 03:04:05 +09:00 2010");
        assertThat(String.format(Locale.UK, "%1$tc", odt1)).isEqualTo("Sat Jan 02 03:04:05 +09:00 2010");
        assertThat(String.format(Locale.JAPAN, "%1$tc", odt1)).isEqualTo("土 1月 02 03:04:05 +09:00 2010");

        assertThat(String.format(Locale.US, "%1$tr", ldt1)).isEqualTo("03:04:05 AM");
        // jdk11
        // assertThat(String.format(Locale.GERMANY, "%1$tr", ldt1)).isEqualTo("03:04:05 VORM.");
        // jdk21
        assertThat(String.format(Locale.GERMANY, "%1$tr", ldt1)).isEqualTo("03:04:05 AM");
        assertThat(String.format(Locale.JAPAN, "%1$tr", ldt1)).isEqualTo("03:04:05 午前");
    }
}
