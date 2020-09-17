package javasnack.ojcp.se8gold;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class Test08SerializeInputOutputs {
    /* 参考:
     * - https://docs.oracle.com/javase/jp/8/docs/api/java/io/Serializable.html
     * - https://docs.oracle.com/javase/jp/8/docs/technotes/guides/serialization/index.html
     * - https://www.baeldung.com/java-serialization
     */

    static class SerDemo1 implements Serializable {
        private static final long serialVersionUID = 518631047790958304L;

        // NOTE: static変数はserialize対象外
        static int STATIC_NUM = 100;
        static String STATIC_STR = "xxx";

        private int i1;
        // NOTE: transient修飾子が指定されたインスタンス変数はserialize対象外
        transient private int i2;
        private String s1;
        transient private String s2;
        // NOTE: array or collection の場合、その中身も serialize 可能であること
        private int[] ints;
        private String[] strings;

        public SerDemo1(int i1, int i2, String s1, String s2, int[] ints, String[] strings) {
            this.i1 = i1;
            this.i2 = i2;
            this.s1 = s1;
            this.s2 = s2;
            this.ints = ints;
            this.strings = strings;
        }
    }

    @TempDir
    Path tempDir;

    @Test
    public void testBasicSerializeDemo() throws IOException, ClassNotFoundException {
        File dir = tempDir.toFile();
        File f1 = new File(dir, "data1.bin");
        SerDemo1 o1 = new SerDemo1(10, 20, "hello", "world", new int[] { 1, 2, 3 }, new String[] { "xx", "yy" });
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f1));
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f1))) {
            oos.writeObject(o1);
            oos.flush();
            // この間に static 変数を変更してみる。
            SerDemo1.STATIC_NUM = 999;
            SerDemo1.STATIC_STR = "yyy";
            SerDemo1 o2 = (SerDemo1) ois.readObject();
            // static 変数はserializeデータから復元されることはない(対象外)
            assertThat(SerDemo1.STATIC_NUM).isEqualTo(999);
            assertThat(SerDemo1.STATIC_STR).isEqualTo("yyy");
            // instance 変数については transient 以外は復元される。
            // transient フィールドについては、未初期化状態の instance field と同じ状態になる。
            assertThat(o2.i1).isEqualTo(10);
            assertThat(o2.i2).isEqualTo(0);
            assertThat(o2.s1).isEqualTo("hello");
            assertThat(o2.s2).isNull();
            assertThat(o2.ints).isEqualTo(new int[] { 1, 2, 3 });
            assertThat(o2.strings).isEqualTo(new String[] { "xx", "yy" });
        }
    }

    // NOTE: final class にも Serializable interface を実装可能
    static final class SerDemo2 implements Serializable {
        private static final long serialVersionUID = 1L;
        private final int i1;
        private final String s1;

        public SerDemo2(int i1, String s1) {
            this.i1 = i1;
            this.s1 = s1;
        }
    }

    @Test
    public void testSerializeFinalFieldDemo() throws IOException, ClassNotFoundException {
        File dir = tempDir.toFile();
        File f1 = new File(dir, "data1.bin");
        SerDemo2 o1 = new SerDemo2(10, "hello");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f1));
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f1))) {
            oos.writeObject(o1);
            oos.flush();
            SerDemo2 o2 = (SerDemo2) ois.readObject();
            // private final フィールドについてもserializeデータから復元できる。
            assertThat(o2.i1).isEqualTo(10);
            assertThat(o2.s1).isEqualTo("hello");
        }
    }

    static StringBuilder LOG = new StringBuilder();

    // NOTE: abstract class に Serializable interface を実装可能。
    static abstract class SerDemo3a implements Serializable {
        private static final long serialVersionUID = 1L;

        SerDemo3a() {
            LOG.append("demo3a");
        }
    }

    // NOTE: super class が serializable であれば、その派生クラスも serializable となる。
    static class SerDemo3b extends SerDemo3a {
        private static final long serialVersionUID = 1L;

        SerDemo3b() {
            LOG.append("demo3b");
        }
    }

    @Test
    public void testSerializeAndConstructorCallOrderDemo1() throws IOException, ClassNotFoundException {
        File dir = tempDir.toFile();
        File f1 = new File(dir, "data1.bin");
        LOG = new StringBuilder();

        SerDemo3b o1 = new SerDemo3b();
        assertThat(LOG.toString()).isEqualTo("demo3ademo3b");
        LOG = new StringBuilder();

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f1));
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f1))) {
            oos.writeObject(o1);
            oos.flush();
            ois.readObject();
            /* NOTE: super class で Serializable を実装している場合は、
             * deserialize するときに super class の constructor は呼ばれない。
             * (そのobject自身の constructor も呼ばれない)
             */
            assertThat(LOG.toString()).isEqualTo("");
        }
    }

    static class SerDemo4a {
        SerDemo4a() {
            LOG.append("demo4a");
        }
    }

    static class SerDemo4b extends SerDemo4a implements Serializable {
        private static final long serialVersionUID = 1L;

        SerDemo4b() {
            LOG.append("demo4b");
        }
    }

    @Test
    public void testSerializeAndConstructorCallOrderDemo2() throws IOException, ClassNotFoundException {
        File dir = tempDir.toFile();
        File f1 = new File(dir, "data1.bin");
        LOG = new StringBuilder();

        SerDemo4b o1 = new SerDemo4b();
        assertThat(LOG.toString()).isEqualTo("demo4ademo4b");
        LOG = new StringBuilder();

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f1));
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f1))) {
            oos.writeObject(o1);
            oos.flush();
            ois.readObject();
            /* NOTE: super class ではなく sub class で Serializable を実装している場合は、
             * deserialize するときに super class の引数を取らない constructor が呼ばれる。
             * (そのobject自身の constructor は呼ばれない)
             */
            assertThat(LOG.toString()).isEqualTo("demo4a");
        }
    }
}
