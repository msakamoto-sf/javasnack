package javasnack.ojcp.se8silver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.DateTimeException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Test;

public class Test12ApiDemoDateTime {

    @Test
    public void testLocalDateTimeDemo() {
        LocalDate date1 = LocalDate.of(2020, 2, 29);
        LocalTime time1 = LocalTime.of(23, 59, 59);
        LocalDateTime dt1 = LocalDateTime.of(2020, 2, 29, 23, 59, 59);

        LocalDate date2 = LocalDate.parse("2020-02-29");
        LocalTime time2 = LocalTime.parse("23:59:59");
        LocalDateTime dt2 = LocalDateTime.parse("2020-02-29T23:59:59");

        assertThat(date1).isEqualTo(date2);
        assertThat(time1).isEqualTo(time2);
        assertThat(dt1).isEqualTo(dt2);

        assertThatThrownBy(() -> {
            LocalDate.of(2019, 2, 29);
        }).isInstanceOf(DateTimeException.class);

        assertThatThrownBy(() -> {
            LocalTime.of(24, 0, 0);
        }).isInstanceOf(DateTimeException.class);

        assertThatThrownBy(() -> {
            LocalDateTime.of(2019, 2, 29, 23, 59, 59);
        }).isInstanceOf(DateTimeException.class);

        assertThatThrownBy(() -> {
            LocalDate.parse("2020-2-29");
        }).isInstanceOf(DateTimeException.class);

        assertThatThrownBy(() -> {
            LocalTime.parse("23:1:1");
        }).isInstanceOf(DateTimeException.class);

        assertThatThrownBy(() -> {
            LocalDateTime.parse("2020-02-29 23:59:59");
        }).isInstanceOf(DateTimeException.class);

        assertThat(date1.format(DateTimeFormatter.ISO_DATE)).isEqualTo("2020-02-29");
        assertThat(time1.format(DateTimeFormatter.ISO_TIME)).isEqualTo("23:59:59");
        assertThat(dt1.format(DateTimeFormatter.ISO_DATE_TIME)).isEqualTo("2020-02-29T23:59:59");
        assertThat(dt1.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"))).isEqualTo("2020/02/29 23:59:59");
        date2 = LocalDate.parse("2020/02/29", DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        assertThat(date1).isEqualTo(date2);

        date2 = date1.plusDays(1L);
        // >#>POINT<#<: 日付/時刻操作メソッドは元のオブジェクトは変更せず、新しいオブジェクトを返す。
        assertThat(date1.format(DateTimeFormatter.ISO_DATE)).isEqualTo("2020-02-29");
        assertThat(date2.format(DateTimeFormatter.ISO_DATE)).isEqualTo("2020-03-01");
        time2 = time1.plusSeconds(1L);
        assertThat(time1.format(DateTimeFormatter.ISO_TIME)).isEqualTo("23:59:59");
        assertThat(time2.format(DateTimeFormatter.ISO_TIME)).isEqualTo("00:00:00");

        date2 = date2.minusDays(1L);
        assertThat(date1).isEqualTo(date2);
        time2 = time2.minusSeconds(1L);
        assertThat(time1).isEqualTo(time2);
    }

    // Duration, Period などの参考記事
    // https://qiita.com/tag1216/items/9cb33a39a6666983491d

    @SuppressWarnings("unused")
    @Test
    public void testDurations() {
        // Duration は時間ベースの時間量
        // of からの生成例
        Duration d0 = Duration.of(1, ChronoUnit.DAYS);
        d0 = Duration.ofDays(1);
        d0 = Duration.ofHours(2);
        d0 = Duration.ofMinutes(3);
        d0 = Duration.ofSeconds(4);
        d0 = Duration.ofMillis(5);
        d0 = Duration.ofNanos(6);

        LocalDateTime dt1 = LocalDateTime.of(2020, 2, 29, 23, 59, 59);
        LocalDateTime dt2 = LocalDateTime.of(2020, 3, 2, 0, 1, 2);
        Duration d1 = Duration.between(dt1, dt2);
        assertThat(d1.isNegative()).isFalse();
        // 間隔が日/時/分/秒の各単位で取り出せる。(精度が各単位に丸められる)
        assertThat(d1.toDays()).isEqualTo(1);
        assertThat(d1.toHours()).isEqualTo(24);
        assertThat(d1.toMinutes()).isEqualTo(1441);
        assertThat(d1.toSeconds()).isEqualTo(86463L);
        // 他のDateTime APIと同様Immutable
        Duration d2 = d1.plusDays(2);
        assertThat(d1.toDays()).isEqualTo(1);
        assertThat(d1.toHours()).isEqualTo(24);
        assertThat(d1.toHoursPart()).isEqualTo(0);
        assertThat(d1.toMinutes()).isEqualTo(1441);
        assertThat(d1.toSeconds()).isEqualTo(86463L);
        assertThat(d2.toDays()).isEqualTo(3);
        assertThat(d2.toHours()).isEqualTo(72);
        assertThat(d2.toMinutes()).isEqualTo(4321);
        assertThat(d2.toSeconds()).isEqualTo(259263L);

        // LocalDateTime などで加減算ができる。
        LocalDateTime dt3 = dt1.plus(d2);
        assertThat(dt3.format(DateTimeFormatter.ISO_DATE_TIME)).isEqualTo("2020-03-04T00:01:02");

        // start -> end を逆にすると、負数になる。
        d1 = Duration.between(dt2, dt1);
        assertThat(d1.isNegative()).isTrue();
        assertThat(d1.toDays()).isEqualTo(-1);
        assertThat(d1.toHours()).isEqualTo(-24);
        assertThat(d1.toMinutes()).isEqualTo(-1441);
        assertThat(d1.toSeconds()).isEqualTo(-86463L);
    }

    @Test
    public void testPeriods() {
        // Period は日付ベースの時間量
        // of からの生成例
        Period p0 = Period.of(1, 2, 3); //year, month, day
        p0 = Period.ofWeeks(4);
        p0 = Period.ofDays(3);
        p0 = Period.ofMonths(2);
        p0 = Period.ofYears(1);

        LocalDateTime dt1 = LocalDateTime.of(2020, 2, 29, 23, 59, 59);
        LocalDateTime dt2 = LocalDateTime.of(2021, 5, 2, 0, 1, 2);
        p0 = Period.between(dt1.toLocalDate(), dt2.toLocalDate());
        assertThat(p0.isNegative()).isFalse();
        assertThat(p0.getYears()).isEqualTo(1);
        assertThat(p0.getMonths()).isEqualTo(2);
        assertThat(p0.getDays()).isEqualTo(3);

        Period p1 = p0.plusDays(4);
        assertThat(p0.getYears()).isEqualTo(1);
        assertThat(p0.getMonths()).isEqualTo(2);
        assertThat(p0.getDays()).isEqualTo(3);
        // 他のDateTime APIと同様Immutable
        assertThat(p1.getYears()).isEqualTo(1);
        assertThat(p1.getMonths()).isEqualTo(2);
        assertThat(p1.getDays()).isEqualTo(7);

        // LocalDate などで加減算ができる。
        LocalDate ld1 = dt1.toLocalDate().plus(p1);
        assertThat(ld1.format(DateTimeFormatter.ISO_DATE)).isEqualTo("2021-05-06");

        // start -> end を逆にすると、負数になる。
        p0 = Period.between(dt2.toLocalDate(), dt1.toLocalDate());
        assertThat(p0.isNegative()).isTrue();
        assertThat(p0.getYears()).isEqualTo(-1);
        assertThat(p0.getMonths()).isEqualTo(-2);
        assertThat(p0.getDays()).isEqualTo(-2); // え、ここ、-3 じゃないんだ・・・。
    }
}
