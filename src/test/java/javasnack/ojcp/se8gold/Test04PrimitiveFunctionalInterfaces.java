package javasnack.ojcp.se8gold;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;
import java.util.function.DoublePredicate;
import java.util.function.DoubleSupplier;
import java.util.function.DoubleToIntFunction;
import java.util.function.DoubleToLongFunction;
import java.util.function.DoubleUnaryOperator;
import java.util.function.IntBinaryOperator;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.IntSupplier;
import java.util.function.IntToDoubleFunction;
import java.util.function.IntToLongFunction;
import java.util.function.IntUnaryOperator;
import java.util.function.LongBinaryOperator;
import java.util.function.LongConsumer;
import java.util.function.LongFunction;
import java.util.function.LongPredicate;
import java.util.function.LongSupplier;
import java.util.function.LongToDoubleFunction;
import java.util.function.LongToIntFunction;
import java.util.function.LongUnaryOperator;
import java.util.function.ObjDoubleConsumer;
import java.util.function.ObjIntConsumer;
import java.util.function.ObjLongConsumer;
import java.util.function.ToDoubleBiFunction;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntBiFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongBiFunction;
import java.util.function.ToLongFunction;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

public class Test04PrimitiveFunctionalInterfaces {

    /* int/long/double を引数に取る or 返す関数型インターフェイスのデモ
     * - Unary/BinaryOperator については applyAs{Int|Long|Double}() になる。
     * - Supplier については getAs{Int|Long|Double} になる。
     */

    @Test
    public void testFromIntegerDemo() {
        IntFunction<String> if1 = (n) -> Integer.toString(n);
        assertThat(if1.apply(10)).isEqualTo("10");

        StringBuilder sb = new StringBuilder();
        IntConsumer ic1 = (n) -> sb.append(Integer.toString(n));
        ic1.accept(10);
        assertThat(sb.toString()).isEqualTo("10");

        IntPredicate ip1 = (n) -> n == 10;
        assertThat(ip1.test(10)).isTrue();
        assertThat(ip1.test(11)).isFalse();

        IntSupplier is1 = () -> 100;
        assertThat(is1.getAsInt()).isEqualTo(100);

        IntUnaryOperator iuo1 = (n) -> n + 1;
        assertThat(iuo1.applyAsInt(10)).isEqualTo(11);

        IntBinaryOperator ibo1 = (x, y) -> x + y;
        assertThat(ibo1.applyAsInt(2, 3)).isEqualTo(5);
    }

    @Test
    public void testFromLongDemo() {
        LongFunction<String> lf1 = (n) -> Long.toString(n);
        assertThat(lf1.apply(10)).isEqualTo("10");

        StringBuilder sb = new StringBuilder();
        LongConsumer lc1 = (n) -> sb.append(Long.toString(n));
        lc1.accept(10);
        assertThat(sb.toString()).isEqualTo("10");

        LongPredicate lp1 = (n) -> n == 10;
        assertThat(lp1.test(10)).isTrue();
        assertThat(lp1.test(11)).isFalse();

        LongSupplier ls1 = () -> 100;
        assertThat(ls1.getAsLong()).isEqualTo(100);

        LongUnaryOperator luo1 = (n) -> n + 1;
        assertThat(luo1.applyAsLong(10)).isEqualTo(11);

        LongBinaryOperator lbo1 = (x, y) -> x + y;
        assertThat(lbo1.applyAsLong(2, 3)).isEqualTo(5);
    }

    @Test
    public void testFromDoubleDemo() {
        DoubleFunction<String> df1 = (n) -> Double.toString(n);
        assertThat(df1.apply(10.0)).isEqualTo("10.0");

        StringBuilder sb = new StringBuilder();
        DoubleConsumer dc1 = (n) -> sb.append(Double.toString(n));
        dc1.accept(10.0);
        assertThat(sb.toString()).isEqualTo("10.0");

        DoublePredicate dp1 = (n) -> n == 10;
        assertThat(dp1.test(10)).isTrue();
        assertThat(dp1.test(11)).isFalse();

        DoubleSupplier ds1 = () -> 100.0;
        assertThat(ds1.getAsDouble()).isEqualTo(100.0);

        DoubleUnaryOperator duo1 = (n) -> n + 0.1;
        assertThat(duo1.applyAsDouble(10)).isEqualTo(10.1);

        DoubleBinaryOperator dbo1 = (x, y) -> x + y + 0.1;
        assertThat(dbo1.applyAsDouble(2, 3)).isEqualTo(5.1);
    }

    @Test
    public void testFromBooleanDemo() {
        // boolean からは Supplier のみ対応。
        BooleanSupplier bs1 = () -> true;
        assertThat(bs1.getAsBoolean()).isTrue();
    }

    /* オブジェクトを int/long/double に変換する Function のデモ
     * - applyAs{Int|Long|Double}(T t) になる。
     */

    @Test
    public void testToIntDemo() {
        ToIntFunction<String> if1 = (String s) -> Integer.parseInt(s);
        assertThat(if1.applyAsInt("100")).isEqualTo(100);

        ToIntBiFunction<String, String> ibf1 = (String x, String y) -> Integer.parseInt(x) + Integer.parseInt(y);
        assertThat(ibf1.applyAsInt("10", "20")).isEqualTo(30);

        LongToIntFunction lif1 = (long v) -> (int) v - 1;
        assertThat(lif1.applyAsInt(10L)).isEqualTo(9);

        DoubleToIntFunction dif1 = (double d) -> (int) d + 1;
        assertThat(dif1.applyAsInt(10.1)).isEqualTo(11);
    }

    @Test
    public void testToLongDemo() {
        ToLongFunction<String> lf1 = (String s) -> Long.parseLong(s);
        assertThat(lf1.applyAsLong("100")).isEqualTo(100);

        ToLongBiFunction<String, String> lbf1 = (String x, String y) -> Long.parseLong(x) + Long.parseLong(y);
        assertThat(lbf1.applyAsLong("10", "20")).isEqualTo(30);

        IntToLongFunction ilf1 = (int v) -> v - 1;
        assertThat(ilf1.applyAsLong(10)).isEqualTo(9);

        DoubleToLongFunction dlf1 = (double d) -> (long) (d + 1);
        assertThat(dlf1.applyAsLong(10.1)).isEqualTo(11);
    }

    @Test
    public void testToDoubleDemo() {
        ToDoubleFunction<String> df1 = (String s) -> Double.parseDouble(s);
        assertThat(df1.applyAsDouble("100.1")).isEqualTo(100.1);

        ToDoubleBiFunction<String, String> dbf1 = (String x, String y) -> Double.parseDouble(x) + Double.parseDouble(y);
        assertThat(dbf1.applyAsDouble("10.1", "20.2")).isCloseTo(30.3, Offset.offset(0.1));

        IntToDoubleFunction idf1 = (int v) -> v + 1.1;
        assertThat(idf1.applyAsDouble(10)).isEqualTo(11.1);

        LongToDoubleFunction ldf1 = (long v) -> v + 1.1;
        assertThat(ldf1.applyAsDouble(10)).isEqualTo(11.1);
    }

    /* Object + int/long/double を引数に取る BiConsumer の派生型のデモ
     */

    @Test
    public void testObjectToPrimitiveConsumerDemo() {
        StringBuilder sb = new StringBuilder();
        ObjIntConsumer<String> c1 = (s, v) -> {
            sb.append(s);
            sb.append("int:" + v);
        };
        ObjLongConsumer<String> c2 = (s, v) -> {
            sb.append(s);
            sb.append("long:" + v);
        };
        ObjDoubleConsumer<String> c3 = (s, v) -> {
            sb.append(s);
            sb.append("double:" + v);
        };
        c1.accept(",s+i,", 10);
        c2.accept(",s+l,", 20);
        c3.accept(",s+d,", 30.1);
        assertThat(sb.toString()).isEqualTo(",s+i,int:10,s+l,long:20,s+d,double:30.1");
    }
}
