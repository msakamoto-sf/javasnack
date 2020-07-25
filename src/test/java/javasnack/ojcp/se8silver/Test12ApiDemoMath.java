package javasnack.ojcp.se8silver;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class Test12ApiDemoMath {

    @Test
    public void testMathDemos() {
        System.out.println("Math.E=" + Math.E);
        System.out.println("Math.PI=" + Math.PI);

        final int n1 = -20;
        final int n2 = 10;
        assertThat(Math.max(n1, n2)).isEqualTo(10);
        assertThat(Math.min(n1, n2)).isEqualTo(-20);
        assertThat(Math.abs(n1)).isEqualTo(20);

        assertThat(Math.sqrt(4.0)).isEqualTo(2.0); // 平方根
        assertThat(Math.cbrt(8.0)).isEqualTo(2.0); // 立方根
        assertThat(Math.log10(100.0)).isEqualTo(2.0); // log10
        assertThat(Math.pow(10.0, 2.0)).isEqualTo(100.0);

        assertThat(Math.ceil(9.4)).isEqualTo(10.0);
        assertThat(Math.ceil(9.5)).isEqualTo(10.0);
        assertThat(Math.ceil(9.6)).isEqualTo(10.0);
        assertThat(Math.ceil(9.9)).isEqualTo(10.0);
        assertThat(Math.ceil(10.0)).isEqualTo(10.0);
        assertThat(Math.ceil(10.1)).isEqualTo(11.0);
        assertThat(Math.ceil(10.4)).isEqualTo(11.0);
        assertThat(Math.ceil(10.5)).isEqualTo(11.0);
        assertThat(Math.ceil(10.6)).isEqualTo(11.0);
        assertThat(Math.ceil(-0.1)).isEqualTo(-0.0);
        assertThat(Math.ceil(-0.4)).isEqualTo(-0.0);
        assertThat(Math.ceil(-0.5)).isEqualTo(-0.0);
        assertThat(Math.ceil(-0.6)).isEqualTo(-0.0);
        assertThat(Math.ceil(-0.9)).isEqualTo(-0.0);
        assertThat(Math.ceil(-1.0)).isEqualTo(-1.0);
        assertThat(Math.ceil(-1.1)).isEqualTo(-1.0);
        assertThat(Math.ceil(-1.4)).isEqualTo(-1.0);
        assertThat(Math.ceil(-1.5)).isEqualTo(-1.0);
        assertThat(Math.ceil(-1.6)).isEqualTo(-1.0);
        assertThat(Math.ceil(-1.9)).isEqualTo(-1.0);

        assertThat(Math.floor(9.4)).isEqualTo(9.0);
        assertThat(Math.floor(9.5)).isEqualTo(9.0);
        assertThat(Math.floor(9.6)).isEqualTo(9.0);
        assertThat(Math.floor(9.9)).isEqualTo(9.0);
        assertThat(Math.floor(10.0)).isEqualTo(10.0);
        assertThat(Math.floor(10.1)).isEqualTo(10.0);
        assertThat(Math.floor(10.4)).isEqualTo(10.0);
        assertThat(Math.floor(10.5)).isEqualTo(10.0);
        assertThat(Math.floor(10.6)).isEqualTo(10.0);
        assertThat(Math.floor(-0.1)).isEqualTo(-1.0);
        assertThat(Math.floor(-0.4)).isEqualTo(-1.0);
        assertThat(Math.floor(-0.5)).isEqualTo(-1.0);
        assertThat(Math.floor(-0.6)).isEqualTo(-1.0);
        assertThat(Math.floor(-0.9)).isEqualTo(-1.0);
        assertThat(Math.floor(-1.0)).isEqualTo(-1.0);
        assertThat(Math.floor(-1.1)).isEqualTo(-2.0);
        assertThat(Math.floor(-1.4)).isEqualTo(-2.0);
        assertThat(Math.floor(-1.5)).isEqualTo(-2.0);
        assertThat(Math.floor(-1.6)).isEqualTo(-2.0);
        assertThat(Math.floor(-1.9)).isEqualTo(-2.0);

        assertThat(Math.rint(9.4)).isEqualTo(9.0);
        assertThat(Math.rint(9.5)).isEqualTo(10.0);
        assertThat(Math.rint(9.6)).isEqualTo(10.0);
        assertThat(Math.rint(9.9)).isEqualTo(10.0);
        assertThat(Math.rint(10.0)).isEqualTo(10.0);
        assertThat(Math.rint(10.1)).isEqualTo(10.0);
        assertThat(Math.rint(10.4)).isEqualTo(10.0);
        assertThat(Math.rint(10.5)).isEqualTo(10.0); // 9.5 と動きが違う
        assertThat(Math.rint(10.6)).isEqualTo(11.0);
        assertThat(Math.rint(-0.1)).isEqualTo(-0.0);
        assertThat(Math.rint(-0.4)).isEqualTo(-0.0);
        assertThat(Math.rint(-0.5)).isEqualTo(-0.0);
        assertThat(Math.rint(-0.6)).isEqualTo(-1.0);
        assertThat(Math.rint(-0.9)).isEqualTo(-1.0);
        assertThat(Math.rint(-1.0)).isEqualTo(-1.0);
        assertThat(Math.rint(-1.1)).isEqualTo(-1.0);
        assertThat(Math.rint(-1.4)).isEqualTo(-1.0);
        assertThat(Math.rint(-1.5)).isEqualTo(-2.0); // -0.5 と動きが違う
        assertThat(Math.rint(-1.6)).isEqualTo(-2.0);
        assertThat(Math.rint(-1.9)).isEqualTo(-2.0);

        assertThat(Math.round(9.4)).isEqualTo(9);
        assertThat(Math.round(9.5)).isEqualTo(10);
        assertThat(Math.round(9.6)).isEqualTo(10);
        assertThat(Math.round(9.9)).isEqualTo(10);
        assertThat(Math.round(10.0)).isEqualTo(10);
        assertThat(Math.round(10.1)).isEqualTo(10);
        assertThat(Math.round(10.4)).isEqualTo(10);
        assertThat(Math.round(10.5)).isEqualTo(11); // こちらは 9.5 と動きが同じ
        assertThat(Math.round(10.6)).isEqualTo(11);
        assertThat(Math.round(-0.1)).isEqualTo(-0);
        assertThat(Math.round(-0.4)).isEqualTo(-0);
        assertThat(Math.round(-0.5)).isEqualTo(-0);
        assertThat(Math.round(-0.6)).isEqualTo(-1);
        assertThat(Math.round(-0.9)).isEqualTo(-1);
        assertThat(Math.round(-1.0)).isEqualTo(-1);
        assertThat(Math.round(-1.1)).isEqualTo(-1);
        assertThat(Math.round(-1.4)).isEqualTo(-1);
        assertThat(Math.round(-1.5)).isEqualTo(-1); // こちらは -0.5 と動きが同じ
        assertThat(Math.round(-1.6)).isEqualTo(-2);
        assertThat(Math.round(-1.9)).isEqualTo(-2);
    }
}
