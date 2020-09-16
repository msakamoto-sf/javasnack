package javasnack.ojcp.se8gold;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

public class Test07DateTimeApis {
    @Test
    public void testLocalDateTimeCreationDemo() {
        // LocalDate.of() demo
        LocalDate ld1 = LocalDate.of(2010, 02, 24);
        assertThat(ld1.toString()).isEqualTo("2010-02-24");
        ld1 = LocalDate.of(2010, 1, 1); // 月は1始まり
        assertThat(ld1.toString()).isEqualTo("2010-01-01");
        ld1 = LocalDate.of(2010, 12, 31);
        assertThat(ld1.toString()).isEqualTo("2010-12-31");
        ld1 = LocalDate.of(2010, 10, 24);
        assertThat(ld1.toString()).isEqualTo("2010-10-24");
        // 月が0始まりなのでoctet(8進数)扱いになる
        ld1 = LocalDate.of(2010, 010, 24);
        assertThat(ld1.toString()).isEqualTo("2010-08-24");

        assertThat(Month.JANUARY.getValue()).isEqualTo(1);
        assertThat(Month.DECEMBER.getValue()).isEqualTo(12);
        ld1 = LocalDate.of(2010, Month.FEBRUARY, 3);
        assertThat(ld1.toString()).isEqualTo("2010-02-03");
        assertThat(ld1.getYear()).isEqualTo(2010);
        assertThat(ld1.getMonth()).isEqualTo(Month.FEBRUARY);
        assertThat(ld1.getMonthValue()).isEqualTo(2);
        assertThat(ld1.getDayOfYear()).isEqualTo(34);
        assertThat(ld1.getDayOfMonth()).isEqualTo(3);
        assertThat(ld1.getDayOfWeek()).isEqualTo(DayOfWeek.WEDNESDAY);

        assertThatThrownBy(() -> {
            LocalDate.of(2010, 0, 1);
        }).isInstanceOf(DateTimeException.class);

        assertThatThrownBy(() -> {
            LocalDate.of(2010, 1, 0);
        }).isInstanceOf(DateTimeException.class);

        assertThatThrownBy(() -> {
            LocalDate.of(2010, 1, 32);
        }).isInstanceOf(DateTimeException.class);

        assertThatThrownBy(() -> {
            LocalDate.of(2010, 13, 1);
        }).isInstanceOf(DateTimeException.class);

        // LocalDate.parse() demo
        ld1 = LocalDate.parse("2010-02-24");
        assertThat(ld1.toString()).isEqualTo("2010-02-24");
        assertThatThrownBy(() -> {
            LocalDate.parse("2010-2-24");
        }).isInstanceOf(DateTimeParseException.class);

        // LocalTime.of() demo
        LocalTime lt1 = LocalTime.of(1, 2);
        assertThat(lt1.toString()).isEqualTo("01:02");
        lt1 = LocalTime.of(1, 2, 3);
        assertThat(lt1.toString()).isEqualTo("01:02:03");
        lt1 = LocalTime.of(0, 0, 0);
        assertThat(lt1.toString()).isEqualTo("00:00");
        lt1 = LocalTime.of(23, 59, 59);
        assertThat(lt1.toString()).isEqualTo("23:59:59");
        lt1 = LocalTime.of(1, 2, 3, 4);
        assertThat(lt1.toString()).isEqualTo("01:02:03.000000004");

        assertThatThrownBy(() -> {
            LocalTime.of(24, 1);
        }).isInstanceOf(DateTimeException.class);
        assertThatThrownBy(() -> {
            LocalTime.of(23, 60);
        }).isInstanceOf(DateTimeException.class);
        assertThatThrownBy(() -> {
            LocalTime.of(23, 59, 60);
        }).isInstanceOf(DateTimeException.class);

        // LocalTime.parse() demo
        lt1 = LocalTime.parse("00:00");
        assertThat(lt1.toString()).isEqualTo("00:00");
        lt1 = LocalTime.parse("00:00:00");
        assertThat(lt1.toString()).isEqualTo("00:00");
        lt1 = LocalTime.parse("00:00:00.0");
        assertThat(lt1.toString()).isEqualTo("00:00");
        lt1 = LocalTime.parse("00:00:01");
        assertThat(lt1.toString()).isEqualTo("00:00:01");
        lt1 = LocalTime.parse("00:00:00.1");
        assertThat(lt1.toString()).isEqualTo("00:00:00.100");
        lt1 = LocalTime.parse("00:00:00.01");
        assertThat(lt1.toString()).isEqualTo("00:00:00.010");
        lt1 = LocalTime.parse("00:00:00.001");
        assertThat(lt1.toString()).isEqualTo("00:00:00.001");
        lt1 = LocalTime.parse("00:00:00.0001");
        assertThat(lt1.toString()).isEqualTo("00:00:00.000100");
        lt1 = LocalTime.parse("23:59:59");
        assertThat(lt1.toString()).isEqualTo("23:59:59");
        assertThatThrownBy(() -> {
            LocalTime.parse("24:59:59");
        }).isInstanceOf(DateTimeParseException.class);
        assertThatThrownBy(() -> {
            LocalTime.parse("23:60:59");
        }).isInstanceOf(DateTimeParseException.class);
        assertThatThrownBy(() -> {
            LocalTime.parse("23:59:60");
        }).isInstanceOf(DateTimeParseException.class);
        assertThatThrownBy(() -> {
            LocalTime.parse("0:00");
        }).isInstanceOf(DateTimeParseException.class);
        assertThatThrownBy(() -> {
            LocalTime.parse("00:0");
        }).isInstanceOf(DateTimeParseException.class);
        assertThatThrownBy(() -> {
            LocalTime.parse("00:00:0");
        }).isInstanceOf(DateTimeParseException.class);

        // LocalDateTime.of() demo
        LocalDateTime ldt1 = LocalDateTime.of(2010, 1, 2, 3, 4);
        assertThat(ldt1.toString()).isEqualTo("2010-01-02T03:04");
        ldt1 = LocalDateTime.of(2010, 1, 2, 3, 4, 5);
        assertThat(ldt1.toString()).isEqualTo("2010-01-02T03:04:05");
        ldt1 = LocalDateTime.of(2010, 1, 2, 3, 4, 5, 6);
        assertThat(ldt1.toString()).isEqualTo("2010-01-02T03:04:05.000000006");
        ldt1 = LocalDateTime.of(2010, Month.JANUARY, 2, 3, 4);
        assertThat(ldt1.toString()).isEqualTo("2010-01-02T03:04");
        ldt1 = LocalDateTime.of(2010, Month.JANUARY, 2, 3, 4, 5);
        assertThat(ldt1.toString()).isEqualTo("2010-01-02T03:04:05");
        ldt1 = LocalDateTime.of(2010, Month.JANUARY, 2, 3, 4, 5, 6);
        assertThat(ldt1.toString()).isEqualTo("2010-01-02T03:04:05.000000006");

        ld1 = LocalDate.of(2010, 1, 2);
        lt1 = LocalTime.of(3, 4, 5);
        ldt1 = LocalDateTime.of(ld1, lt1);
        assertThat(ldt1.toString()).isEqualTo("2010-01-02T03:04:05");

        assertThatThrownBy(() -> {
            LocalDateTime.of(2010, 0, 2, 3, 4, 5);
        }).isInstanceOf(DateTimeException.class);
        assertThatThrownBy(() -> {
            LocalDateTime.of(2010, 13, 2, 3, 4, 5);
        }).isInstanceOf(DateTimeException.class);
        assertThatThrownBy(() -> {
            LocalDateTime.of(2010, 1, 32, 3, 4, 5);
        }).isInstanceOf(DateTimeException.class);
        assertThatThrownBy(() -> {
            LocalDateTime.of(2010, 1, 2, 24, 4, 5);
        }).isInstanceOf(DateTimeException.class);
        assertThatThrownBy(() -> {
            LocalDateTime.of(2010, 1, 2, 3, 60, 5);
        }).isInstanceOf(DateTimeException.class);
        assertThatThrownBy(() -> {
            LocalDateTime.of(2010, 1, 2, 3, 4, 60);
        }).isInstanceOf(DateTimeException.class);

        // LocalDateTime.parse() demo
        ldt1 = LocalDateTime.parse("2010-01-02T03:04");
        assertThat(ldt1.toString()).isEqualTo("2010-01-02T03:04");
        ldt1 = LocalDateTime.parse("2010-01-02T03:04:05");
        assertThat(ldt1.toString()).isEqualTo("2010-01-02T03:04:05");
        ldt1 = LocalDateTime.parse("2010-01-02T03:04:05.123");
        assertThat(ldt1.toString()).isEqualTo("2010-01-02T03:04:05.123");
        assertThatThrownBy(() -> {
            LocalDateTime.parse("2010-0-02T03:04:05");
        }).isInstanceOf(DateTimeParseException.class);
        assertThatThrownBy(() -> {
            LocalDateTime.parse("2010-13-02T03:04:05");
        }).isInstanceOf(DateTimeParseException.class);
        assertThatThrownBy(() -> {
            LocalDateTime.parse("2010-01-0T03:04:05");
        }).isInstanceOf(DateTimeParseException.class);
        assertThatThrownBy(() -> {
            LocalDateTime.parse("2010-01-1T03:04:05");
        }).isInstanceOf(DateTimeParseException.class);
        assertThatThrownBy(() -> {
            LocalDateTime.parse("2010-01-32T03:04:05");
        }).isInstanceOf(DateTimeParseException.class);
        assertThatThrownBy(() -> {
            LocalDateTime.parse("2010-01-02T1:04:05");
        }).isInstanceOf(DateTimeParseException.class);
        assertThatThrownBy(() -> {
            LocalDateTime.parse("2010-01-02 03:04:05");
        }).isInstanceOf(DateTimeParseException.class);
        assertThatThrownBy(() -> {
            LocalDateTime.parse("2010-01-02T03:4:05");
        }).isInstanceOf(DateTimeParseException.class);
        assertThatThrownBy(() -> {
            LocalDateTime.parse("2010-01-02T03:04:5");
        }).isInstanceOf(DateTimeParseException.class);

        // LocalDateTime.equals() demo
        LocalDateTime ldt2a = LocalDateTime.of(2010, 1, 2, 3, 4, 5);
        LocalDateTime ldt2b = LocalDateTime.of(2010, 1, 2, 3, 4, 5);
        assertThat(ldt2a == ldt2b).isFalse();
        assertThat(ldt2a.equals(ldt2b)).isTrue();
        LocalDate ld2a = LocalDate.of(2010, 1, 2);
        LocalDate ld2b = LocalDate.of(2010, 1, 2);
        assertThat(ld2a == ld2b).isFalse();
        assertThat(ld2a.equals(ld2b)).isTrue();
        LocalTime lt2a = LocalTime.of(3, 4, 5);
        LocalTime lt2b = LocalTime.of(3, 4, 5);
        assertThat(lt2a == lt2b).isFalse();
        assertThat(lt2a.equals(lt2b)).isTrue();
    }

    @Test
    public void testDateTimeFormatDemo() {
        LocalDateTime ldt = LocalDateTime.of(2010, 1, 2, 3, 4, 5);
        // ISO_DATE_TIME, ISO_DATE, ISO_TIME では使用可能ならオフセットを含む。
        DateTimeFormatter fmt = DateTimeFormatter.ISO_DATE_TIME;
        assertThat(ldt.format(fmt)).isEqualTo("2010-01-02T03:04:05");
        assertThat(fmt.format(ldt)).isEqualTo("2010-01-02T03:04:05");
        fmt = DateTimeFormatter.ISO_DATE;
        assertThat(ldt.format(fmt)).isEqualTo("2010-01-02");
        assertThat(fmt.format(ldt)).isEqualTo("2010-01-02");
        fmt = DateTimeFormatter.ISO_TIME;
        assertThat(ldt.format(fmt)).isEqualTo("03:04:05");
        assertThat(fmt.format(ldt)).isEqualTo("03:04:05");
        // ISO_LOCAL_* はオフセットを含まない。
        fmt = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        assertThat(ldt.format(fmt)).isEqualTo("2010-01-02T03:04:05");
        assertThat(fmt.format(ldt)).isEqualTo("2010-01-02T03:04:05");
        fmt = DateTimeFormatter.ISO_LOCAL_DATE;
        assertThat(ldt.format(fmt)).isEqualTo("2010-01-02");
        assertThat(fmt.format(ldt)).isEqualTo("2010-01-02");
        fmt = DateTimeFormatter.ISO_LOCAL_TIME;
        assertThat(ldt.format(fmt)).isEqualTo("03:04:05");
        assertThat(fmt.format(ldt)).isEqualTo("03:04:05");

        // Localeにより影響を受ける書式設定
        fmt = DateTimeFormatter.ofPattern("MMMM", Locale.JAPAN);
        assertThat(ldt.format(fmt)).isEqualTo("1月");
        assertThat(fmt.format(ldt)).isEqualTo("1月");
        fmt = DateTimeFormatter.ofPattern("MMMM", Locale.US);
        assertThat(ldt.format(fmt)).isEqualTo("January");
        assertThat(fmt.format(ldt)).isEqualTo("January");

        fmt = DateTimeFormatter.ofPattern("yyyy/MM/dd HH-mm-ss");
        assertThat(ldt.format(fmt)).isEqualTo("2010/01/02 03-04-05");
        assertThat(fmt.format(ldt)).isEqualTo("2010/01/02 03-04-05");
        ldt = LocalDateTime.parse("2011/02/03 04-05-06", fmt);
        assertThat(ldt.toString()).isEqualTo("2011-02-03T04:05:06");
    }

    @Test
    public void testDateTimePlusMinusDemo() {
        LocalDate ld1 = LocalDate.of(2010, 12, 30);
        assertThat(ld1.toString()).isEqualTo("2010-12-30");
        LocalDate ld2 = ld1.plusDays(3);
        assertThat(ld1.toString()).isEqualTo("2010-12-30"); // 元の LocalDate は変化しない
        assertThat(ld2.toString()).isEqualTo("2011-01-02"); // 繰り上がり
        assertThat(ld1.plusMonths(3).toString()).isEqualTo("2011-03-30"); // 繰り上がり
        assertThat(ld1.plusYears(3).toString()).isEqualTo("2013-12-30"); // 繰り上がり
        ld1 = LocalDate.of(2010, 1, 2);
        assertThat(ld1.minusDays(3).toString()).isEqualTo("2009-12-30"); // 繰り下がり
        assertThat(ld1.minusMonths(3).toString()).isEqualTo("2009-10-02"); // 繰り下がり
        assertThat(ld1.minusYears(3).toString()).isEqualTo("2007-01-02");

        // LocalDate には時間の加減算メソッドは無い -> compile error
        //ld1.plusHours(3);
        //ld1.plusMinutes(3);
        //ld1.plusSeconds(3);
        //ld1.plusNanos(3);
        //ld1.minusHours(3);
        //ld1.minusMinutes(3);
        //ld1.minusSeconds(3);
        //ld1.minusNanos(3);

        LocalTime lt1 = LocalTime.of(22, 58, 58, 999999998);
        assertThat(lt1.toString()).isEqualTo("22:58:58.999999998");
        LocalTime lt2 = lt1.plusHours(3);
        assertThat(lt1.toString()).isEqualTo("22:58:58.999999998"); // 元の LocalTime は変化しない
        assertThat(lt2.toString()).isEqualTo("01:58:58.999999998"); // 繰り上がり
        lt2 = lt1.plusMinutes(3);
        assertThat(lt2.toString()).isEqualTo("23:01:58.999999998"); // 繰り上がり
        lt2 = lt1.plusSeconds(3);
        assertThat(lt2.toString()).isEqualTo("22:59:01.999999998"); // 繰り上がり
        lt2 = lt1.plusNanos(3);
        assertThat(lt2.toString()).isEqualTo("22:58:59.000000001"); // 繰り上がり
        lt1 = LocalTime.of(1, 2, 3, 4);
        lt2 = lt1.minusHours(2);
        assertThat(lt2.toString()).isEqualTo("23:02:03.000000004"); // 繰り下がり
        lt2 = lt1.minusMinutes(3);
        assertThat(lt2.toString()).isEqualTo("00:59:03.000000004"); // 繰り下がり
        lt2 = lt1.minusSeconds(4);
        assertThat(lt2.toString()).isEqualTo("01:01:59.000000004"); // 繰り下がり
        lt2 = lt1.minusNanos(5);
        assertThat(lt2.toString()).isEqualTo("01:02:02.999999999"); // 繰り下がり

        // LocalTime には年月日の加減算メソッドは無い -> compile error
        //lt1.plusDays(3);
        //lt1.plusMonths(3);
        //lt1.plusYears(3);
        //lt1.minusDays(3);
        //lt1.minusMonths(3);
        //lt1.minusYears(3);

        LocalDateTime ldt1 = LocalDateTime.of(2010, 12, 30, 22, 58, 58, 999999998);
        assertThat(ldt1.toString()).isEqualTo("2010-12-30T22:58:58.999999998");
        LocalDateTime ldt2 = ldt1.plusDays(3);
        // 元の LocalDate は変化しない
        assertThat(ldt1.toString()).isEqualTo("2010-12-30T22:58:58.999999998");
        assertThat(ldt2.toString()).isEqualTo("2011-01-02T22:58:58.999999998"); // 繰り上がり
        ldt2 = ldt1.plusMonths(3);
        assertThat(ldt2.toString()).isEqualTo("2011-03-30T22:58:58.999999998"); // 繰り上がり
        ldt2 = ldt1.plusYears(3);
        assertThat(ldt2.toString()).isEqualTo("2013-12-30T22:58:58.999999998"); // 繰り上がり
        ldt2 = ldt1.plusHours(3);
        assertThat(ldt2.toString()).isEqualTo("2010-12-31T01:58:58.999999998"); // 繰り上がり
        ldt2 = ldt1.plusMinutes(3);
        assertThat(ldt2.toString()).isEqualTo("2010-12-30T23:01:58.999999998"); // 繰り上がり
        ldt2 = ldt1.plusSeconds(3);
        assertThat(ldt2.toString()).isEqualTo("2010-12-30T22:59:01.999999998"); // 繰り上がり
        ldt2 = ldt1.plusNanos(3);
        assertThat(ldt2.toString()).isEqualTo("2010-12-30T22:58:59.000000001"); // 繰り上がり
        ldt1 = LocalDateTime.of(2010, 1, 2, 1, 2, 3, 4);
        ldt2 = ldt1.minusDays(3);
        assertThat(ldt2.toString()).isEqualTo("2009-12-30T01:02:03.000000004"); // 繰り下がり
        ldt2 = ldt1.minusMonths(3);
        assertThat(ldt2.toString()).isEqualTo("2009-10-02T01:02:03.000000004"); // 繰り下がり
        ldt2 = ldt1.minusYears(3);
        assertThat(ldt2.toString()).isEqualTo("2007-01-02T01:02:03.000000004");
        ldt2 = ldt1.minusHours(2);
        assertThat(ldt2.toString()).isEqualTo("2010-01-01T23:02:03.000000004"); // 繰り下がり
        ldt2 = ldt1.minusMinutes(3);
        assertThat(ldt2.toString()).isEqualTo("2010-01-02T00:59:03.000000004"); // 繰り下がり
        ldt2 = ldt1.minusSeconds(4);
        assertThat(ldt2.toString()).isEqualTo("2010-01-02T01:01:59.000000004"); // 繰り下がり
        ldt2 = ldt1.minusNanos(5);
        assertThat(ldt2.toString()).isEqualTo("2010-01-02T01:02:02.999999999"); // 繰り下がり
    }

    @Test
    public void testZonedDateTimeDemo() {
        ZoneId zi1 = ZoneId.systemDefault();
        System.out.println("ZoneId.systemDefault(): " + zi1.getId());

        Set<String> availableZoneIds = ZoneId.getAvailableZoneIds();
        System.out.println("ZoneId.getAvailableZoneIds(): "
                + availableZoneIds.stream().sorted().collect(Collectors.joining(",\n")));

        zi1 = ZoneId.of("Asia/Tokyo");
        // LocalDateTime に ZoneId 情報をつける。(日時そのものは変えない)
        LocalDateTime ldt1 = LocalDateTime.of(1900, 1, 2, 3, 4, 5);
        ZonedDateTime zdt1 = ZonedDateTime.of(ldt1, zi1);
        assertThat(zdt1.toString()).isEqualTo("1900-01-02T03:04:05+09:00[Asia/Tokyo]");
        zdt1 = ZonedDateTime.of(LocalDate.of(1900, 1, 2), LocalTime.of(3, 4, 5), zi1);
        assertThat(zdt1.toString()).isEqualTo("1900-01-02T03:04:05+09:00[Asia/Tokyo]");
        zdt1 = ZonedDateTime.of(1900, 1, 2, 3, 4, 5, 0, zi1);
        assertThat(zdt1.toString()).isEqualTo("1900-01-02T03:04:05+09:00[Asia/Tokyo]");

        zi1 = ZoneId.of("Japan");
        zdt1 = ZonedDateTime.of(ldt1, zi1);
        assertThat(zdt1.toString()).isEqualTo("1900-01-02T03:04:05+09:00[Japan]");
        zdt1 = ZonedDateTime.of(LocalDate.of(1900, 1, 2), LocalTime.of(3, 4, 5), zi1);
        assertThat(zdt1.toString()).isEqualTo("1900-01-02T03:04:05+09:00[Japan]");
        zdt1 = ZonedDateTime.of(1900, 1, 2, 3, 4, 5, 0, zi1);
        assertThat(zdt1.toString()).isEqualTo("1900-01-02T03:04:05+09:00[Japan]");

        zi1 = ZoneId.of("America/Los_Angeles");
        zdt1 = ZonedDateTime.of(ldt1, zi1);
        assertThat(zdt1.toString()).isEqualTo("1900-01-02T03:04:05-08:00[America/Los_Angeles]");
        zdt1 = ZonedDateTime.of(LocalDate.of(1900, 1, 2), LocalTime.of(3, 4, 5), zi1);
        assertThat(zdt1.toString()).isEqualTo("1900-01-02T03:04:05-08:00[America/Los_Angeles]");
        zdt1 = ZonedDateTime.of(1900, 1, 2, 3, 4, 5, 0, zi1);
        assertThat(zdt1.toString()).isEqualTo("1900-01-02T03:04:05-08:00[America/Los_Angeles]");

        zi1 = ZoneId.of("GMT");
        zdt1 = ZonedDateTime.of(ldt1, zi1);
        assertThat(zdt1.toString()).isEqualTo("1900-01-02T03:04:05Z[GMT]");
        zdt1 = ZonedDateTime.of(LocalDate.of(1900, 1, 2), LocalTime.of(3, 4, 5), zi1);
        assertThat(zdt1.toString()).isEqualTo("1900-01-02T03:04:05Z[GMT]");
        zdt1 = ZonedDateTime.of(1900, 1, 2, 3, 4, 5, 0, zi1);
        assertThat(zdt1.toString()).isEqualTo("1900-01-02T03:04:05Z[GMT]");

        zi1 = ZoneId.of("UTC");
        zdt1 = ZonedDateTime.of(ldt1, zi1);
        assertThat(zdt1.toString()).isEqualTo("1900-01-02T03:04:05Z[UTC]");
        zdt1 = ZonedDateTime.of(LocalDate.of(1900, 1, 2), LocalTime.of(3, 4, 5), zi1);
        assertThat(zdt1.toString()).isEqualTo("1900-01-02T03:04:05Z[UTC]");
        zdt1 = ZonedDateTime.of(1900, 1, 2, 3, 4, 5, 0, zi1);
        assertThat(zdt1.toString()).isEqualTo("1900-01-02T03:04:05Z[UTC]");

        zi1 = ZoneId.of("Asia/Tokyo");
        zdt1 = ZonedDateTime.of(ldt1, zi1);
        DateTimeFormatter fmt1 = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL);
        assertThat(zdt1.format(fmt1)).isEqualTo(fmt1.format(zdt1));
        assertThat(zdt1.format(fmt1)).isEqualTo("1900年1月2日火曜日 3時04分05秒 日本標準時");
        fmt1 = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG);
        assertThat(zdt1.format(fmt1)).isEqualTo("1900年1月2日 3:04:05 JST");
        fmt1 = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
        assertThat(zdt1.format(fmt1)).isEqualTo("1900/01/02 3:04:05");
        fmt1 = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
        assertThat(zdt1.format(fmt1)).isEqualTo("1900/01/02 3:04");

        // FULL, LONG は zone 情報を必要とするため、
        // zone情報の無いLocalDateTimeでフォーマットしようとすると実行時例外
        assertThatThrownBy(() -> {
            ldt1.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL));
        }).isInstanceOf(DateTimeException.class);
        assertThatThrownBy(() -> {
            ldt1.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG));
        }).isInstanceOf(DateTimeException.class);
        fmt1 = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
        assertThat(ldt1.format(fmt1)).isEqualTo("1900/01/02 3:04:05");
        fmt1 = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
        assertThat(ldt1.format(fmt1)).isEqualTo("1900/01/02 3:04");

        // ZonedDateTime.equals() demo
        LocalDateTime ldt0a = LocalDateTime.of(1970, 1, 1, 9, 0, 0);
        LocalDateTime ldt0b = LocalDateTime.of(1970, 1, 1, 0, 0, 0);
        ZonedDateTime zdt2a = ZonedDateTime.of(ldt0a, ZoneId.of("Asia/Tokyo"));
        ZonedDateTime zdt2b = ZonedDateTime.of(ldt0a, ZoneId.of("Asia/Tokyo"));
        ZonedDateTime zdt2c = ZonedDateTime.of(ldt0b, ZoneId.of("UTC"));
        assertThat(zdt2a == zdt2b).isFalse();
        assertThat(zdt2a.equals(zdt2b)).isTrue();
        // 時差を計算すれば同じ時点(Instant)にはなるが、ZonedDateTime自体のequals()はfalse.
        assertThat(zdt2a.equals(zdt2c)).isFalse();
    }

    @Test
    public void testZoneOffsetDemo() {
        ZoneOffset zo1 = ZoneOffset.of("+09:30");
        assertThat(zo1.getId()).isEqualTo("+09:30");
        assertThat(zo1.isSupported(ChronoField.OFFSET_SECONDS)).isTrue();
        assertThat(zo1.get(ChronoField.OFFSET_SECONDS)).isEqualTo(34200);
        assertThat(zo1.getTotalSeconds()).isEqualTo(34200);

        assertThatThrownBy(() -> {
            ZoneOffset.of("+00:00").get(ChronoField.HOUR_OF_DAY);
        }).isInstanceOf(UnsupportedTemporalTypeException.class);

        assertThatThrownBy(() -> {
            ZoneOffset.of("+00:00").get(ChronoField.MINUTE_OF_HOUR);
        }).isInstanceOf(UnsupportedTemporalTypeException.class);

        // ZoneOffset.of(String) demo
        zo1 = ZoneOffset.of("Z"); // UTC
        assertThat(zo1.getId()).isEqualTo("Z");
        assertThat(zo1.getTotalSeconds()).isEqualTo(0);
        zo1 = ZoneOffset.of("+1");
        assertThat(zo1.getId()).isEqualTo("+01:00");
        assertThat(zo1.getTotalSeconds()).isEqualTo(3600);
        zo1 = ZoneOffset.of("-1");
        assertThat(zo1.getId()).isEqualTo("-01:00");
        assertThat(zo1.getTotalSeconds()).isEqualTo(-3600);
        zo1 = ZoneOffset.of("+02");
        assertThat(zo1.getId()).isEqualTo("+02:00");
        assertThat(zo1.getTotalSeconds()).isEqualTo(7200);
        zo1 = ZoneOffset.of("-02");
        assertThat(zo1.getId()).isEqualTo("-02:00");
        assertThat(zo1.getTotalSeconds()).isEqualTo(-7200);
        zo1 = ZoneOffset.of("+01:01");
        assertThat(zo1.getId()).isEqualTo("+01:01");
        assertThat(zo1.getTotalSeconds()).isEqualTo(3660);
        zo1 = ZoneOffset.of("-01:01");
        assertThat(zo1.getId()).isEqualTo("-01:01");
        assertThat(zo1.getTotalSeconds()).isEqualTo(-3660);
        zo1 = ZoneOffset.of("+0101");
        assertThat(zo1.getId()).isEqualTo("+01:01");
        assertThat(zo1.getTotalSeconds()).isEqualTo(3660);
        zo1 = ZoneOffset.of("-0101");
        assertThat(zo1.getId()).isEqualTo("-01:01");
        assertThat(zo1.getTotalSeconds()).isEqualTo(-3660);
        zo1 = ZoneOffset.of("+01:02:03");
        assertThat(zo1.getId()).isEqualTo("+01:02:03");
        assertThat(zo1.getTotalSeconds()).isEqualTo(3723);
        zo1 = ZoneOffset.of("-01:02:03");
        assertThat(zo1.getId()).isEqualTo("-01:02:03");
        assertThat(zo1.getTotalSeconds()).isEqualTo(-3723);
        zo1 = ZoneOffset.of("+010203");
        assertThat(zo1.getId()).isEqualTo("+01:02:03");
        assertThat(zo1.getTotalSeconds()).isEqualTo(3723);
        zo1 = ZoneOffset.of("-010203");
        assertThat(zo1.getId()).isEqualTo("-01:02:03");
        assertThat(zo1.getTotalSeconds()).isEqualTo(-3723);

        // ZoneOffset.ofHours() demo
        zo1 = ZoneOffset.ofHours(0);
        assertThat(zo1.getId()).isEqualTo("Z");
        assertThat(zo1.getTotalSeconds()).isEqualTo(0);
        zo1 = ZoneOffset.ofHours(1);
        assertThat(zo1.getId()).isEqualTo("+01:00");
        assertThat(zo1.getTotalSeconds()).isEqualTo(3600);
        zo1 = ZoneOffset.ofHours(-1);
        assertThat(zo1.getId()).isEqualTo("-01:00");
        assertThat(zo1.getTotalSeconds()).isEqualTo(-3600);

        // ZoneOffset.ofHoursMinutes() demo
        zo1 = ZoneOffset.ofHoursMinutes(1, 2);
        assertThat(zo1.getId()).isEqualTo("+01:02");
        assertThat(zo1.getTotalSeconds()).isEqualTo(3720);
        // 片方だけがnegative値だと DateTimeException 発生
        zo1 = ZoneOffset.ofHoursMinutes(-1, -2);
        assertThat(zo1.getId()).isEqualTo("-01:02");
        assertThat(zo1.getTotalSeconds()).isEqualTo(-3720);

        // ZoneOffset.ofHoursMinutesSeconds() demo
        zo1 = ZoneOffset.ofHoursMinutesSeconds(1, 2, 3);
        assertThat(zo1.getId()).isEqualTo("+01:02:03");
        assertThat(zo1.getTotalSeconds()).isEqualTo(3723);
        // plus/negative混在だと DateTimeException 発生
        zo1 = ZoneOffset.ofHoursMinutesSeconds(-1, -2, -3);
        assertThat(zo1.getId()).isEqualTo("-01:02:03");
        assertThat(zo1.getTotalSeconds()).isEqualTo(-3723);

        // ZoneOffset.ofTotalSeconds() demo
        zo1 = ZoneOffset.ofTotalSeconds(1);
        assertThat(zo1.getId()).isEqualTo("+00:00:01");
        assertThat(zo1.getTotalSeconds()).isEqualTo(1);
        zo1 = ZoneOffset.ofTotalSeconds(-1);
        assertThat(zo1.getId()).isEqualTo("-00:00:01");
        assertThat(zo1.getTotalSeconds()).isEqualTo(-1);
    }

    @Test
    public void testOffsetDateTimeDemo() {
        ZoneOffset zo1 = ZoneOffset.of("+01:30");
        LocalDateTime ldt1 = LocalDateTime.of(1900, 1, 2, 3, 4, 5);
        // LocalDateTime に ZoneOffset 情報をつける。(日時そのものは変えない)
        OffsetDateTime odt1 = OffsetDateTime.of(ldt1, zo1);
        assertThat(odt1.toString()).isEqualTo("1900-01-02T03:04:05+01:30");
        odt1 = OffsetDateTime.of(LocalDate.of(1900, 1, 2), LocalTime.of(3, 4, 5), zo1);
        assertThat(odt1.toString()).isEqualTo("1900-01-02T03:04:05+01:30");
        odt1 = OffsetDateTime.of(1900, 1, 2, 3, 4, 5, 600, zo1);
        assertThat(odt1.toString()).isEqualTo("1900-01-02T03:04:05.000000600+01:30");

        OffsetTime ot1 = OffsetTime.of(LocalTime.of(1, 2, 3), zo1);
        assertThat(ot1.toString()).isEqualTo("01:02:03+01:30");
        ot1 = OffsetTime.of(1, 2, 3, 400, zo1);
        assertThat(ot1.toString()).isEqualTo("01:02:03.000000400+01:30");

        ldt1 = LocalDateTime.of(1900, 1, 1, 0, 0, 0);
        OffsetDateTime odt2a = OffsetDateTime.of(ldt1, ZoneOffset.of("+01:30"));
        OffsetDateTime odt2b = OffsetDateTime.of(ldt1, ZoneOffset.of("+01:30"));
        ldt1 = LocalDateTime.of(1900, 1, 1, 1, 0, 0);
        OffsetDateTime odt2c = OffsetDateTime.of(ldt1, ZoneOffset.of("+00:30"));
        assertThat(odt2a == odt2b).isFalse();
        assertThat(odt2a.equals(odt2b)).isTrue();
        // 時差を計算すれば同じ時点(Instant)にはなるが、OffsetDateTime自体のequals()はfalse.
        assertThat(odt2a.equals(odt2c)).isFalse();

        OffsetTime ot2a = OffsetTime.of(LocalTime.of(1, 0), ZoneOffset.of("+01:00"));
        OffsetTime ot2b = OffsetTime.of(LocalTime.of(1, 0), ZoneOffset.of("+01:00"));
        OffsetTime ot2c = OffsetTime.of(LocalTime.of(2, 0), ZoneOffset.of("+00:00"));
        assertThat(ot2a == ot2b).isFalse();
        assertThat(ot2a.equals(ot2b)).isTrue();
        // 時差を計算すれば同じ時点(Instant)にはなるが、OffsetTime自体のequals()はfalse.
        assertThat(ot2a.equals(ot2c)).isFalse();
    }

    @Test
    public void testSummerTimeOrDaylightSavingTimeDemo() {
        /* 2016年の夏時間開始日時 : 2016-03-13, 02:00 (EST, 東部標準時)
         * 2016年の夏時間終了日時 : 2016-11-06, 02:00 (EDT, 東部夏時間)
         */
        ZoneId zi1 = ZoneId.of("America/Los_Angeles");
        LocalDate ld1 = LocalDate.of(2016, Month.MARCH, 13);
        LocalTime lt1 = LocalTime.of(1, 00); // 切り替え前
        LocalTime lt2 = LocalTime.of(2, 00); // 切り替え中
        // 切り替え前はそのまま、時差も標準の -08:00
        ZonedDateTime zdt1 = ZonedDateTime.of(ld1, lt1, zi1);
        assertThat(zdt1.toString()).isEqualTo("2016-03-13T01:00-08:00[America/Los_Angeles]");
        // 切り替え後だと、1時間進むため 02:00 -> 03:00 で1時間進み、時差も1時間早まり -07:00 になる。
        ZonedDateTime zdt2 = ZonedDateTime.of(ld1, lt2, zi1);
        assertThat(zdt2.toString()).isEqualTo("2016-03-13T03:00-07:00[America/Los_Angeles]");
        // -> 2020-03-13T02:00:00 と 2020-03-13T03:00:00 は等しくなる。
        ZonedDateTime zdt3 = ZonedDateTime.of(ld1, LocalTime.of(3, 0), zi1);
        assertThat(zdt3.toString()).isEqualTo("2016-03-13T03:00-07:00[America/Los_Angeles]");
        assertThat(zdt2.equals(zdt3)).isTrue();

        ld1 = LocalDate.of(2016, Month.NOVEMBER, 6);
        lt1 = LocalTime.of(1, 00); // 切り替え中
        lt2 = LocalTime.of(2, 00); // 東部標準時に戻る
        zdt1 = ZonedDateTime.of(ld1, lt1, zi1);
        assertThat(zdt1.toString()).isEqualTo("2016-11-06T01:00-07:00[America/Los_Angeles]");
        // 2016-11-06 02:00 時点では標準時に戻っているため、そのままとなる。
        zdt2 = ZonedDateTime.of(ld1, lt2, zi1);
        assertThat(zdt2.toString()).isEqualTo("2016-11-06T02:00-08:00[America/Los_Angeles]");

        ld1 = LocalDate.of(2016, Month.MARCH, 13);
        lt1 = LocalTime.of(1, 59); // 切り替え前
        zdt1 = ZonedDateTime.of(ld1, lt1, zi1);
        // 1:59 の1分後は、3:00 になる。(2:00 はスキップされる)
        assertThat(zdt1.plusMinutes(1).toString()).isEqualTo("2016-03-13T03:00-07:00[America/Los_Angeles]");

        ld1 = LocalDate.of(2016, Month.NOVEMBER, 6);
        lt1 = LocalTime.of(1, 59); // 切り替え前
        zdt1 = ZonedDateTime.of(ld1, lt1, zi1);
        // 標準時に戻るときは、1:59 -> 1:00 に戻る。
        assertThat(zdt1.plusMinutes(1).toString()).isEqualTo("2016-11-06T01:00-08:00[America/Los_Angeles]");
        // さらに1時間加えると、1:59 -> 1:00 -> (+60m) -> 2:00 になる。
        assertThat(zdt1.plusMinutes(61).toString()).isEqualTo("2016-11-06T02:00-08:00[America/Los_Angeles]");
    }

    void assertPeriod(final Period period, final String str, final int years, final int months, final int days,
            final long totalMonths, final boolean isNegative, final boolean isZero) {
        assertThat(period.toString()).isEqualTo(str);
        assertThat(period.getYears()).isEqualTo(years);
        assertThat(period.getMonths()).isEqualTo(months);
        assertThat(period.getDays()).isEqualTo(days);
        assertThat(period.toTotalMonths()).isEqualTo(totalMonths);
        assertThat(period.isNegative()).isEqualTo(isNegative);
        assertThat(period.isZero()).isEqualTo(isZero);
    }

    void assertPeriod(final Period period, final String str, final int years, final int months, final int days,
            final long totalMonths) {
        assertPeriod(period, str, years, months, days, totalMonths, false, false);
    }

    @SuppressWarnings("static-access")
    @Test
    public void testPeriodDemo() {
        LocalDate start = LocalDate.of(2016, Month.JANUARY, 1);
        LocalDate end = LocalDate.of(2017, Month.MARCH, 5);
        // start < end -> positive number
        assertPeriod(Period.between(start, end), "P1Y2M4D", 1, 2, 4, 14L);

        // start = end -> zero
        assertPeriod(Period.between(start, start), "P0D", 0, 0, 0, 0L, false, true);
        // zero period
        assertPeriod(Period.ZERO, "P0D", 0, 0, 0, 0L, false, true);

        // start > end -> negative number
        assertPeriod(Period.between(end, start), "P-1Y-2M-4D", -1, -2, -4, -14L, true, false);

        assertPeriod(Period.ofYears(1), "P1Y", 1, 0, 0, 12L);
        assertPeriod(Period.ofMonths(2), "P2M", 0, 2, 0, 2L);
        assertPeriod(Period.ofWeeks(3), "P21D", 0, 0, 21, 0L); // ofWeeks() は日単位に変換される。
        assertPeriod(Period.ofDays(4), "P4D", 0, 0, 4, 0L);
        assertPeriod(Period.parse("P1Y2M3D"), "P1Y2M3D", 1, 2, 3, 14L);
        assertPeriod(Period.parse("P1Y"), "P1Y", 1, 0, 0, 12L);
        assertPeriod(Period.parse("P2M"), "P2M", 0, 2, 0, 2L);
        assertPeriod(Period.parse("P3D"), "P3D", 0, 0, 3, 0L);
        assertPeriod(Period.parse("P1Y0M2D"), "P1Y2D", 1, 0, 2, 12L);
        assertPeriod(Period.parse("P0Y1M0D"), "P1M", 0, 1, 0, 1L);
        assertThatThrownBy(() -> {
            Period.parse("x");
        }).isInstanceOf(DateTimeParseException.class);

        assertPeriod(Period.ofDays(30), "P30D", 0, 0, 30, 0L);
        assertPeriod(Period.ofDays(31), "P31D", 0, 0, 31, 0L);
        assertPeriod(Period.ofDays(32), "P32D", 0, 0, 32, 0L);
        assertPeriod(Period.ofDays(100), "P100D", 0, 0, 100, 0L); // 自動で月単位に変換はされない。
        assertPeriod(Period.of(1, 2, 3), "P1Y2M3D", 1, 2, 3, 14L);
        assertPeriod(Period.of(0, 0, 1), "P1D", 0, 0, 1, 0L);
        // y, m, d それぞれで指定した通りになり、自動で繰り上がることは無い。
        assertPeriod(Period.of(0, 25, 700), "P25M700D", 0, 25, 700, 25L);

        // これはインスタンス経由で static method を呼んでるだけなので、最終的な ofDays(30) が返される。
        assertPeriod(Period.ofYears(1).ofMonths(1).ofDays(30), "P30D", 0, 0, 30, 0L);

        assertPeriod(Period.ofDays(1).plusDays(2), "P3D", 0, 0, 3, 0L);
        assertPeriod(Period.ofDays(1).plusDays(60), "P61D", 0, 0, 61, 0L);
        assertPeriod(Period.ofDays(1).plusMonths(13), "P13M1D", 0, 13, 1, 13L);
        assertPeriod(Period.ofDays(1).plusYears(1), "P1Y1D", 1, 0, 1, 12L);

        assertPeriod(Period.ofDays(1).minusDays(2), "P-1D", 0, 0, -1, 0L, true, false);
        assertPeriod(Period.ofDays(1).minusDays(60), "P-59D", 0, 0, -59, 0L, true, false);
        assertPeriod(Period.ofDays(1).minusMonths(13), "P-13M1D", 0, -13, 1, -13L, true, false);
        assertPeriod(Period.ofDays(1).minusYears(1), "P-1Y1D", -1, 0, 1, -12L, true, false);

        LocalDateTime ldt1 = LocalDateTime.of(2020, 2, 28, 12, 59, 59); // include 2/29 (leap year)
        assertThat(ldt1.plus(Period.ofDays(2)).toString()).isEqualTo("2020-03-01T12:59:59");
        ldt1 = LocalDateTime.of(2020, 3, 1, 12, 59, 59);
        assertThat(ldt1.minus(Period.ofDays(2)).toString()).isEqualTo("2020-02-28T12:59:59");
        // 加算で繰り上げされる。
        assertThat(ldt1.plus(Period.of(1, 13, 60)).toString()).isEqualTo("2022-05-31T12:59:59");
        // negative 指定したフィールド分だけ、繰り下げされる。
        assertThat(ldt1.plus(Period.of(1, -13, 60)).toString()).isEqualTo("2020-04-01T12:59:59");
        assertThat(ldt1.minus(Period.of(1, 13, 60)).toString()).isEqualTo("2017-12-03T12:59:59");
        // - (-13) で加算になり、繰り上げ・繰り下げ処理が発生する。
        assertThat(ldt1.minus(Period.of(1, -13, 60)).toString()).isEqualTo("2020-02-01T12:59:59");

        LocalDate ld1 = LocalDate.of(2020, 2, 28);
        assertThat(ld1.plus(Period.ofDays(2)).toString()).isEqualTo("2020-03-01");
        ld1 = LocalDate.of(2020, 3, 1);
        assertThat(ld1.minus(Period.ofDays(2)).toString()).isEqualTo("2020-02-28");

        // LocalTime.plus()/minus() で Period を指定すると、実行時例外発生(未サポート)
        assertThatThrownBy(() -> {
            LocalTime.of(1, 2).plus(Period.ofDays(1));
        }).isInstanceOf(UnsupportedTemporalTypeException.class);
        assertThatThrownBy(() -> {
            LocalTime.of(1, 2).minus(Period.ofDays(1));
        }).isInstanceOf(UnsupportedTemporalTypeException.class);

        // Period.equals() demo
        Period p1a = Period.ofDays(7);
        Period p1b = Period.ofDays(7);
        Period p1c = Period.ofWeeks(1);
        assertThat(p1a == p1b).isFalse();
        assertThat(p1a.equals(p1b)).isTrue();
        // Period.ofWeeks() のみ、ofDays() のインスタンスと equals() == true となる。
        assertThat(p1a.equals(p1c)).isTrue();
        Period p1d = Period.ofDays(30);
        Period p1e = Period.ofMonths(1);
        assertThat(p1d.equals(p1e)).isFalse();
        Period p1f = Period.ofDays(365);
        Period p1g = Period.ofYears(1);
        assertThat(p1f.equals(p1g)).isFalse();
    }

    void assertDuration(
            final Duration duration,
            final String str,
            final long totalDays,
            final long totalHours,
            final long totalMinutes,
            final long totalSeconds,
            final int nanosPart,
            final long totalMillis,
            final long totalNanos,
            final boolean isNegative,
            final boolean isZero) {
        assertThat(duration.toString()).isEqualTo(str);
        assertThat(duration.toDays()).isEqualTo(totalDays);
        assertThat(duration.toHours()).isEqualTo(totalHours);
        assertThat(duration.toMinutes()).isEqualTo(totalMinutes);
        assertThat(duration.getSeconds()).isEqualTo(totalSeconds);
        assertThat(duration.getNano()).isEqualTo(nanosPart);
        assertThat(duration.toMillis()).isEqualTo(totalMillis);
        assertThat(duration.toNanos()).isEqualTo(totalNanos);
        assertThat(duration.isNegative()).isEqualTo(isNegative);
        assertThat(duration.isZero()).isEqualTo(isZero);
    }

    void assertDuration(
            final Duration duration,
            final String str,
            final long totalDays,
            final long totalHours,
            final long totalMinutes,
            final long totalSeconds,
            final int nanosPart) {
        assertThat(duration.toString()).isEqualTo(str);
        assertThat(duration.toDays()).isEqualTo(totalDays);
        assertThat(duration.toHours()).isEqualTo(totalHours);
        assertThat(duration.toMinutes()).isEqualTo(totalMinutes);
        assertThat(duration.getSeconds()).isEqualTo(totalSeconds);
        assertThat(duration.getNano()).isEqualTo(nanosPart);
    }

    @SuppressWarnings("static-access")
    @Test
    public void testDurationDemo() {
        LocalTime start = LocalTime.of(1, 2, 3, 400);
        LocalTime end = LocalTime.of(2, 3, 4, 500);
        // start < end -> positive number
        assertDuration(
                Duration.between(start, end),
                "PT1H1M1.0000001S",
                0, // total days
                1, // total hours
                61, // total minutes
                3661, // total seconds
                100, // nanos (parted)
                3_661_000, // total millis
                3_661_000_000_100L, // total nanos
                false, false);

        // start = end -> zero
        assertDuration(Duration.between(start, start), "PT0S", 0, 0, 0, 0, 0, 0, 0, false, true);
        // zero period
        assertDuration(Duration.ZERO, "PT0S", 0, 0, 0, 0, 0, 0, 0, false, true);

        // start > end -> negative number
        assertDuration(
                Duration.between(end, start),
                "PT-1H-1M-1.0000001S",
                0, // total days
                -1, // total hours
                -61, // total minutes
                -3662, // total seconds
                999999900, // nanos (parted)
                -3_661_000, // total millis
                -3_661_000_000_100L, // total nanos
                true, false);

        // Duration.of{Days|Hours|Minutes}() は、下位の単位について自動的に変換される。
        assertDuration(Duration.ofDays(2), "PT48H", 2, 48, 2880, 172800, 0);
        assertDuration(Duration.ofHours(3), "PT3H", 0, 3, 180, 10800, 0);
        // total days が自動計算されるが、文字表現としてはHまで。
        assertDuration(Duration.ofHours(25), "PT25H", 1, 25, 1500, 90_000, 0);
        assertDuration(Duration.ofMinutes(4), "PT4M", 0, 0, 4, 240, 0);
        // total hours が自動計算され、文字表現も1H1Mに繰り上がる。
        assertDuration(Duration.ofMinutes(61), "PT1H1M", 0, 1, 61, 3660, 0);
        assertDuration(Duration.ofSeconds(5), "PT5S", 0, 0, 0, 5, 0);
        // total hours/minutes が自動計算され、文字表現も1H1M1Sに繰り上がる。
        assertDuration(Duration.ofSeconds(3661), "PT1H1M1S", 0, 1, 61, 3661, 0);
        assertDuration(Duration.ofSeconds(5, 100), "PT5.0000001S", 0, 0, 0, 5, 100);
        assertDuration(Duration.ofMillis(123), "PT0.123S", 0, 0, 0, 0, 123_000_000);
        assertDuration(Duration.ofNanos(123_456_789), "PT0.123456789S", 0, 0, 0, 0, 123_456_789);
        // Duration.of(amount, unit) demo
        assertDuration(Duration.of(1, ChronoUnit.SECONDS), "PT1S", 0, 0, 0, 1, 0);
        // Duration.parse() demo
        assertDuration(Duration.parse("PT1H"), "PT1H", 0, 1, 60, 3600, 0);
        assertDuration(Duration.parse("PT1M"), "PT1M", 0, 0, 1, 60, 0);
        assertDuration(Duration.parse("PT1S"), "PT1S", 0, 0, 0, 1, 0);
        assertDuration(Duration.parse("PT1.100S"), "PT1.1S", 0, 0, 0, 1, 100_000_000);
        assertDuration(Duration.parse("PT1H0M1S"), "PT1H1S", 0, 1, 60, 3601, 0);
        assertDuration(Duration.parse("PT0H1M0S"), "PT1M", 0, 0, 1, 60, 0);
        // 文字表現では、分・秒以下が繰り上がり処理される。
        assertDuration(Duration.parse("PT49H61M62S"), "PT50H2M2S", 2, 50, 3002, 180_122, 0);
        assertThatThrownBy(() -> {
            Duration.parse("x");
        }).isInstanceOf(DateTimeParseException.class);
        assertThatThrownBy(() -> {
            // "D" (日付) 表現は未サポート
            Duration.parse("PT1D");
        }).isInstanceOf(DateTimeParseException.class);

        // これはインスタンス経由で static method を呼んでるだけなので、最終的な ofSeconds(4) が返される。
        assertDuration(Duration.ofDays(2).ofMinutes(3).ofSeconds(4), "PT4S", 0, 0, 0, 4, 0);

        Duration d0 = Duration.ofDays(1);
        assertDuration(d0.plusDays(1), "PT48H", 2, 48, 2880, 172800, 0);
        assertDuration(d0.plusHours(1), "PT25H", 1, 25, 1500, 90000, 0);
        assertDuration(d0.plusHours(25), "PT49H", 2, 49, 2940, 176400, 0);
        assertDuration(d0.plusMinutes(1), "PT24H1M", 1, 24, 1441, 86460, 0);
        assertDuration(d0.plusMinutes(61), "PT25H1M", 1, 25, 1501, 90060, 0);
        assertDuration(d0.plusSeconds(1), "PT24H1S", 1, 24, 1440, 86401, 0);
        assertDuration(d0.plusSeconds(61), "PT24H1M1S", 1, 24, 1441, 86461, 0);
        assertDuration(d0.plusMillis(1), "PT24H0.001S", 1, 24, 1440, 86400, 1_000_000);
        assertDuration(d0.plusMillis(1234), "PT24H1.234S", 1, 24, 1440, 86401, 234_000_000);
        assertDuration(d0.plusNanos(1), "PT24H0.000000001S", 1, 24, 1440, 86400, 1);

        d0 = Duration.ZERO;
        assertDuration(d0.minusDays(1), "PT-24H", -1, -24, -1440, -86400, 0);
        assertDuration(d0.minusHours(1), "PT-1H", 0, -1, -60, -3600, 0);
        assertDuration(d0.minusMinutes(1), "PT-1M", 0, 0, -1, -60, 0);
        assertDuration(d0.minusSeconds(1), "PT-1S", 0, 0, 0, -1, 0);
        // ミリ秒以下のnegativeは、-1sec + positive nanos での差分になる。
        assertDuration(d0.minusMillis(1), "PT-0.001S", 0, 0, 0, -1, 999_000_000);
        assertDuration(d0.minusNanos(1), "PT-0.000000001S", 0, 0, 0, -1, 999999999);

        LocalDateTime ldt1 = LocalDateTime.of(2020, 2, 28, 23, 59, 59); // include 2/29 (leap year)
        // 加算繰り上げ
        assertThat(ldt1.plus(Duration.ofSeconds(2)).toString()).isEqualTo("2020-02-29T00:00:01");
        // 減算繰り下げ
        ldt1 = LocalDateTime.of(2020, 3, 1, 0, 0, 0);
        assertThat(ldt1.minus(Duration.ofSeconds(2)).toString()).isEqualTo("2020-02-29T23:59:58");

        LocalTime lt1 = LocalTime.of(23, 59, 59);
        // 加算して繰り上げ分は無視される。
        assertThat(lt1.plus(Duration.ofHours(1)).toString()).isEqualTo("00:59:59");
        assertThat(lt1.plus(Duration.ofMinutes(1)).toString()).isEqualTo("00:00:59");
        assertThat(lt1.plus(Duration.ofSeconds(1)).toString()).isEqualTo("00:00"); // hmmm...
        lt1 = LocalTime.of(1, 2, 3);
        // 減算して繰り下げ分は無視される。(negative value にはならない)
        assertThat(lt1.minus(Duration.ofHours(2)).toString()).isEqualTo("23:02:03");
        assertThat(lt1.minus(Duration.ofMinutes(3)).toString()).isEqualTo("00:59:03");
        assertThat(lt1.minus(Duration.ofSeconds(3)).toString()).isEqualTo("01:02"); // hmmm...
        assertThat(lt1.minus(Duration.ofSeconds(4)).toString()).isEqualTo("01:01:59");

        // LocalDate.plus()/minus() で Duration を指定すると、実行時例外発生(未サポート)
        assertThatThrownBy(() -> {
            LocalDate.of(2020, 1, 2).plus(Duration.ofSeconds(1));
        }).isInstanceOf(UnsupportedTemporalTypeException.class);
        assertThatThrownBy(() -> {
            LocalDate.of(2020, 1, 2).minus(Duration.ofSeconds(1));
        }).isInstanceOf(UnsupportedTemporalTypeException.class);

        // Duration.equals() demo
        Duration d2a = Duration.ofSeconds(60);
        Duration d2b = Duration.ofSeconds(60);
        Duration d2c = Duration.ofMinutes(1);
        assertThat(d2a == d2b).isFalse();
        assertThat(d2a.equals(d2b)).isTrue();
        assertThat(d2a.equals(d2c)).isTrue();
        // Duration では単位が自動調整され、同じ秒数であれば equals() == true になる。
        Duration d2d = Duration.ofSeconds(3600);
        Duration d2e = Duration.ofMinutes(60);
        Duration d2g = Duration.ofHours(1);
        assertThat(d2d.equals(d2e)).isTrue();
        assertThat(d2e.equals(d2g)).isTrue();
        assertThat(d2d.equals(d2g)).isTrue();
    }

    void assertInstant(
            final Instant is,
            final String str,
            final long epochTotalMillis,
            final long epochSecondsPart,
            final int nanoPart) {
        assertThat(is.toString()).isEqualTo(str);
        assertThat(is.toEpochMilli()).isEqualTo(epochTotalMillis);
        assertThat(is.getEpochSecond()).isEqualTo(epochSecondsPart);
        assertThat(is.getNano()).isEqualTo(nanoPart);
    }

    @Test
    public void testInstantDemo() {
        assertInstant(Instant.EPOCH, "1970-01-01T00:00:00Z", 0, 0, 0);
        assertInstant(Instant.ofEpochSecond(1), "1970-01-01T00:00:01Z", 1_000, 1, 0);
        assertInstant(Instant.ofEpochSecond(1, 1_000_000), "1970-01-01T00:00:01.001Z", 1_001, 1, 1_000_000);
        assertInstant(Instant.ofEpochMilli(1234), "1970-01-01T00:00:01.234Z", 1_234, 1, 234_000_000);

        assertInstant(Instant.ofEpochSecond(-1), "1969-12-31T23:59:59Z", -1_000, -1, 0);
        assertInstant(Instant.ofEpochSecond(-1, -1_000_000), "1969-12-31T23:59:58.999Z", -1_001, -2, 999_000_000);
        assertInstant(Instant.ofEpochMilli(-1234), "1969-12-31T23:59:58.766Z", -1_234, -2, 766_000_000);

        // NOTE: == は isAfter()/isBefore() で常にfalse
        assertThat(Instant.EPOCH.isAfter(Instant.ofEpochSecond(1))).isFalse();
        assertThat(Instant.EPOCH.isAfter(Instant.ofEpochSecond(0))).isFalse();
        assertThat(Instant.EPOCH.isAfter(Instant.ofEpochSecond(-1))).isTrue();
        assertThat(Instant.EPOCH.isBefore(Instant.ofEpochSecond(1))).isTrue();
        assertThat(Instant.EPOCH.isBefore(Instant.ofEpochSecond(0))).isFalse();
        assertThat(Instant.EPOCH.isBefore(Instant.ofEpochSecond(-1))).isFalse();

        LocalDateTime ldt1 = LocalDateTime.of(1970, 1, 1, 0, 0, 1);
        // LocalDateTime + ZoneId -> ZonedDateTime -> Instant
        ZonedDateTime zdt1 = ldt1.atZone(ZoneId.of("UTC"));
        assertInstant(zdt1.toInstant(), "1970-01-01T00:00:01Z", 1_000, 1, 0);
        // Asia/Tokyo(+09:00) で "1970-01-01T00:00:01" ということは、epoch ではその9時間前になる。 
        zdt1 = ldt1.atZone(ZoneId.of("Asia/Tokyo"));
        assertInstant(zdt1.toInstant(), "1969-12-31T15:00:01Z", -32399000, -32399, 0);

        // LocalDateTime + ZoneOffset -> OffsetDateTime -> Instant
        assertInstant(ldt1.toInstant(ZoneOffset.UTC), "1970-01-01T00:00:01Z", 1_000, 1, 0);
        OffsetDateTime odt1 = ldt1.atOffset(ZoneOffset.UTC);
        assertInstant(odt1.toInstant(), "1970-01-01T00:00:01Z", 1_000, 1, 0);
        assertInstant(ldt1.toInstant(ZoneOffset.of("+0900")), "1969-12-31T15:00:01Z", -32399000, -32399, 0);
        odt1 = ldt1.atOffset(ZoneOffset.of("+09:00"));
        assertInstant(odt1.toInstant(), "1969-12-31T15:00:01Z", -32399000, -32399, 0);

        Instant is0 = Instant.EPOCH;
        assertInstant(is0.plus(1, ChronoUnit.SECONDS), "1970-01-01T00:00:01Z", 1_000, 1, 0);
        assertInstant(is0.plusSeconds(1), "1970-01-01T00:00:01Z", 1_000, 1, 0);
        assertInstant(is0.plusNanos(1_000_000), "1970-01-01T00:00:00.001Z", 1, 0, 1_000_000);
        assertInstant(is0.plusMillis(1234), "1970-01-01T00:00:01.234Z", 1_234, 1, 234_000_000);

        assertInstant(is0.minus(1, ChronoUnit.SECONDS), "1969-12-31T23:59:59Z", -1_000, -1, 0);
        assertInstant(is0.minusSeconds(1), "1969-12-31T23:59:59Z", -1_000, -1, 0);
        assertInstant(is0.minusNanos(1_000_000), "1969-12-31T23:59:59.999Z", -1, -1, 999_000_000);
        assertInstant(is0.minusMillis(1234), "1969-12-31T23:59:58.766Z", -1_234, -2, 766_000_000);

        assertThat(is0.isSupported(ChronoField.YEAR)).isFalse();
        assertThat(is0.isSupported(ChronoUnit.YEARS)).isFalse();
        assertThat(is0.isSupported(ChronoUnit.SECONDS)).isTrue();

        assertInstant(is0.plus(1, ChronoUnit.NANOS), "1970-01-01T00:00:00.000000001Z", 0, 0, 1);
        assertInstant(is0.plus(1, ChronoUnit.MICROS), "1970-01-01T00:00:00.000001Z", 0, 0, 1_000);
        assertInstant(is0.plus(1, ChronoUnit.MILLIS), "1970-01-01T00:00:00.001Z", 1, 0, 1_000_000);
        assertInstant(is0.plus(1, ChronoUnit.SECONDS), "1970-01-01T00:00:01Z", 1_000, 1, 0);
        assertInstant(is0.plus(1, ChronoUnit.MINUTES), "1970-01-01T00:01:00Z", 60_000, 60, 0);
        assertInstant(is0.plus(1, ChronoUnit.HOURS), "1970-01-01T01:00:00Z", 3600_000, 3600, 0);
        assertInstant(is0.plus(1, ChronoUnit.HALF_DAYS), "1970-01-01T12:00:00Z", 43_200_000, 43_200, 0);
        assertInstant(is0.plus(1, ChronoUnit.DAYS), "1970-01-02T00:00:00Z", 86_400_000, 86_400, 0);

        assertThatThrownBy(() -> {
            Instant.EPOCH.plus(1, ChronoUnit.YEARS);
        }).isInstanceOf(UnsupportedTemporalTypeException.class);
        assertThatThrownBy(() -> {
            Instant.EPOCH.minus(1, ChronoUnit.YEARS);
        }).isInstanceOf(UnsupportedTemporalTypeException.class);

        // Instant.equals() demo
        Instant is2a = Instant.EPOCH;
        Instant is2b = Instant.ofEpochSecond(0).plusSeconds(1).minusSeconds(1);
        assertThat(is2a == is2b).isTrue(); // 内部で差し替えてる？
        assertThat(is2a.equals(is2b)).isTrue();
        Instant is2c = Instant.ofEpochSecond(1).plusSeconds(1).minusSeconds(1);
        Instant is2d = Instant.ofEpochSecond(1).plusSeconds(1).minusSeconds(1);
        assertThat(is2c == is2d).isFalse();
        assertThat(is2c.equals(is2d)).isTrue();
    }
}
