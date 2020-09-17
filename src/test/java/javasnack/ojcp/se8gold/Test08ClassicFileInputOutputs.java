package javasnack.ojcp.se8gold;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Formatter;
import java.util.FormatterClosedException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javasnack.myutils.HexDumper;

public class Test08ClassicFileInputOutputs {
    @TempDir
    Path tempDir;

    static String hex(final String src) {
        HexDumper hex = new HexDumper();
        hex.setPrefix("0x");
        hex.setSeparator(" ");
        return "<" + hex.dump(src.getBytes(StandardCharsets.ISO_8859_1)) + ">,[" + src + "]";
    }

    @Test
    public void testFileRelatedSystemProperties() {
        System.out.println("---- dump file related system properties:");
        System.out.println("line.separator=" + hex(System.getProperty("line.separator")));
        System.out.println("file.separator=" + hex(System.getProperty("file.separator")));
        System.out.println("path.separator=" + hex(System.getProperty("path.separator")));
        System.out.println("System.lineSeparator()=" + hex(System.lineSeparator()));
        System.out.println("File.pathSeparator=" + hex(File.pathSeparator));
        System.out.println("File.separator=" + hex(File.separator));
        System.out.println();
    }

    @Test
    public void testFileSystemInfos() {
        System.out.println("\nFile.listRoots(), get{Total|Free|Usable}Space() demo");
        File[] roots = File.listRoots();
        for (File f : roots) {
            String fs = f.getAbsolutePath();
            System.out.println("[" + fs + "].getTotalSpace()=" + f.getTotalSpace());
            /* NOTE: free/usableいずれも手がかりであり、確実な値を保証するものではない。
             * usableの方がOS等の制限を可能であればチェックするので確度は高い。
             */
            System.out.println("[" + fs + "].getFreeSpace()=" + f.getFreeSpace());
            System.out.println("[" + fs + "].getUsableSpace()=" + f.getUsableSpace());
        }
    }

    @Test
    public void testClassicFileApiUsageDemo() throws IOException {
        System.out.println("tempDir=[" + tempDir + "]");

        // File(String), exists(), createNewFile(), delete(), isFile(), isDirectory() demo
        File f0 = new File(tempDir.toString() + "/demo1.txt");
        assertThat(f0.exists()).isFalse();
        assertThat(f0.createNewFile()).isTrue(); // atomic file creation
        assertThat(f0.exists()).isTrue();
        assertThat(f0.isFile()).isTrue(); // regular file
        assertThat(f0.isDirectory()).isFalse();
        // rename() demo
        File f1 = new File("demo2.txt");
        assertThat(f1.exists()).isFalse();
        assertThat(f0.renameTo(f1)).isTrue();
        assertThat(f0.exists()).isFalse();
        assertThat(f1.exists()).isTrue();
        // delete() demo
        assertThat(f1.delete()).isTrue();
        assertThat(f1.exists()).isFalse();

        // パス名取得(full path)
        f0 = new File(tempDir.toString() + "/demo1.txt");
        assertThat(f0.isAbsolute()).isTrue();
        assertThat(f0.getName()).isEqualTo("demo1.txt");
        assertThat(f0.getParent()).isEqualTo(tempDir.toString());
        String s0 = tempDir.toString() + File.separator + "demo1.txt";
        assertThat(f0.getPath()).isEqualTo(s0);
        assertThat(f0.getAbsolutePath()).isEqualTo(s0);
        assertThat(f0.getCanonicalPath()).isEqualTo(s0);
        // return File instance
        assertThat(f0.getParentFile().toString()).isEqualTo(tempDir.toString());
        assertThat(f0.getAbsoluteFile().toString()).isEqualTo(s0);
        assertThat(f0.getCanonicalFile().toString()).isEqualTo(s0);

        // パス名取得(ファイル名のみ)
        f0 = new File("demo2.txt");
        assertThat(f0.isAbsolute()).isFalse();
        assertThat(f0.getName()).isEqualTo("demo2.txt");
        assertThat(f0.getParent()).isNull(); // 親ディレクトリが含まれていないためnull
        assertThat(f0.getPath()).isEqualTo("demo2.txt");
        // new File() で親ディレクトリを未指定のため、現在ディレクトリからの相対パスとして解決される。
        assertThat(f0.getAbsolutePath()).isNotEqualTo("demo2.txt").contains("demo2.txt");
        System.out.println("new File(\"demo2.txt\").getAbsolutePath()=" + f0.getAbsolutePath());
        assertThat(f0.getCanonicalPath()).isNotEqualTo("demo2.txt").contains("demo2.txt");
        System.out.println("new File(\"demo2.txt\").getCanonicalPath()=" + f0.getCanonicalPath());

        // パス名取得(相対パス)
        f0 = new File("../../demo3.txt");
        assertThat(f0.isAbsolute()).isFalse();
        assertThat(f0.getName()).isEqualTo("demo3.txt");
        // 親ディレクトリが相対パス部分になる。またパス区切りはプラットフォーム固有の文字に置換される。
        assertThat(f0.getParent()).isEqualTo(".." + File.separator + "..");
        // パス区切りはプラットフォーム固有の文字に置換される。
        assertThat(f0.getPath()).isEqualTo(".." + File.separator + ".." + File.separator + "demo3.txt");
        // new File() で親ディレクトリを未指定のため、現在ディレクトリからの相対パスとして解決される。
        assertThat(f0.getAbsolutePath()).isNotEqualTo("demo3.txt").contains("demo3.txt");
        System.out.println("new File(\"../../demo3.txt\").getAbsolutePath()=" + f0.getAbsolutePath());
        assertThat(f0.getCanonicalPath()).isNotEqualTo("demo3.txt").contains("demo3.txt");
        System.out.println("new File(\"../../demo3.txt\").getCanonicalPath()=" + f0.getCanonicalPath());

        // equals() demo
        f0 = new File(tempDir.toString() + "/demo1.txt");
        f1 = new File(tempDir.toString() + "/demo1.txt");
        assertThat(f0.equals(f1)).isTrue();
        // 絶対パスで同じ場所を指していても、元の表現が相対パスで異なれば equals() は false
        f0 = new File(tempDir.toString() + "/dir1/demo1.txt");
        f1 = new File(tempDir.toString() + "/dir1/../dir1/demo1.txt");
        assertThat(f0.equals(f1)).isFalse();
        // -> 最終的な絶対パスで比較したいときは File#getAbsolute{Path|File}() で比較した方が良さそう。

        // File(String, String), mkdir() demo
        f0 = new File(tempDir.toString(), "dir1");
        assertThat(f0.mkdir()).isTrue();
        assertThat(f0.exists()).isTrue();
        assertThat(f0.isFile()).isFalse();
        assertThat(f0.isDirectory()).isTrue();

        // File(File, String), mkdirs() demo
        f1 = new File(f0, "dir2/dir3");
        assertThat(f1.mkdir()).isFalse(); // dir2 はまだ無いので dir3 のmkdir()は失敗
        assertThat(f1.mkdirs()).isTrue(); // dir2, dir3 と順に作成
        assertThat(f1.isDirectory()).isTrue();

        // ディレクトリの delete() は中身が空でないと false を返すデモ
        assertThat(f1.delete()).isTrue(); // -> 削除されるのは dir3
        assertThat(f0.delete()).isFalse(); // dir1 にはまだ dir2 が残ってるので false を返す。
        f1 = new File(f0, "dir2");
        assertThat(f1.delete()).isTrue(); // dir2 も削除
        assertThat(f0.delete()).isTrue(); // これで dir1 が空になるので、削除成功で true となる。

        // list(), listFiles() demo
        f0 = new File(tempDir.toString() + "/demo1.txt");
        assertThat(f0.createNewFile()).isTrue();
        f0 = new File(tempDir.toString() + "/demo2.txt");
        assertThat(f0.createNewFile()).isTrue();
        f0 = new File(tempDir.toString() + "/dir1/dir1a/dir1b");
        assertThat(f0.mkdirs()).isTrue();
        f0 = new File(tempDir.toString() + "/dir2/dir2a/dir2b");
        assertThat(f0.mkdirs()).isTrue();
        f0 = new File(tempDir.toString());
        assertThat(f0.isDirectory()).isTrue();
        List<String> entries = Arrays.asList(f0.list());
        assertThat(entries.size()).isEqualTo(4);
        assertThat(entries.contains("demo1.txt")).isTrue();
        assertThat(entries.contains("demo2.txt")).isTrue();
        // 直下のディレクトリ名のみ抽出する。再帰的には見に行かない。
        assertThat(entries.contains("dir1")).isTrue();
        assertThat(entries.contains("dir2")).isTrue();
        List<File> files = Arrays.asList(f0.listFiles());
        assertThat(files.size()).isEqualTo(4);
        assertThat(files.contains(new File(tempDir.toString() + "/demo1.txt"))).isTrue();
        assertThat(files.contains(new File(tempDir.toString() + "/demo2.txt"))).isTrue();
        assertThat(files.contains(new File(tempDir.toString() + "/dir1"))).isTrue();
        assertThat(files.contains(new File(tempDir.toString() + "/dir2"))).isTrue();

        f0 = new File(tempDir.toString() + "/demo1.txt");
        // execute/read/write の権限更新については OSとfilesystemによってサポートが変わる。
        // assertThat(f0.setExecutable(false)).isTrue(); 
        // assertThat(f0.setReadable(false)).isTrue();
        // assertThat(f0.setReadOnly()).isTrue();
        // assertThat(f0.setWritable(false)).isTrue();
        // assertThat(f0.canExecute()).isFalse();
        // 通常ファイルであれば、どのプラットフォームでもread/writeのgetはサポート。
        assertThat(f0.canRead()).isTrue();
        assertThat(f0.canWrite()).isTrue();
        // 2020年の unix/win 最新系であれば、last-modified のget/setはサポートしてそう。
        assertThat(f0.setLastModified(0L)).isTrue();
        assertThat(f0.lastModified()).isEqualTo(0L);

        // File.createTempFile() demo
        f0 = File.createTempFile("prefix0", "suffix0", tempDir.toFile());
        System.out.println("File.createTempFile(prefix, suffix, directory) : " + f0.toString());
        assertThat(f0.delete()).isTrue();
        f0 = File.createTempFile("prefix0", "suffix0");
        System.out.println("File.createTempFile(prefix, suffix) : " + f0.toString());
        assertThat(f0.delete()).isTrue();
    }

    void assertFisReadBytes(final File file, final byte[] expectedBytes) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] bytes = new byte[expectedBytes.length];
            assertThat(fis.read(bytes)).isEqualTo(expectedBytes.length);
            assertThat(bytes).isEqualTo(expectedBytes);
        }
    }

    @Test
    public void testFileInputOutputStreamDemo() throws IOException {
        File dir = tempDir.toFile();
        File f1 = new File(dir, "data1.bin");

        try (FileOutputStream fos = new FileOutputStream(f1)) {
            fos.write(1);
            fos.write(2);
            fos.write(new byte[] { 3, 4, 5 });
            fos.flush();
        }

        assertThat(f1.length()).isEqualTo(5);

        try (FileInputStream fis = new FileInputStream(f1)) {
            assertThat(fis.read()).isEqualTo(1);
            byte[] bytes = new byte[5];
            assertThat(fis.read(bytes)).isEqualTo(4);
            assertThat(bytes).isEqualTo(new byte[] { 2, 3, 4, 5, 0 });
            assertThat(fis.read()).isEqualTo(-1); // EOF
        }

        // append = true (追記)
        try (FileOutputStream fos = new FileOutputStream(f1, true)) {
            fos.write(6);
            fos.write(7);
            fos.flush();
        }

        assertThat(f1.length()).isEqualTo(7);
        assertFisReadBytes(f1, new byte[] { 1, 2, 3, 4, 5, 6, 7 });

        // append = false (上書き)
        try (FileOutputStream fos = new FileOutputStream(f1, false)) {
            fos.write(8);
            fos.write(9);
            fos.flush();
        }

        assertThat(f1.length()).isEqualTo(2);
        assertFisReadBytes(f1, new byte[] { 8, 9 });

        // append 引数がない場合は先頭から上書きになる(append = false)
        try (FileOutputStream fos = new FileOutputStream(f1)) {
            fos.write(0);
            fos.write(1);
            fos.write(2);
            fos.flush();
        }

        assertThat(f1.length()).isEqualTo(3);
        assertFisReadBytes(f1, new byte[] { 0, 1, 2 });

        assertThatThrownBy(() -> {
            File fx = new File(dir, "datax.bin");
            try (FileInputStream fis = new FileInputStream(fx)) {
                fis.read();
            }
        }).isInstanceOf(FileNotFoundException.class);
    }

    @Test
    public void testDataInputOutputStreamDemo() throws IOException {
        File dir = tempDir.toFile();
        File f1 = new File(dir, "data1.bin");
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(f1));
                DataInputStream dis = new DataInputStream(new FileInputStream(f1))) {
            dos.write(128);
            dos.write(new byte[] { 1, 2, 3, 4, 5, 6 }, 2, 3);
            dos.writeByte(255);
            dos.writeByte(Byte.MIN_VALUE);
            dos.writeByte(Byte.MAX_VALUE);
            dos.writeShort(Short.MIN_VALUE);
            dos.writeShort(Short.MAX_VALUE);
            dos.writeInt(Integer.MIN_VALUE);
            dos.writeInt(Integer.MAX_VALUE);
            dos.writeLong(Long.MIN_VALUE);
            dos.writeLong(Long.MAX_VALUE);
            dos.writeFloat(3.14f);
            dos.writeDouble(3.14);
            dos.writeBoolean(true);
            dos.writeChar('A');
            dos.writeUTF("日本語");
            dos.flush();
            assertThat(dos.size()).isEqualTo(61);
            assertThat(dis.readByte()).isEqualTo((byte) -128); // write(128)
            byte[] bytes = new byte[3];
            dis.read(bytes);
            assertThat(bytes).isEqualTo(new byte[] { 3, 4, 5 });
            assertThat(dis.readByte()).isEqualTo((byte) -1); // write(255)
            assertThat(dis.readByte()).isEqualTo(Byte.MIN_VALUE);
            assertThat(dis.readByte()).isEqualTo(Byte.MAX_VALUE);
            assertThat(dis.readShort()).isEqualTo(Short.MIN_VALUE);
            assertThat(dis.readShort()).isEqualTo(Short.MAX_VALUE);
            assertThat(dis.readInt()).isEqualTo(Integer.MIN_VALUE);
            assertThat(dis.readInt()).isEqualTo(Integer.MAX_VALUE);
            assertThat(dis.readLong()).isEqualTo(Long.MIN_VALUE);
            assertThat(dis.readLong()).isEqualTo(Long.MAX_VALUE);
            assertThat(dis.readFloat()).isEqualTo(3.14f);
            assertThat(dis.readDouble()).isEqualTo(3.14);
            assertThat(dis.readBoolean()).isTrue();
            assertThat(dis.readChar()).isEqualTo('A');
            assertThat(dis.readUTF()).isEqualTo("日本語");
        }
    }

    @Test
    public void testFileReaderWriterDemo() throws IOException {
        File dir = tempDir.toFile();
        File f1 = new File(dir, "data1.bin");
        // 文字コードは指定できない。プラットフォームの文字コードが使用される。
        try (FileWriter fw = new FileWriter(f1); // append フラグも指定できる。
                FileReader fr = new FileReader(f1)) {
            fw.write('A');
            fw.write("日本語");
            fw.append('A');
            fw.append("日本語");
            fw.flush();
            assertThat(fr.read()).isEqualTo('A');
            assertThat(fr.read()).isEqualTo('日');
            assertThat(fr.read()).isEqualTo('本');
            assertThat(fr.read()).isEqualTo('語');
            assertThat(fr.read()).isEqualTo('A');
            assertThat(fr.read()).isEqualTo('日');
            assertThat(fr.read()).isEqualTo('本');
            assertThat(fr.read()).isEqualTo('語');
            assertThat(fr.read()).isEqualTo(-1); // EOF
        }
    }

    @Test
    public void testBufferedReaderWriterDemo() throws IOException {
        File dir = tempDir.toFile();
        File f1 = new File(dir, "data1.bin");
        // 文字コードは指定できない。プラットフォームの文字コードが使用される。
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(f1));
                BufferedReader br = new BufferedReader(new FileReader(f1))) {
            bw.write("日本語");
            bw.newLine();
            bw.append("あいうえお");
            bw.newLine();
            bw.flush();
            assertThat(br.readLine()).isEqualTo("日本語");
            assertThat(br.readLine()).isEqualTo("あいうえお");
            assertThat(br.readLine()).isNull(); // EOF
        }
    }

    @Test
    public void testMarkResetSkipDemo() throws IOException {
        File dir = tempDir.toFile();
        File f1 = new File(dir, "data1.bin");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(f1));
                BufferedReader br = new BufferedReader(new FileReader(f1))) {
            bw.write("abcdefghijklmnopqrstuvwxyz");
            bw.newLine();
            bw.write("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
            bw.newLine();
            bw.flush();
            assertThat(br.markSupported()).isTrue();
            assertThat(br.read()).isEqualTo('a');
            assertThat(br.read()).isEqualTo('b');
            br.mark(3); // "c" に mark, そのまま5文字までに制限
            assertThat(br.read()).isEqualTo('c');
            assertThat(br.read()).isEqualTo('d');
            assertThat(br.read()).isEqualTo('e');
            assertThat(br.read()).isEqualTo('f');
            assertThat(br.read()).isEqualTo('g');
            br.reset();
            assertThat(br.read()).isEqualTo('c');
            assertThat(br.read()).isEqualTo('d');
            assertThat(br.read()).isEqualTo('e');
            assertThat(br.read()).isEqualTo('f');
            assertThat(br.read()).isEqualTo('g');
            // mark で指定した制限を超えて read する。
            assertThat(br.read()).isEqualTo('h');
            br.reset();
            assertThat(br.read()).isEqualTo('c'); // 5文字を超えても reset() で戻れる。
            // mark で指定した制限を超えて read する。
            assertThat(br.readLine()).isEqualTo("defghijklmnopqrstuvwxyz");
            br.reset();
            assertThat(br.read()).isEqualTo('c'); // readLine() 後も reset() で戻れる。
            assertThat(br.readLine()).isEqualTo("defghijklmnopqrstuvwxyz");
            br.skip(3);
            assertThat(br.read()).isEqualTo('D'); // ABC の 3文字をskip
            assertThat(br.readLine()).isEqualTo("EFGHIJKLMNOPQRSTUVWXYZ");
            assertThat(br.readLine()).isNull(); // EOF
        }
        try (FileOutputStream fos = new FileOutputStream(f1)) {
            fos.write(new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 });
            fos.flush();
        }
        try (FileInputStream fis = new FileInputStream(f1)) {
            // FileInputStream では mark() は未サポート
            assertThat(fis.markSupported()).isFalse();
            assertThat(fis.read()).isEqualTo(1);
            assertThat(fis.read()).isEqualTo(2);
            fis.mark(2); // 未サポートで呼んでも副作用はない
            assertThat(fis.read()).isEqualTo(3);
            assertThat(fis.read()).isEqualTo(4);
            assertThat(fis.read()).isEqualTo(5);
            assertThatThrownBy(() -> {
                // reset() を呼ぼうとすると IOException
                fis.reset();
            }).isInstanceOf(IOException.class);
            fis.skip(2); // skip 6, 7
            assertThat(fis.read()).isEqualTo(8);
            assertThat(fis.read()).isEqualTo(9);
            assertThat(fis.read()).isEqualTo(-1); // EOF
        }
    }

    @Test
    public void testStreamToReaderWriterDemo() throws FileNotFoundException, IOException {
        File dir = tempDir.toFile();
        File f1 = new File(dir, "data1.bin");
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(f1),
                        StandardCharsets.UTF_8));
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(
                                new FileInputStream(f1),
                                StandardCharsets.UTF_8))) {
            bw.append("abc");
            bw.newLine();
            bw.write("日本語");
            bw.flush();
            assertThat(br.readLine()).isEqualTo("abc");
            assertThat(br.readLine()).isEqualTo("日本語");
            assertThat(br.readLine()).isNull(); // EOF
        }
    }

    @Test
    public void testPrintStreamAndWriterWithFormatter() throws IOException {
        File dir = tempDir.toFile();
        File f1 = new File(dir, "data1.bin");
        /* PrintStream() のコンストラクタは、OutputStream を渡す形式で明示的に autoFlush = true
         * を指定しない限りは自動でflushを行わない。
         * (= 自動flushを行いたければ、OutputStream を渡す + 明示的に第二引数 autoFlush = true 
         *  を指定するほか無い)
         * autoFlush = true にしても、println()系 または文字列書き込みで "\n" が書き込まれた場合のみflushされる。
         * (= write()/append() については autoFlush=true でも flush されない) 
         */
        try (PrintStream ps = new PrintStream(new FileOutputStream(f1), true);
                BufferedReader br = new BufferedReader(new FileReader(f1))) {
            ps.print(true);
            ps.print(100);
            ps.print(3.14);
            ps.print("日本語");
            assertThat(br.readLine()).isEqualTo("true1003.14日本語");
            ps.println(true);
            assertThat(br.readLine()).isEqualTo("true");
            ps.println(100);
            assertThat(br.readLine()).isEqualTo("100");
            ps.println(3.14);
            assertThat(br.readLine()).isEqualTo("3.14");
            ps.println("日本語");
            assertThat(br.readLine()).isEqualTo("日本語");
            ps.format("s=%s, d=%d", "あいう", 100);
            assertThat(br.readLine()).isEqualTo("s=あいう, d=100");
            ps.printf("d=%2$d, s=%1$s", "日本語", 200);
            assertThat(br.readLine()).isEqualTo("d=200, s=日本語");
            assertThat(br.readLine()).isNull(); // EOF
        }

        /* PrintWriter での autoFlush = true は、 println/printf/format 系でのみflushする。
         */
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(f1), true);
                BufferedReader br = new BufferedReader(new FileReader(f1))) {
            pw.print(true);
            pw.print(100);
            pw.print(3.14);
            pw.print("日本語");
            pw.flush(); // print() では自動 flush はされない。
            assertThat(br.readLine()).isEqualTo("true1003.14日本語");
            pw.println(true);
            assertThat(br.readLine()).isEqualTo("true");
            pw.println(100);
            assertThat(br.readLine()).isEqualTo("100");
            pw.println(3.14);
            assertThat(br.readLine()).isEqualTo("3.14");
            pw.println("日本語");
            assertThat(br.readLine()).isEqualTo("日本語");
            pw.format("s=%s, d=%d", "あいう", 100);
            assertThat(br.readLine()).isEqualTo("s=あいう, d=100");
            pw.printf("d=%2$d, s=%1$s", "日本語", 200);
            assertThat(br.readLine()).isEqualTo("d=200, s=日本語");
            assertThat(br.readLine()).isNull(); // EOF
        }

        // NOTE: 書式設定の詳細パターンは javasnack.langspecs.TestStringFormat 参照
        Formatter fmt1 = new Formatter();
        fmt1.format("str=%s, d=%d, %+010.3f %n", "abc", 100, 3.141592);
        assertThat(fmt1.toString()).isEqualTo("str=abc, d=100, +00003.142 " + System.lineSeparator());
        fmt1.close();
        assertThatThrownBy(() -> {
            fmt1.format("s=%s", "x");
        }).isInstanceOf(FormatterClosedException.class);

        Formatter fmt2 = new Formatter();
        fmt2.format("d=%2$d, %3$+010.3f, str=%1$s", "abc", 100, 3.141592);
        assertThat(fmt2.toString()).isEqualTo("d=100, +00003.142, str=abc");
        fmt2.close();
    }
}
