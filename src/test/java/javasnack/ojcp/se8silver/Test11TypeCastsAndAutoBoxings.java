package javasnack.ojcp.se8silver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.Test;

public class Test11TypeCastsAndAutoBoxings {
    @Test
    public void testPrimitiveTypesCastingDemos() {
        // implicit type cast
        byte bytev = 10;
        short shortv = bytev;
        int intv = shortv;
        long longv = intv;
        float floatv = longv;
        double doublev = floatv;
        assertThat(doublev).isEqualTo(10.0);

        char charv = 'a';
        int intv2 = charv;
        assertThat(intv2).isEqualTo(97);

        // >#>POINT<#< : down cast need explicit type casting
        doublev = 3.14;
        //floatv = doublev; // compile error
        floatv = (float) doublev;
        //longv = floatv; // compile error
        longv = (long) floatv;
        assertThat(longv).isEqualTo(3);
        //intv = longv; // compile error
        intv = (int) longv;
        //shortv = intv; // compile error;
        shortv = (short) intv;
        //bytev = shortv; // compile error;
        bytev = (byte) shortv;
        assertThat(bytev).isEqualTo((byte) 3);

        //charv = intv2; // compile error
        charv = (char) intv2;
        assertThat(charv).isEqualTo('a');

        // 以下は整数リテラルが代入先の型の範囲を超えるため compile error
        //bytev = 150;
        //shortv = 70_000;

        // >#>POINT<#<: 2つのオペランドを取る算術演算子においては up cast が発生する。
        doublev = 4.0;
        floatv = 2.0f;
        // double に up cast されるため compile error
        //floatv = floatv + 4.0;
        //floatv = floatv + doublev;
        // 明示的な cast が必要
        floatv = (float) (floatv + 4.0);
        floatv = (float) (floatv + doublev);
        floatv = floatv + (float) 4.0;
        floatv = floatv + (float) doublev;
        // 代入演算子だと compile ok 
        floatv += 4.0;
        assertThat(floatv).isEqualTo(22.0f);

        floatv = 2.0f;
        longv = 1;
        // double or float に up cast されるため compile error
        //longv = longv + 2.0;
        //longv = longv + floatv;
        // 明示的な cast が必要
        longv = (long) (longv + 2.0);
        longv = (long) (longv + floatv);
        longv = longv + (long) 2.0;
        longv = longv + (long) floatv;
        // 代入演算子だと compile ok 
        longv += floatv;
        assertThat(longv).isEqualTo(11);

        intv = 1;
        // double/float/long に up cast されるため compile error
        //intv = intv + 2.0;
        //intv = intv + 3.0f;
        //intv = intv + 4L;
        // 明示的な cast が必要
        intv = (int) (intv + 2.0);
        intv = (int) (intv + 3.0f);
        intv = (int) (intv + 4L);
        // 代入演算子だと compile ok 
        intv += 2.0;
        intv += 3.0f;
        intv += 4L;
        assertThat(intv).isEqualTo(19);

        shortv = 1;
        bytev = 1;
        // double/float/long/int に up cast されるため compile error
        //shortv = shortv + 2.0;
        //shortv = shortv + 3.0f;
        //shortv = shortv + 4L;
        //shortv = shortv + 1;
        //bytev = bytev + 2.0;
        //bytev = bytev + 3.0f;
        //bytev = bytev + 4L;
        //bytev = bytev + 1;
        shortv = (short) (shortv + 2.0);
        shortv = (short) (shortv + 3.0f);
        shortv = (short) (shortv + 4L);
        shortv = (short) (shortv + 1);
        bytev = (byte) (bytev + 2.0);
        bytev = (byte) (bytev + 3.0f);
        bytev = (byte) (bytev + 4L);
        bytev = (byte) (bytev + 1);
        assertThat(shortv).isEqualTo((short) 11);
        assertThat(bytev).isEqualTo((byte) 11);
        // そのままだと Short/Byte <> Integer インスタンスの比較となってしまった。
        assertThat(shortv).isNotEqualTo(11);
        assertThat(bytev).isNotEqualTo(11);
    }

    static class C1 {
    }

    static class C2 extends C1 {
    }

    static class C3 extends C2 {
    }

    static class D4 {
    }

    static abstract class D5 {
    }

    @Test
    public void testReferencialTypeAndInstanceOfDemo() {
        final Object o0 = new Object();
        final C1 o1 = new C1();
        final C2 o2 = new C2();
        final C3 o3 = new C3();
        final D4 o4 = new D4();

        // compile ok.
        final C2 o2x = o3;
        final C1 o1xa = o3;
        final C1 o1xb = o2x;
        final Object o0x = o1;

        final C2 o2y = o3;
        final Object o0y = o3;
        //final C3 o3ya = o2y; // compile error
        //final C3 o3yb = o0y; // compile error 
        final C3 o3ya = (C3) o2y;
        final C3 o3yb = (C3) o0y;

        assertThatThrownBy(() -> {
            final C3 o3z = (C3) o2;
        }).isInstanceOf(ClassCastException.class);

        // instanceof の右辺がインスタンスは compile error
        //assertThat(o1 instanceof o1).isTrue();
        // instanceof の左辺がクラスは compiile error
        //assertThat(C1 instanceof C1).isTrue();
        assertThat(o1 instanceof C1).isTrue();
        assertThat(o1 instanceof C2).isFalse();
        assertThat(o1 instanceof C3).isFalse();
        assertThat(o2 instanceof C1).isTrue();
        assertThat(o2 instanceof C2).isTrue();
        assertThat(o2 instanceof C3).isFalse();
        assertThat(o3 instanceof C1).isTrue();
        assertThat(o3 instanceof C2).isTrue();
        assertThat(o3 instanceof C3).isTrue();
        // >#>POINT<#< : 継承関係の無いクラス(abstract含む)を指定すると compile error
        //assertThat(o3 instanceof D4).isFalse();
        //assertThat(o3 instanceof D5).isFalse();
        // >#>POINT<#< : "interface" は継承関係が無くても compile ok
        assertThat(o3 instanceof List).isFalse();
    }

    String m1(int i) {
        return "int:" + i;
    }

    String m1(Integer i) {
        return "Integer:" + i.toString();
    }

    @Test
    public void testAutoBoxingDemo() {
        final int intv1 = 10;
        assertThat(m1(intv1)).isEqualTo("int:10");
        final Integer intv2 = intv1; // auto boxing
        assertThat(m1(intv2)).isEqualTo("Integer:10");
        final int intv3 = intv2; // auto boxing
        assertThat(m1(intv3)).isEqualTo("int:10");

        final Integer intv4 = 10;
        // >#>POINT<#<: 参照型数値へのリテラル代入では暗黙的な型変換が発生しない。
        // -> 以下は compile error
        //final Long longv1 = 20;
        //final Float floatv1 = 3.14;
        //final Double doublev1 = 40;
        // -> リテラルできちんと参照型に揃えれば compile ok.
        final Long longv2 = 20L;
        final Float floatv2 = 3.14f;
        final Double doublev1 = 40.0;
    }
}
