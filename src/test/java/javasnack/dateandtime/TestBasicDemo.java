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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.testng.annotations.Test;

/**
 * @see https://docs.oracle.com/javase/jp/8/docs/api/java/time/package-summary.html
 */
public class TestBasicDemo {

    @Test
    public void basicLocal() throws Exception {
        LocalDateTime ldt1 = LocalDateTime.of(2010, 1, 2, 23, 59, 59);
        assertEquals(ldt1.getYear(), 2010);
        assertEquals(ldt1.getMonth(), Month.JANUARY);
        assertEquals(ldt1.getDayOfMonth(), 2);
        assertEquals(ldt1.getHour(), 23);
        assertEquals(ldt1.getMinute(), 59);
        assertEquals(ldt1.getSecond(), 59);
        LocalDateTime ldt1b = ldt1.withYear(2014).withMonth(3).withDayOfMonth(4).withHour(1).withMinute(2)
                .withSecond(3);
        assertEquals(ldt1b.getYear(), 2014);
        assertEquals(ldt1b.getMonth(), Month.MARCH);
        assertEquals(ldt1b.getDayOfMonth(), 4);
        assertEquals(ldt1b.getHour(), 1);
        assertEquals(ldt1b.getMinute(), 2);
        assertEquals(ldt1b.getSecond(), 3);

        LocalDate ld1 = ldt1.toLocalDate();
        assertEquals(ld1.getYear(), 2010);
        assertEquals(ld1.getMonth(), Month.JANUARY);
        assertEquals(ld1.getDayOfMonth(), 2);

        LocalTime lt1 = ldt1.toLocalTime();
        assertEquals(lt1.getHour(), 23);
        assertEquals(lt1.getMinute(), 59);
        assertEquals(lt1.getSecond(), 59);

        LocalDateTime ldt2 = LocalDateTime.parse("2011-12-31T01:02:03");
        assertEquals(ldt2.getYear(), 2011);
        assertEquals(ldt2.getMonth(), Month.DECEMBER);
        assertEquals(ldt2.getDayOfMonth(), 31);
        assertEquals(ldt2.getHour(), 1);
        assertEquals(ldt2.getMinute(), 2);
        assertEquals(ldt2.getSecond(), 3);

        LocalDate ld2 = LocalDate.parse("2011-12-31");
        assertEquals(ld2.getYear(), 2011);
        assertEquals(ld2.getMonthValue(), 12);
        assertEquals(ld2.getDayOfMonth(), 31);

        LocalTime lt2 = LocalTime.parse("23:59:59");
        assertEquals(lt2.getHour(), 23);
        assertEquals(lt2.getMinute(), 59);
        assertEquals(lt2.getSecond(), 59);

        LocalDateTime ldt3 = ld2.atTime(1, 2, 3);
        assertEquals(ldt3.getHour(), 1);
        assertEquals(ldt3.getMinute(), 2);
        assertEquals(ldt3.getSecond(), 3);

        LocalDateTime ldt4 = lt2.atDate(LocalDate.of(2012, 1, 2));
        assertEquals(ldt4.getYear(), 2012);
        assertEquals(ldt4.getMonthValue(), 1);
        assertEquals(ldt4.getDayOfMonth(), 2);
    }

    @Test
    public void basicOffsetOrZoned() throws Exception {
        LocalDateTime ldt1 = LocalDateTime.of(2010, 1, 2, 23, 59, 59);
        ZoneOffset zo1 = ZoneOffset.of("+09:00");
        OffsetDateTime odt1 = OffsetDateTime.of(ldt1, zo1);
        assertEquals(odt1.getYear(), 2010);
        assertEquals(odt1.getMonth(), Month.JANUARY);
        assertEquals(odt1.getDayOfMonth(), 2);
        assertEquals(odt1.getHour(), 23);
        assertEquals(odt1.getMinute(), 59);
        assertEquals(odt1.getSecond(), 59);
        assertEquals(odt1.getOffset().toString(), "+09:00");
        ZoneId zi1 = ZoneId.of("Asia/Tokyo");
        ZonedDateTime zdt1 = ZonedDateTime.of(ldt1, zi1);
        assertEquals(zdt1.getYear(), 2010);
        assertEquals(zdt1.getMonth(), Month.JANUARY);
        assertEquals(zdt1.getDayOfMonth(), 2);
        assertEquals(zdt1.getHour(), 23);
        assertEquals(zdt1.getMinute(), 59);
        assertEquals(zdt1.getSecond(), 59);
        assertEquals(zdt1.getOffset().toString(), "+09:00");
        assertEquals(zdt1.getZone().toString(), "Asia/Tokyo");

        assertTrue(ZoneId.getAvailableZoneIds().contains("Asia/Tokyo"));
        assertFalse(ZoneId.getAvailableZoneIds().contains("Asia/Kyoto"));

        assertEquals(ZoneId.of("Asia/Tokyo"), ZoneId.of("JST", ZoneId.SHORT_IDS));
    }

    @Test
    public void zoneDemo() {
        LocalDateTime ldt0 = LocalDateTime.of(2010, 1, 1, 0, 0, 0);
        ZoneOffset zo0 = ZoneOffset.of("+01:30");
        ZonedDateTime zdt0 = ZonedDateTime.of(ldt0, zo0);

        ZonedDateTime zdt1 = zdt0.withZoneSameLocal(ZoneOffset.UTC);
        assertEquals(zdt1.getYear(), 2010);
        assertEquals(zdt1.getMonth(), Month.JANUARY);
        assertEquals(zdt1.getDayOfMonth(), 1);
        assertEquals(zdt1.getHour(), 0);
        assertEquals(zdt1.getMinute(), 0);
        assertEquals(zdt1.getSecond(), 0);
        assertEquals(zdt1.getOffset().toString(), "Z");

        ZonedDateTime zdt2 = zdt0.withZoneSameInstant(ZoneOffset.UTC);
        assertEquals(zdt2.getYear(), 2009);
        assertEquals(zdt2.getMonth(), Month.DECEMBER);
        assertEquals(zdt2.getDayOfMonth(), 31);
        assertEquals(zdt2.getHour(), 22);
        assertEquals(zdt2.getMinute(), 30);
        assertEquals(zdt2.getSecond(), 0);
        assertEquals(zdt2.getOffset().toString(), "Z");
    }

    @Test
    public void compatConversion() {
        LocalDateTime ldt0 = LocalDateTime.of(2010, 1, 1, 0, 0, 0);
        Date dt0 = Date.from(ldt0.toInstant(ZoneOffset.UTC));
        Calendar c0 = Calendar.getInstance(TimeZone.getTimeZone(ZoneOffset.UTC));
        c0.setTime(dt0);
        assertEquals(c0.get(Calendar.YEAR), 2010);
        assertEquals(c0.get(Calendar.MONTH), 0);
        assertEquals(c0.get(Calendar.DAY_OF_MONTH), 1);
        assertEquals(c0.get(Calendar.HOUR), 0);
        assertEquals(c0.get(Calendar.MINUTE), 0);
        assertEquals(c0.get(Calendar.SECOND), 0);

        Calendar c1 = Calendar.getInstance(TimeZone.getDefault());
        c1.set(2011, 2, 3, 23, 59, 59);
        ZonedDateTime zdt1 = ZonedDateTime.ofInstant(c1.toInstant(), ZoneOffset.systemDefault());
        assertEquals(zdt1.getYear(), 2011);
        assertEquals(zdt1.getMonth(), Month.MARCH);
        assertEquals(zdt1.getDayOfMonth(), 3);
        assertEquals(zdt1.getHour(), 23);
        assertEquals(zdt1.getMinute(), 59);
        assertEquals(zdt1.getSecond(), 59);
        assertEquals(TimeZone.getTimeZone(zdt1.getOffset()).getRawOffset(), TimeZone.getDefault().getRawOffset());
    }

    @Test
    public void clockAndInstance() {
        Instant i0 = Instant.parse("1970-01-01T00:00:00.000Z");
        ZoneOffset zo0 = ZoneOffset.of("+09:00");
        Clock c0 = Clock.fixed(i0, zo0);
        LocalDateTime ldt0 = LocalDateTime.now(c0);
        assertEquals(ldt0.toString(), "1970-01-01T09:00");
        ZonedDateTime zdt0 = ZonedDateTime.now(c0);
        assertEquals(zdt0.toString(), "1970-01-01T09:00+09:00");

        LocalDateTime ldt0b = LocalDateTime.of(1970, 1, 2, 9, 1, 2);
        Clock c1 = Clock.fixed(ldt0b.toInstant(zo0), zo0);
        LocalDateTime ldt1 = LocalDateTime.now(c1);
        assertEquals(ldt1.toString(), "1970-01-02T09:01:02");
        ZonedDateTime zdt1 = ZonedDateTime.now(c1);
        assertEquals(zdt1.toString(), "1970-01-02T09:01:02+09:00");
    }
}
