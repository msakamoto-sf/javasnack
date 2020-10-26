package javasnack.ojcp.se8gold.chapter12;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

import org.junit.jupiter.api.Test;

public class Test12ResourceBundleDemo {
    @Test
    public void testListResourceBundleDemo() {
        final String myres = "javasnack.ojcp.se8gold.chapter12.MyResources";
        final ResourceBundle rb1 = ResourceBundle.getBundle(myres, Locale.JAPANESE);
        assertThat(rb1.getString("send")).isEqualTo("送信");
        assertThat(rb1.getString("cancel")).isEqualTo("取消");
        final ResourceBundle rb2 = ResourceBundle.getBundle(myres, Locale.ENGLISH);
        assertThat(rb2.getString("send")).isEqualTo("send");
        assertThat(rb2.getString("cancel")).isEqualTo("cancel");

        // "_xx", "_xx_YY" 部分を指定してロードすることもできる。
        final String myresx = "javasnack.ojcp.se8gold.chapter12.MyResources_xx";
        final ResourceBundle rbx = ResourceBundle.getBundle(myresx);
        assertThat(rbx.getString("send")).isEqualTo("sendx");
        assertThat(rbx.getString("cancel")).isEqualTo("cancelx");
        final String myresxy = "javasnack.ojcp.se8gold.chapter12.MyResources_xx_YY";
        final ResourceBundle rbxy = ResourceBundle.getBundle(myresxy);
        assertThat(rbxy.getString("send")).isEqualTo("sendxy");
        assertThat(rbxy.getString("cancel")).isEqualTo("cancelxy");
    }

    @Test
    public void testPropertyResourceBundleDemo() {
        final String myres = "javasnack.ojcp.se8gold.chapter12.MyResourcesP";
        final ResourceBundle rb1 = ResourceBundle.getBundle(myres, Locale.JAPANESE);
        assertThat(rb1.getString("send")).isEqualTo("P_送信");
        assertThat(rb1.getString("cancel")).isEqualTo("P_取消");
        final ResourceBundle rb2 = ResourceBundle.getBundle(myres, Locale.ENGLISH);
        assertThat(rb2.getString("send")).isEqualTo("P_send");
        assertThat(rb2.getString("cancel")).isEqualTo("P_cancel");

        // "_xx", "_xx_YY" 部分を指定してロードすることもできる。
        final String myresx = "javasnack.ojcp.se8gold.chapter12.MyResourcesP_xx";
        final ResourceBundle rbx = ResourceBundle.getBundle(myresx);
        assertThat(rbx.getString("send")).isEqualTo("P_sendx");
        assertThat(rbx.getString("cancel")).isEqualTo("P_cancelx");
        final String myresxy = "javasnack.ojcp.se8gold.chapter12.MyResourcesP_xx_YY";
        final ResourceBundle rbxy = ResourceBundle.getBundle(myresxy);
        assertThat(rbxy.getString("send")).isEqualTo("P_sendxy");
        assertThat(rbxy.getString("cancel")).isEqualTo("P_cancelxy");
    }

    @Test
    public void testResourceBundleKeyOpsAndGetValuesDemo() {
        final String myres = "javasnack.ojcp.se8gold.chapter12.MyResources2";
        final ResourceBundle rb1 = ResourceBundle.getBundle(myres);

        assertThat(rb1.containsKey("str1")).isTrue();
        assertThat(rb1.containsKey("strx")).isFalse();

        System.out.println("--- see console log ---");
        for (String k : rb1.keySet()) { // stream()/keys() などは無く、keySet() のみ。
            System.out.println(k);
        }

        final Integer int1 = (Integer) rb1.getObject("int1");
        assertThat(int1).isEqualTo(500);
        final Long long1 = (Long) rb1.getObject("long1");
        assertThat(long1).isEqualTo(10_000L);
        final int[] ints1 = (int[]) rb1.getObject("ints");
        assertThat(ints1).isEqualTo(new int[] { 10, 20, 30 });
        assertThatThrownBy(() -> {
            rb1.getObject("xxxx");
        }).isInstanceOf(MissingResourceException.class);

        assertThat(rb1.getString("str1")).isEqualTo("abcd");
        assertThatThrownBy(() -> {
            rb1.getString("strx");
        }).isInstanceOf(MissingResourceException.class);

        /* { "strings1", "AA", "BB", "CC" }
         * -> getStringArray() として読み出そうとすると ClassCastException 発生。
         * 単純に getString() としてなら読み出せるが、最初の "AA" のみ返される。
         */
        assertThatThrownBy(() -> {
            rb1.getStringArray("strings1");
        }).isInstanceOf(ClassCastException.class);
        assertThat(rb1.getString("strings1")).isEqualTo("AA");

        final String[] strings2 = (String[]) rb1.getObject("strings2");
        assertThat(strings2).isEqualTo(new String[] { "aa", "bb", "cc" });
        // ↑と同等
        assertThat(rb1.getStringArray("strings2")).isEqualTo(new String[] { "aa", "bb", "cc" });
    }

    @Test
    public void testResourceBundleWithPropertiesDemo() {
        final String myres = "javasnack.ojcp.se8gold.chapter12.MyResources";
        final ResourceBundle rb0 = ResourceBundle.getBundle(myres, Locale.ENGLISH);
        final Properties p0 = new Properties();
        rb0.keySet().stream().forEach(k -> p0.put(k, rb0.getString(k)));

        assertThat(p0.get("send")).isEqualTo("send");
        assertThat(p0.getProperty("send")).isEqualTo("send");
        assertThat(p0.get("xxx")).isNull();
        assertThat(p0.getProperty("xxx")).isNull();
        assertThat(p0.getProperty("xxx", "default")).isEqualTo("default");
    }

    @Test
    public void testResourceBundleSearchPriorityDemo() {
        final String myres = "javasnack.ojcp.se8gold.chapter12.MyResources3";
        // lang/region 完全マッチ & class base
        ResourceBundle rb0 = ResourceBundle.getBundle(myres, new Locale("z1", "ZA"));
        assertThat(rb0.getString("kx")).isEqualTo("z1-ZA");
        assertThat(rb0.getString("kx0")).isEqualTo("00-z1-ZA");
        // lang/region 完全マッチ & property file base
        rb0 = ResourceBundle.getBundle(myres, new Locale("z1", "ZB"));
        assertThat(rb0.getString("kx")).isEqualTo("z1-ZB");
        // 見つからないときは locale 無しのリソースにfallback
        assertThat(rb0.getString("kx0")).isEqualTo("00-base");

        // lang 部分マッチ & class base
        rb0 = ResourceBundle.getBundle(myres, new Locale("z2", "ZX"));
        assertThat(rb0.getString("kx")).isEqualTo("z2-class");
        // lang 部分マッチ & property file base
        rb0 = ResourceBundle.getBundle(myres, new Locale("z3", "ZX"));
        assertThat(rb0.getString("kx")).isEqualTo("z3-prop");

        // default locale fallback & class base
        rb0 = ResourceBundle.getBundle(myres, new Locale("z4", "ZX"));
        assertThat(rb0.getString("kx")).isEqualTo("zz-class");

        final String myres2 = "javasnack.ojcp.se8gold.chapter12.MyResources4";
        // default locale fallback & property file base
        rb0 = ResourceBundle.getBundle(myres2, new Locale("z4", "ZX"));
        assertThat(rb0.getString("kx")).isEqualTo("zz-prop");

        final String myres3 = "javasnack.ojcp.se8gold.chapter12.MyResources5";
        // 指定した Locale にピッタリ一致するものが無く、fallback 対象も存在しない場合
        assertThatThrownBy(() -> {
            ResourceBundle.getBundle(myres3, new Locale("zx", "ZX"));
        }).isInstanceOf(MissingResourceException.class);
    }

}
