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
package javasnack.dateandtime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.time.Clock;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.chrono.JapaneseDate;
import java.time.chrono.JapaneseEra;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/* see:
 * https://docs.oracle.com/javase/jp/8/docs/api/java/time/package-summary.html
 */
public class TestBasicDemo {

    @Test
    public void basicLocal() throws Exception {
        LocalDateTime ldt1 = LocalDateTime.of(2010, 1, 2, 23, 59, 59);
        assertEquals(2010, ldt1.getYear());
        assertEquals(Month.JANUARY, ldt1.getMonth());
        assertEquals(2, ldt1.getDayOfMonth());
        assertEquals(23, ldt1.getHour());
        assertEquals(59, ldt1.getMinute());
        assertEquals(59, ldt1.getSecond());
        LocalDateTime ldt1b = ldt1
                .withYear(2014)
                .withMonth(3)
                .withDayOfMonth(4)
                .withHour(1)
                .withMinute(2)
                .withSecond(3);
        assertEquals(2014, ldt1b.getYear());
        assertEquals(Month.MARCH, ldt1b.getMonth());
        assertEquals(4, ldt1b.getDayOfMonth());
        assertEquals(1, ldt1b.getHour());
        assertEquals(2, ldt1b.getMinute());
        assertEquals(3, ldt1b.getSecond());

        LocalDate ld1 = ldt1.toLocalDate();
        assertEquals(2010, ld1.getYear());
        assertEquals(Month.JANUARY, ld1.getMonth());
        assertEquals(2, ld1.getDayOfMonth());

        LocalTime lt1 = ldt1.toLocalTime();
        assertEquals(23, lt1.getHour());
        assertEquals(59, lt1.getMinute());
        assertEquals(59, lt1.getSecond());

        LocalDateTime ldt2 = LocalDateTime.parse("2011-12-31T01:02:03");
        assertEquals(2011, ldt2.getYear());
        assertEquals(Month.DECEMBER, ldt2.getMonth());
        assertEquals(31, ldt2.getDayOfMonth());
        assertEquals(1, ldt2.getHour());
        assertEquals(2, ldt2.getMinute());
        assertEquals(3, ldt2.getSecond());

        LocalDate ld2 = LocalDate.parse("2011-12-31");
        assertEquals(2011, ld2.getYear());
        assertEquals(12, ld2.getMonthValue());
        assertEquals(31, ld2.getDayOfMonth());

        LocalTime lt2 = LocalTime.parse("23:59:59");
        assertEquals(23, lt2.getHour());
        assertEquals(59, lt2.getMinute());
        assertEquals(59, lt2.getSecond());

        LocalDateTime ldt3 = ld2.atTime(1, 2, 3);
        assertEquals(1, ldt3.getHour());
        assertEquals(2, ldt3.getMinute());
        assertEquals(3, ldt3.getSecond());

        LocalDateTime ldt4 = lt2.atDate(LocalDate.of(2012, 1, 2));
        assertEquals(2012, ldt4.getYear());
        assertEquals(1, ldt4.getMonthValue());
        assertEquals(2, ldt4.getDayOfMonth());
    }

    @Test
    public void basicOffsetOrZoned() throws Exception {
        LocalDateTime ldt1 = LocalDateTime.of(2010, 1, 2, 23, 59, 59);
        ZoneOffset zo1 = ZoneOffset.of("+09:00");
        OffsetDateTime odt1 = OffsetDateTime.of(ldt1, zo1);
        assertEquals(2010, odt1.getYear());
        assertEquals(Month.JANUARY, odt1.getMonth());
        assertEquals(2, odt1.getDayOfMonth());
        assertEquals(23, odt1.getHour());
        assertEquals(59, odt1.getMinute());
        assertEquals(59, odt1.getSecond());
        assertEquals("+09:00", odt1.getOffset().toString());
        ZoneId zi1 = ZoneId.of("Asia/Tokyo");
        ZonedDateTime zdt1 = ZonedDateTime.of(ldt1, zi1);
        assertEquals(2010, zdt1.getYear());
        assertEquals(Month.JANUARY, zdt1.getMonth());
        assertEquals(2, zdt1.getDayOfMonth());
        assertEquals(23, zdt1.getHour());
        assertEquals(59, zdt1.getMinute());
        assertEquals(59, zdt1.getSecond());
        assertEquals("+09:00", zdt1.getOffset().toString());
        assertEquals("Asia/Tokyo", zdt1.getZone().toString());

        assertTrue(ZoneId.getAvailableZoneIds().contains("Asia/Tokyo"));
        assertFalse(ZoneId.getAvailableZoneIds().contains("Asia/Kyoto"));

        assertEquals(ZoneId.of("JST", ZoneId.SHORT_IDS), ZoneId.of("Asia/Tokyo"));
    }

    @Test
    public void zoneDemo() {
        LocalDateTime ldt0 = LocalDateTime.of(2010, 1, 1, 0, 0, 0);
        ZoneOffset zo0 = ZoneOffset.of("+01:30");
        ZonedDateTime zdt0 = ZonedDateTime.of(ldt0, zo0);

        ZonedDateTime zdt1 = zdt0.withZoneSameLocal(ZoneOffset.UTC);
        assertEquals(2010, zdt1.getYear());
        assertEquals(Month.JANUARY, zdt1.getMonth());
        assertEquals(1, zdt1.getDayOfMonth());
        assertEquals(0, zdt1.getHour());
        assertEquals(0, zdt1.getMinute());
        assertEquals(0, zdt1.getSecond());
        assertEquals("Z", zdt1.getOffset().toString());

        ZonedDateTime zdt2 = zdt0.withZoneSameInstant(ZoneOffset.UTC);
        assertEquals(2009, zdt2.getYear());
        assertEquals(Month.DECEMBER, zdt2.getMonth());
        assertEquals(31, zdt2.getDayOfMonth());
        assertEquals(22, zdt2.getHour());
        assertEquals(30, zdt2.getMinute());
        assertEquals(0, zdt2.getSecond());
        assertEquals("Z", zdt2.getOffset().toString());
    }

    @Test
    public void compatConversion() {
        LocalDateTime ldt0 = LocalDateTime.of(2010, 1, 1, 0, 0, 0);
        Date dt0 = Date.from(ldt0.toInstant(ZoneOffset.UTC));
        Calendar c0 = Calendar.getInstance(TimeZone.getTimeZone(ZoneOffset.UTC));
        c0.setTime(dt0);
        assertEquals(2010, c0.get(Calendar.YEAR));
        assertEquals(0, c0.get(Calendar.MONTH));
        assertEquals(1, c0.get(Calendar.DAY_OF_MONTH));
        assertEquals(0, c0.get(Calendar.HOUR));
        assertEquals(0, c0.get(Calendar.MINUTE));
        assertEquals(0, c0.get(Calendar.SECOND));

        Calendar c1 = Calendar.getInstance(TimeZone.getDefault());
        c1.set(2011, 2, 3, 23, 59, 59);
        ZonedDateTime zdt1 = ZonedDateTime.ofInstant(c1.toInstant(), ZoneOffset.systemDefault());
        assertEquals(2011, zdt1.getYear());
        assertEquals(Month.MARCH, zdt1.getMonth());
        assertEquals(3, zdt1.getDayOfMonth());
        assertEquals(23, zdt1.getHour());
        assertEquals(59, zdt1.getMinute());
        assertEquals(59, zdt1.getSecond());
        assertEquals(TimeZone.getDefault().getRawOffset(), TimeZone.getTimeZone(zdt1.getOffset()).getRawOffset());
    }

    @Test
    public void clockAndInstance() {
        Instant i0 = Instant.parse("1970-01-01T00:00:00.000Z");
        ZoneOffset zo0 = ZoneOffset.of("+09:00");
        Clock c0 = Clock.fixed(i0, zo0);
        LocalDateTime ldt0 = LocalDateTime.now(c0);
        assertEquals("1970-01-01T09:00", ldt0.toString());
        ZonedDateTime zdt0 = ZonedDateTime.now(c0);
        assertEquals("1970-01-01T09:00+09:00", zdt0.toString());

        LocalDateTime ldt0b = LocalDateTime.of(1970, 1, 2, 9, 1, 2);
        Clock c1 = Clock.fixed(ldt0b.toInstant(zo0), zo0);
        LocalDateTime ldt1 = LocalDateTime.now(c1);
        assertEquals("1970-01-02T09:01:02", ldt1.toString());
        ZonedDateTime zdt1 = ZonedDateTime.now(c1);
        assertEquals("1970-01-02T09:01:02+09:00", zdt1.toString());

        // see also:
        // https://stackoverflow.com/questions/24491260/mocking-time-in-java-8s-java-time-api
        // https://stackoverflow.com/questions/27067049/unit-testing-a-class-with-a-java-8-clock
    }

    @Test
    public void formatterDemo() {
        LocalDateTime ldt1 = LocalDateTime.parse("2017-01-02T03:04:05");
        OffsetDateTime odt1 = OffsetDateTime.parse("2016-12-31T23:59:59+09:00");
        ZonedDateTime zdt1 = ZonedDateTime.parse("2017-02-03T04:05:06+09:00[Asia/Tokyo]");
        assertEquals("2017-01-02T03:04:05", ldt1.format(DateTimeFormatter.ISO_DATE_TIME));
        assertEquals("2016-12-31T23:59:59+09:00", odt1.format(DateTimeFormatter.ISO_DATE_TIME));
        assertEquals("2017-02-03T04:05:06+09:00[Asia/Tokyo]", zdt1.format(DateTimeFormatter.ISO_DATE_TIME));

        DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("yyyy'年'MM'月'dd'日 'HH'時'mm'分'ss'秒 'XXX");
        try {
            ldt1.format(dtf1);
            fail();
        } catch (DateTimeException e) {
            assertEquals(UnsupportedTemporalTypeException.class, e.getClass());
        }
        assertEquals("2016年12月31日 23時59分59秒 +09:00", odt1.format(dtf1));
        assertEquals("2017年02月03日 04時05分06秒 +09:00", zdt1.format(dtf1));

        DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyyMMdd' 'HHmmss[XXX[' ['VV']']]");
        assertEquals("20170102 030405", ldt1.format(dtf2));
        assertEquals("20161231 235959+09:00", odt1.format(dtf2));
        assertEquals("20170203 040506+09:00 [Asia/Tokyo]", zdt1.format(dtf2));

        assertEquals(LocalDateTime.parse("20170102 030405", dtf2), ldt1);
        assertEquals(OffsetDateTime.parse("20161231 235959+09:00", dtf2), odt1);
        assertEquals(ZonedDateTime.parse("20170203 040506+09:00 [Asia/Tokyo]", dtf2), zdt1);
    }

    static Stream<Arguments> provideFormatShouldSuccess_ForLocalDate() {
        return Stream.of(
                arguments("0000-01-01", DateTimeFormatter.ISO_LOCAL_DATE),
                arguments("1900-01-01", DateTimeFormatter.ISO_LOCAL_DATE),
                arguments("1970-01-01", DateTimeFormatter.ISO_LOCAL_DATE),
                arguments("1970-12-31", DateTimeFormatter.ISO_LOCAL_DATE),
                // leap-year
                arguments("2004-02-29", DateTimeFormatter.ISO_LOCAL_DATE));
    }

    @ParameterizedTest
    @MethodSource("provideFormatShouldSuccess_ForLocalDate")
    public void formatShouldSuccess_ForLocalDate(final String s, final DateTimeFormatter dtf) {
        LocalDate.parse(s, dtf);
    }

    static Stream<Arguments> provideFormatShouldFailure_ForLocalDate() {
        return Stream.of(
                arguments("0000-00-00", DateTimeFormatter.ISO_LOCAL_DATE),
                arguments("1970-12-32", DateTimeFormatter.ISO_LOCAL_DATE),
                arguments("1970-11-31", DateTimeFormatter.ISO_LOCAL_DATE),
                // not leap-year
                arguments("2003-02-29", DateTimeFormatter.ISO_LOCAL_DATE),
                arguments("1970-1-1", DateTimeFormatter.ISO_LOCAL_DATE),
                arguments("1970/01/01", DateTimeFormatter.ISO_LOCAL_DATE),
                arguments("1970/1/1", DateTimeFormatter.ISO_LOCAL_DATE),
                arguments("aaa", DateTimeFormatter.ISO_LOCAL_DATE),
                arguments("", DateTimeFormatter.ISO_LOCAL_DATE));
        // NOTE: null -> NPE.
    }

    @ParameterizedTest
    @MethodSource("provideFormatShouldFailure_ForLocalDate")
    public void formatShouldFailure_ForLocalDate(final String s, final DateTimeFormatter dtf) {
        assertThrows(DateTimeParseException.class, () -> {
            LocalDate.parse(s, dtf);
        });
    }

    static Stream<Arguments> provideFormatShouldSuccess_ForLocalTime() {
        return Stream.of(
                arguments("00:00:00", DateTimeFormatter.ISO_LOCAL_TIME),
                arguments("12:59:59", DateTimeFormatter.ISO_LOCAL_TIME),
                arguments("13:00:00", DateTimeFormatter.ISO_LOCAL_TIME),
                arguments("23:59:59", DateTimeFormatter.ISO_LOCAL_TIME),
                arguments("00:00", DateTimeFormatter.ISO_LOCAL_TIME),
                arguments("23:59", DateTimeFormatter.ISO_LOCAL_TIME));
    }

    @ParameterizedTest
    @MethodSource("provideFormatShouldSuccess_ForLocalTime")
    public void formatShouldSuccess_ForLocalTime(final String s, final DateTimeFormatter dtf) {
        LocalTime.parse(s, dtf);
    }

    static Stream<Arguments> provideFormatShouldFailure_ForLocalTime() {
        return Stream.of(
                arguments("24:00:00", DateTimeFormatter.ISO_LOCAL_TIME),
                arguments("24:00", DateTimeFormatter.ISO_LOCAL_TIME),
                arguments("AM 00:00", DateTimeFormatter.ISO_LOCAL_TIME),
                arguments("1:00", DateTimeFormatter.ISO_LOCAL_TIME),
                arguments("1:0", DateTimeFormatter.ISO_LOCAL_TIME),
                arguments("aaa", DateTimeFormatter.ISO_LOCAL_TIME),
                arguments("", DateTimeFormatter.ISO_LOCAL_TIME));
    }

    @ParameterizedTest
    @MethodSource("provideFormatShouldFailure_ForLocalTime")
    public void formatShouldFailure_ForLocalTime(final String s, final DateTimeFormatter dtf) {
        assertThrows(DateTimeParseException.class, () -> {
            LocalTime.parse(s, dtf);
        });
    }

    @Test
    public void betweenDurationDemo() {
        LocalDateTime ldt0 = LocalDateTime.parse("2008-02-27T23:59:58");
        LocalDateTime ldt1 = LocalDateTime.parse("2008-02-28T00:00:00");
        // leep-year, 2008-02-29 exists.
        LocalDateTime ldt2 = LocalDateTime.parse("2008-03-01T00:00:00");
        Duration dr0 = Duration.between(ldt1, ldt0);
        assertEquals(-2L, dr0.getSeconds());
        Duration dr1 = Duration.between(ldt1, ldt2);
        assertEquals(3600 * 24 * 2L, dr1.getSeconds());

        OffsetDateTime odt1 = OffsetDateTime.parse("2008-03-01T00:00:00+09:00");
        OffsetDateTime odt2 = OffsetDateTime.parse("2008-03-01T00:00:00+01:00");
        assertEquals(3600 * 24 * 2L, Duration.between(ldt1, odt1).getSeconds());
        assertEquals(3600 * 24 * 2L, Duration.between(ldt1, odt2).getSeconds());
        assertEquals(3600 * 8L, Duration.between(odt1, odt2).getSeconds());
    }

    @Test
    public void japaneseEraDemo() {
        assertEquals(JapaneseEra.MEIJI, JapaneseDate.of(1874, 1, 1).getEra()); // MEIJI supports from 1874(MEIJI 6 year)
        assertEquals(JapaneseEra.MEIJI, JapaneseDate.of(1912, 7, 29).getEra());
        assertEquals(JapaneseEra.TAISHO, JapaneseDate.of(1912, 7, 30).getEra());
        assertEquals(JapaneseEra.TAISHO, JapaneseDate.of(1926, 12, 24).getEra());
        assertEquals(JapaneseEra.SHOWA, JapaneseDate.of(1926, 12, 25).getEra());
        assertEquals(JapaneseEra.SHOWA, JapaneseDate.of(1989, 1, 7).getEra());
        assertEquals(JapaneseEra.HEISEI, JapaneseDate.of(1989, 1, 8).getEra());

        JapaneseDate jdh1 = JapaneseDate.of(JapaneseEra.HEISEI, 1, 1, 8);
        assertEquals(1989, jdh1.get(ChronoField.YEAR));
        assertEquals(1, jdh1.get(ChronoField.YEAR_OF_ERA));

        try {
            JapaneseDate.of(JapaneseEra.HEISEI, 1, 1, 7);
            fail();
        } catch (DateTimeException e) {
            assertEquals(DateTimeException.class, e.getClass());
        }
        JapaneseDate jdh2 = JapaneseDate.of(JapaneseEra.SHOWA, 64, 1, 7);
        assertEquals(1989, jdh2.get(ChronoField.YEAR));
        assertEquals(64, jdh2.get(ChronoField.YEAR_OF_ERA));

        DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("Gy年M月d日");
        assertEquals("平成1年1月8日", jdh1.format(dtf1));
        assertEquals("昭和64年1月7日", jdh2.format(dtf1));
        DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("GGGGGy年M月d日");
        assertEquals("H1年1月8日", jdh1.format(dtf2));
        assertEquals("S64年1月7日", jdh2.format(dtf2));
    }
}
