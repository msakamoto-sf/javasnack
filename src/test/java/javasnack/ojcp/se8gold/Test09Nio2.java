package javasnack.ojcp.se8gold;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.io.TempDir;

public class Test09Nio2 {
    @TempDir
    Path tempDir;

    static final String FS = File.separator;

    static String p(String... elements) {
        return Arrays.stream(elements).collect(Collectors.joining(FS));
    }

    static String p2(String... elements) {
        return FS + Arrays.stream(elements).collect(Collectors.joining(FS));
    }

    @Test
    public void testPathsGetDemo() throws IOException, URISyntaxException {
        System.out.println("tempDir=[" + tempDir + "]");

        // Paths.get() demo

        Path p0 = Paths.get("foo/bar/baz.txt");
        assertThat(p0.toString()).isEqualTo(p("foo", "bar", "baz.txt"));
        assertThat(p0.getFileName().toString()).isEqualTo("baz.txt");
        assertThat(p0.getNameCount()).isEqualTo(3);
        assertThat(p0.getName(0).toString()).isEqualTo("foo");
        assertThat(p0.getName(1).toString()).isEqualTo("bar");
        assertThat(p0.getName(2).toString()).isEqualTo("baz.txt");
        assertThat(p0.subpath(0, 2).toString()).isEqualTo(p("foo", "bar")); // start 含み, end は含まない
        assertThat(p0.getParent().toString()).isEqualTo(p("foo", "bar"));
        assertThat(p0.getRoot()).isNull();
        assertThat(p0.isAbsolute()).isFalse();
        assertThat(p0.startsWith("f")).isFalse(); // path 要素単位で判定される。
        assertThat(p0.startsWith("foo")).isTrue();
        assertThat(p0.endsWith("baz.txt")).isTrue();
        assertThat(p0.endsWith("t")).isFalse(); // path 要素単位で判定される。
        Iterator<Path> ip0 = p0.iterator();
        assertThat(ip0.next().toString()).isEqualTo("foo");
        assertThat(ip0.next().toString()).isEqualTo("bar");
        assertThat(ip0.next().toString()).isEqualTo("baz.txt");
        assertThat(ip0.hasNext()).isFalse();

        p0 = Paths.get("C:\\foo\\bar\\baz.txt");
        assertThat(p0.toString()).isEqualTo(p("C:", "foo", "bar", "baz.txt"));
        assertThat(p0.getFileName().toString()).isEqualTo("baz.txt");
        // Windows のドライブは getNameCount(), getName(N) に含まれない
        assertThat(p0.getNameCount()).isEqualTo(3);
        assertThat(p0.getName(0).toString()).isEqualTo("foo");
        assertThat(p0.getName(1).toString()).isEqualTo("bar");
        assertThat(p0.getName(2).toString()).isEqualTo("baz.txt");
        assertThat(p0.subpath(0, 2).toString()).isEqualTo(p("foo", "bar")); // start 含み, end は含まない
        // parent, root としてはドライブ要素が含まれる。
        assertThat(p0.getParent().toString()).isEqualTo(p("C:", "foo", "bar"));
        assertThat(p0.getRoot().toString()).isEqualTo("C:\\");
        assertThat(p0.isAbsolute()).isTrue();
        assertThat(p0.startsWith("C")).isFalse(); // path 要素単位で判定される。
        assertThat(p0.startsWith("C:\\")).isTrue();
        assertThat(p0.endsWith("baz.txt")).isTrue();
        assertThat(p0.endsWith("t")).isFalse(); // path 要素単位で判定される。
        ip0 = p0.iterator();
        assertThat(ip0.next().toString()).isEqualTo("foo");
        assertThat(ip0.next().toString()).isEqualTo("bar");
        assertThat(ip0.next().toString()).isEqualTo("baz.txt");
        assertThat(ip0.hasNext()).isFalse();

        p0 = Paths.get(new URI("file:///C:/foo/bar/baz.txt"));
        assertThat(p0.toString()).isEqualTo(p("C:", "foo", "bar", "baz.txt"));
        assertThat(p0.getFileName().toString()).isEqualTo("baz.txt");
        // Windows のドライブは getNameCount(), getName(N) に含まれない
        assertThat(p0.getNameCount()).isEqualTo(3);
        assertThat(p0.getName(0).toString()).isEqualTo("foo");
        assertThat(p0.getName(1).toString()).isEqualTo("bar");
        assertThat(p0.getName(2).toString()).isEqualTo("baz.txt");
        assertThat(p0.subpath(0, 2).toString()).isEqualTo(p("foo", "bar")); // start 含み, end は含まない
        // parent, root としてはドライブ要素が含まれる。
        assertThat(p0.getParent().toString()).isEqualTo(p("C:", "foo", "bar"));
        assertThat(p0.getRoot().toString()).isEqualTo("C:\\");
        assertThat(p0.isAbsolute()).isTrue();
        assertThat(p0.startsWith("C")).isFalse(); // path 要素単位で判定される。
        assertThat(p0.startsWith("C:\\")).isTrue();
        assertThat(p0.endsWith("baz.txt")).isTrue();
        assertThat(p0.endsWith("t")).isFalse(); // path 要素単位で判定される。
        ip0 = p0.iterator();
        assertThat(ip0.next().toString()).isEqualTo("foo");
        assertThat(ip0.next().toString()).isEqualTo("bar");
        assertThat(ip0.next().toString()).isEqualTo("baz.txt");
        assertThat(ip0.hasNext()).isFalse();

        p0 = Paths.get(tempDir.toString(), "foo/bar/baz.txt");
        assertThat(p0.toString()).isEqualTo(p(tempDir.toString(), "foo", "bar", "baz.txt"));
        assertThat(p0.isAbsolute()).isTrue();

        assertThatThrownBy(() -> {
            Paths.get("foo/bar/baz.txt").subpath(1, 1); // start = end
        }).isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> {
            Paths.get("foo/bar/baz.txt").subpath(2, 1); // start > end
        }).isInstanceOf(IllegalArgumentException.class);

        // FileSystem.getPath() demo
        FileSystem fs = FileSystems.getDefault();
        p0 = fs.getPath("foo/bar/baz.txt");
        assertThat(p0.toString()).isEqualTo(p("foo", "bar", "baz.txt"));
        p0 = fs.getPath("C:\\foo\\bar\\baz.txt");
        assertThat(p0.toString()).isEqualTo(p("C:", "foo", "bar", "baz.txt"));
        //p0 = fs.getPath(new URI("file:///C:/foo/bar/baz.txt")); // URI形式は未サポート
        p0 = fs.getPath(tempDir.toString(), "foo/bar/baz.txt");
        assertThat(p0.toString()).isEqualTo(p(tempDir.toString(), "foo", "bar", "baz.txt"));

        Path curr = Paths.get(".").toRealPath();
        System.out.println(curr.toUri());

        p0 = Paths.get("../../foo/./bar/../bar/baz.txt");
        // normalize() は冗長な表現を削除してくれる。
        assertThat(p0.normalize().toString()).isEqualTo(p("..", "..", "foo", "bar", "baz.txt"));
        // toUri() では冗長な表現や相対パスの調整はしてくれない。(as-is)
        assertThat(p0.toUri().toString().toLowerCase())
                .isEqualTo(curr.toUri().toString().toLowerCase() + "../../foo/./bar/../bar/baz.txt");
        // NOTE: drive-letter がJDKによって大文字・小文字が異なる現象が確認されたため、小文字に揃えて比較

        p0 = Paths.get("..").toRealPath();
        // 現在ディレクトリに対して1つ上になるので、subpath() での比較を実施。
        assertThat(p0.subpath(0, p0.getNameCount())).isEqualTo(curr.subpath(0, curr.getNameCount() - 1));
        System.out.println("Paths.get(\".\").toRealPath() = [" + curr + "]");
        System.out.println("Paths.get(\"..\").toRealPath() = [" + p0 + "]");

        // toRealPath() では実際にファイルシステム上での解決を試みるので、存在しないpathだとIOException
        assertThatThrownBy(() -> {
            Paths.get("./xxxxx").toRealPath();
        }).isInstanceOf(IOException.class);

        // toAbsolutePath() では最初の相対パスを文字列上で調整するだけなので、存在しないpathでも no error.
        p0 = Paths.get("./foo/../foo/./bar/baz.txt").toAbsolutePath();
        assertThat(p0.toString().toLowerCase())
                .isEqualTo(p(curr.toString().toLowerCase(), ".", "foo", "..", "foo", ".", "bar", "baz.txt"));
        // NOTE: drive-letter がJDKによって大文字・小文字が異なる現象が確認されたため、小文字に揃えて比較

        // resolve() では文字列上で結合するだけなので、存在しないpathでも no error.
        Path p1 = Paths.get("../foo/bar/");
        Path p2 = Paths.get("../bar/./baz.txt");
        // ., .. なども解決しない。
        assertThat(p1.resolve(p2).toString()).isEqualTo(p("..", "foo", "bar", "..", "bar", ".", "baz.txt"));
        // resolve対象が絶対パス形式の場合は、それをそのまま返す。
        Path p3 = Paths.get("/aaa/bbb/ccc");
        assertThat(p1.resolve(p3).toString()).isEqualTo(p2("aaa", "bbb", "ccc"));

        p1 = Paths.get("aaa/bbb/ccc/ddd/eee.txt");
        p2 = Paths.get("aaa/bbb/xxx/yyy/zzz.txt");
        // a.relativize(b) -> a から見た b の相対パスになる。
        assertThat(p1.relativize(p2).toString()).isEqualTo(p("..", "..", "..", "xxx", "yyy", "zzz.txt"));
        assertThat(p2.relativize(p1).toString()).isEqualTo(p("..", "..", "..", "ccc", "ddd", "eee.txt"));

        // Path.equals() demo, "./" などの相対パス解決まではせず、文字列上での比較ぽい。
        p1 = Paths.get("aaa");
        p2 = Paths.get("aaa");
        p3 = Paths.get("./aaa");
        assertThat(p1.equals(p2)).isTrue();
        assertThat(p1.equals(p3)).isFalse();
    }

    @Test
    public void testFilesUtilityUsageDemo() throws IOException, URISyntaxException {
        System.out.println("tempDir=[" + tempDir + "]");

        File f0 = new File(tempDir.toString() + "/demo1.txt");
        assertThat(f0.createNewFile()).isTrue();
        File f1 = new File(tempDir.toString() + "/dir1/dir1a/dir1b");
        assertThat(f1.mkdirs()).isTrue();

        // File.toPath()
        Path p0 = f0.toPath();
        Path p1 = f0.toPath();
        Path p2 = f1.toPath();
        assertThat(p0.equals(p1)).isTrue();
        assertThat(p0.equals(p2)).isFalse();

        assertThat(Files.isSameFile(p0, p1)).isTrue();
        assertThat(Files.isSameFile(p0, p2)).isFalse();

        assertThat(Files.exists(p0)).isTrue();
        assertThat(Files.exists(p2)).isTrue();
        assertThat(Files.exists(Paths.get("xx"))).isFalse();

        assertThat(Files.isDirectory(p0)).isFalse();
        assertThat(Files.isDirectory(p2)).isTrue();

        assertThat(Files.isRegularFile(p0)).isTrue();
        assertThat(Files.isRegularFile(p2)).isFalse();

        assertThat(Files.isReadable(p0)).isTrue();
        assertThat(Files.isWritable(p0)).isTrue();
        // assertThat(Files.isExecutable(p0)).isTrue(); // win10, NTFS上では true

        assertThat(Files.isReadable(p2)).isTrue();
        assertThat(Files.isWritable(p2)).isTrue();
        assertThat(Files.isExecutable(p2)).isTrue();

        // Files.createDirectory(), createDirectories()
        p0 = Paths.get(tempDir.toString(), "dir2");
        Files.createDirectory(p0);
        final Path dir2 = Paths.get(p0.toString(), "dir2a/dir2b");
        assertThatThrownBy(() -> {
            Files.createDirectory(dir2);
        }).isInstanceOf(NoSuchFileException.class);
        Files.createDirectories(dir2);

        // Files.delete(), deleteIfExists()
        p2 = Files.createFile(Paths.get(dir2.toString(), "data2.txt"));
        assertThatThrownBy(() -> {
            Files.delete(dir2);
        }).isInstanceOf(DirectoryNotEmptyException.class);

        Files.delete(p2);
        assertThatThrownBy(() -> {
            Files.delete(Paths.get(dir2.toString(), "data2.txt"));
        }).isInstanceOf(NoSuchFileException.class);
        assertThat(Files.deleteIfExists(p2)).isFalse(); // 存在しないため false を返す。
        assertThat(Files.deleteIfExists(dir2)).isTrue();
        assertThat(Files.deleteIfExists(dir2.getParent())).isTrue();
        Files.delete(p0);
        assertThat(Files.exists(p0)).isFalse();
        // NOTE: File.delete()/deleteIfExists() でターゲットがsymlinkの場合、symlink 自体を削除する。
        // (= symlink を追わない)

        // Files read/write
        // OpenOption 省略時は StandardOpenOption.CREATE, TRUNCATE_EXISTING, WRITE が指定された挙動となる。
        p0 = Files.writeString(
                Paths.get(tempDir.toString(), "data3.txt"),
                "日本語\nこんにちは\n",
                StandardCharsets.UTF_8);
        List<String> lines = Files.readAllLines(p0, StandardCharsets.UTF_8);
        assertThat(lines).isEqualTo(List.of("日本語", "こんにちは"));

        // TRUNCATE_EXISTING が暗黙的に使われるので、追記されず上書きになる。
        p0 = Files.writeString(
                Paths.get(tempDir.toString(), "data3.txt"),
                "日本語\nこんにちは\n",
                StandardCharsets.UTF_8);
        lines = Files.readAllLines(p0, StandardCharsets.UTF_8);
        assertThat(lines).isEqualTo(List.of("日本語", "こんにちは"));

        assertThatThrownBy(() -> {
            // 既に存在するので CREATE_NEW を指定すると FileAlreadyExistsException (IOException 由来の check 例外)
            Files.writeString(
                    Paths.get(tempDir.toString(), "data3.txt"),
                    "日本語\nこんにちは\n",
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE_NEW);
        }).isInstanceOf(FileAlreadyExistsException.class);

        assertThatThrownBy(() -> {
            // write なのに READ option を指定すると IllegalArgumentException
            Files.writeString(
                    Paths.get(tempDir.toString(), "data3.txt"),
                    "日本語\nこんにちは\n",
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.READ);
        }).isInstanceOf(IllegalArgumentException.class);

        // APPEND を指定すれば追記になる。
        p0 = Files.writeString(
                Paths.get(tempDir.toString(), "data3.txt"),
                "日本語\nこんにちは\n",
                StandardCharsets.UTF_8,
                StandardOpenOption.APPEND);
        lines = Files.readAllLines(p0, StandardCharsets.UTF_8);
        assertThat(lines).isEqualTo(List.of("日本語", "こんにちは", "日本語", "こんにちは"));

        // Files.lines() だと Stream<String> で内容を読み出せる。
        assertThat(Files.lines(p0, StandardCharsets.UTF_8).collect(Collectors.joining("\n")))
                .isEqualTo("日本語\n" +
                        "こんにちは\n" +
                        "日本語\n" +
                        "こんにちは");

        // show file attribute
        FileTime filetime0 = (FileTime) Files.getAttribute(p0, "creationTime");
        FileTime filetime1 = (FileTime) Files.getAttribute(p0, "lastModifiedTime");
        FileTime filetime2 = (FileTime) Files.getAttribute(p0, "lastAccessTime");
        System.out.println("Files.getAttribute()::creationTime=" + filetime0);
        System.out.println("Files.getAttribute()::lastModifiedTime=" + filetime1);
        System.out.println("Files.getAttribute()::lastAccessTime=" + filetime2);
        long sz0 = (Long) Files.getAttribute(p0, "size");
        System.out.println("Files.getAttribute()::size=" + sz0);
        assertThat((Boolean) Files.getAttribute(p0, "isRegularFile")).isTrue();
        assertThat((Boolean) Files.getAttribute(p0, "isDirectory")).isFalse();
        assertThat((Boolean) Files.getAttribute(p0, "isSymbolicLink")).isFalse();

        BasicFileAttributes attr0 = Files.readAttributes(p0, BasicFileAttributes.class);
        System.out.println("BasicFileAttributes.creationTime()=" + attr0.creationTime());
        System.out.println("BasicFileAttributes.lastModifiedTime()=" + attr0.lastModifiedTime());
        System.out.println("BasicFileAttributes.lastAccessTime()=" + attr0.lastAccessTime());
        System.out.println("BasicFileAttributes.size()=" + attr0.size());
        assertThat(attr0.isRegularFile()).isTrue();
        assertThat(attr0.isDirectory()).isFalse();
        assertThat(attr0.isSymbolicLink()).isFalse();
    }

    @Test
    @EnabledOnOs({ OS.WINDOWS })
    public void testDosFileAttributesDemo() throws IOException {
        System.out.println("tempDir=[" + tempDir + "]");

        Path p0 = Files.writeString(
                Paths.get(tempDir.toString(), "data3.txt"),
                "日本語\nこんにちは",
                StandardCharsets.UTF_8);

        DosFileAttributes attr = Files.readAttributes(p0, DosFileAttributes.class);
        assertThat(attr.isArchive()).isTrue();
        assertThat(attr.isHidden()).isFalse();
        assertThat(attr.isReadOnly()).isFalse();
        assertThat(attr.isSystem()).isFalse();
    }

    @Test
    public void testFilesUtilityCopyMoveDemo() throws IOException, URISyntaxException {
        System.out.println("tempDir=[" + tempDir + "]");

        Files.createDirectory(Paths.get(tempDir.toString(), "dir1"));
        Files.createDirectory(Paths.get(tempDir.toString(), "dir2"));

        Files.write(Paths.get(tempDir.toString(), "dir1/f1.txt"), new byte[] { 1, 2, 3, 4, 5 });

        Files.copy(
                Paths.get(tempDir.toString(), "dir1/f1.txt"),
                Paths.get(tempDir.toString(), "dir2/f2.txt"));

        assertThat(Files.readAllBytes(Paths.get(tempDir.toString(), "dir2/f2.txt")))
                .isEqualTo(new byte[] { 1, 2, 3, 4, 5 });

        Files.write(Paths.get(tempDir.toString(), "dir1/f1.txt"), new byte[] { 6, 7, 8 });

        // オプション指定無しだと、コピー先のファイルが存在するときは
        // FileAlreadyExistsException (IOException 由来の check例外) 発生。
        assertThatThrownBy(() -> {
            Files.copy(
                    Paths.get(tempDir.toString(), "dir1/f1.txt"),
                    Paths.get(tempDir.toString(), "dir2/f2.txt"));
        }).isInstanceOf(FileAlreadyExistsException.class);

        // コピー先を上書きしてOKなときは StandardCopyOption.REPLACE_EXISTING を指定。
        Files.copy(
                Paths.get(tempDir.toString(), "dir1/f1.txt"),
                Paths.get(tempDir.toString(), "dir2/f2.txt"),
                StandardCopyOption.REPLACE_EXISTING);
        assertThat(Files.readAllBytes(Paths.get(tempDir.toString(), "dir2/f2.txt")))
                .isEqualTo(new byte[] { 6, 7, 8 });
        // copy 用だと他に COPY_ATTRIBUTES も使える。

        // unix系だとデフォルトでは symlink を辿ってコピーする。
        // -> symlink のままコピーするなら LinkOption.NOFOLLOW_LINKS を指定する。
        Files.copy(
                Paths.get(tempDir.toString(), "dir1/f1.txt"),
                Paths.get(tempDir.toString(), "dir1/f1b.txt"),
                LinkOption.NOFOLLOW_LINKS); // win で指定しても特にエラーにはならない。
        assertThat(Files.readAllBytes(Paths.get(tempDir.toString(), "dir1/f1b.txt")))
                .isEqualTo(new byte[] { 6, 7, 8 });

        // ディレクトリのコピーは、ディレクトリそのものしかコピーしない。
        Files.copy(
                Paths.get(tempDir.toString(), "dir1"),
                Paths.get(tempDir.toString(), "dir3"));
        assertThat(Files.isDirectory(Paths.get(tempDir.toString(), "dir3"))).isTrue();
        // ディレクトリの中身まで再帰的にコピーするわけではない。
        assertThat(Files.exists(Paths.get(tempDir.toString(), "dir3/f1.txt"))).isFalse();
        assertThat(Files.exists(Paths.get(tempDir.toString(), "dir3/f1b.txt"))).isFalse();

        Files.move(
                Paths.get(tempDir.toString(), "dir1/f1b.txt"),
                Paths.get(tempDir.toString(), "dir2/f2b.txt"));
        assertThat(Files.readAllBytes(Paths.get(tempDir.toString(), "dir2/f2b.txt")))
                .isEqualTo(new byte[] { 6, 7, 8 });

        Files.write(Paths.get(tempDir.toString(), "dir1/f1b.txt"), new byte[] { 9, 0, 1 });
        // オプション指定無しだと、コピー先のファイルが存在するときは
        // FileAlreadyExistsException (IOException 由来の check例外) 発生。
        assertThatThrownBy(() -> {
            Files.move(
                    Paths.get(tempDir.toString(), "dir1/f1b.txt"),
                    Paths.get(tempDir.toString(), "dir2/f2b.txt"));
        }).isInstanceOf(FileAlreadyExistsException.class);

        // コピー先を上書きしてOKなときは StandardCopyOption.REPLACE_EXISTING を指定。
        Files.move(
                Paths.get(tempDir.toString(), "dir1/f1b.txt"),
                Paths.get(tempDir.toString(), "dir2/f2b.txt"),
                StandardCopyOption.REPLACE_EXISTING);
        assertThat(Files.readAllBytes(Paths.get(tempDir.toString(), "dir2/f2b.txt")))
                .isEqualTo(new byte[] { 9, 0, 1 });

        // ディレクトリの移動は、そのまままるごと移動。
        // (mount point(unix) or drive(win) を跨いだ移動ではIOExceptionが発生する模様)
        Files.move(
                Paths.get(tempDir.toString(), "dir1"),
                Paths.get(tempDir.toString(), "dir4"));
        assertThat(Files.isDirectory(Paths.get(tempDir.toString(), "dir4"))).isTrue();
        assertThat(Files.readAllBytes(Paths.get(tempDir.toString(), "dir4/f1.txt")))
                .isEqualTo(new byte[] { 6, 7, 8 });
    }

    @Test
    public void testShowRootDirectories() {
        FileSystem fs = FileSystems.getDefault();
        Iterable<Path> dirs = fs.getRootDirectories();
        for (Path name : dirs) {
            System.out.println("RootDirectories : " + name);
        }
    }

    @Test
    public void testShowDirectoryStream() throws IOException {
        System.out.println("tempDir=[" + tempDir + "]");
        Files.createFile(Paths.get(tempDir.toString(), "aaa.txt"));
        Files.createFile(Paths.get(tempDir.toString(), "bbb.jpg"));
        Files.createDirectory(Paths.get(tempDir.toString(), "dir1"));
        Files.createFile(Paths.get(tempDir.toString(), "dir1/ccc.txt"));
        Files.createFile(Paths.get(tempDir.toString(), "dir1/ddd.jpg"));
        Files.createDirectory(Paths.get(tempDir.toString(), "dir2"));

        System.out.println("-- show DirectoryStream<Path>:start");
        DirectoryStream<Path> stream = Files.newDirectoryStream(tempDir);
        stream.forEach(System.out::println);
        System.out.println("-- show DirectoryStream<Path>:end");
    }

    @Test
    public void testDirectoryTreeWalkings() throws IOException {
        System.out.println("tempDir=[" + tempDir + "]");
        Files.createFile(Paths.get(tempDir.toString(), "aaa.txt"));
        Files.createFile(Paths.get(tempDir.toString(), "bbb.jpg"));
        Files.createDirectory(Paths.get(tempDir.toString(), "dir1"));
        Files.createFile(Paths.get(tempDir.toString(), "dir1/aaa.txt"));
        Files.createFile(Paths.get(tempDir.toString(), "dir1/bbb.jpg"));
        Files.createDirectories(Paths.get(tempDir.toString(), "dir1/dir1a"));
        Files.createFile(Paths.get(tempDir.toString(), "dir1/dir1a/aaa.txt"));
        Files.createFile(Paths.get(tempDir.toString(), "dir1/dir1a/bbb.jpg"));
        Files.createDirectories(Paths.get(tempDir.toString(), "dir1/dir1b"));
        Files.createDirectory(Paths.get(tempDir.toString(), "dir2"));
        Files.createFile(Paths.get(tempDir.toString(), "dir2/aaa.txt"));
        Files.createFile(Paths.get(tempDir.toString(), "dir2/bbb.jpg"));

        // FileVisitOption.FOLLOW_LINKS を指定するとsymlinkも辿ってくれる
        List<String> trees = Files.walk(tempDir)
                // absolute path に変換後、tempDir 部分を削除
                .map(p -> p.toAbsolutePath().toString().replace(tempDir.toString(), ""))
                .sorted()
                .collect(Collectors.toList());
        assertThat(trees).isEqualTo(List.of(
                "", // tempDir 自身
                p2("aaa.txt"),
                p2("bbb.jpg"),
                p2("dir1"),
                p2("dir1", "aaa.txt"),
                p2("dir1", "bbb.jpg"),
                p2("dir1", "dir1a"),
                p2("dir1", "dir1a", "aaa.txt"),
                p2("dir1", "dir1a", "bbb.jpg"),
                p2("dir1", "dir1b"), // 空ディレクトリも抽出される。
                p2("dir2"),
                p2("dir2", "aaa.txt"),
                p2("dir2", "bbb.jpg")));

        // ディレクトリをたどる深さを制限できる。
        trees = Files.walk(tempDir, 1, FileVisitOption.FOLLOW_LINKS)
                // absolute path に変換後、tempDir 部分を削除
                .map(p -> p.toAbsolutePath().toString().replace(tempDir.toString(), ""))
                .sorted()
                .collect(Collectors.toList());
        assertThat(trees).isEqualTo(List.of(
                "", // tempDir 自身
                p2("aaa.txt"),
                p2("bbb.jpg"),
                p2("dir1"),
                p2("dir2")));

        // 深さ0を指定すると、そのディレクトリだけが抽出される。
        trees = Files.walk(tempDir, 0)
                // absolute path に変換後、tempDir 部分を削除
                .map(p -> p.toAbsolutePath().toString().replace(tempDir.toString(), ""))
                .sorted()
                .collect(Collectors.toList());
        assertThat(trees).isEqualTo(List.of("")); // tempDir 自身のみ。

        // no-dir(= regular file) でパス要素が "bbb.jpg" で終わってるものだけ抽出
        trees = Files.walk(tempDir)
                .filter(p -> !Files.isDirectory(p))
                .filter(p -> p.endsWith("bbb.jpg"))
                .map(p -> p.toAbsolutePath().toString().replace(tempDir.toString(), ""))
                .sorted()
                .collect(Collectors.toList());
        assertThat(trees).isEqualTo(List.of(
                p2("bbb.jpg"),
                p2("dir1", "bbb.jpg"),
                p2("dir1", "dir1a", "bbb.jpg"),
                p2("dir2", "bbb.jpg")));

        // walk()で regular-file を指定すると、そのファイルだけが抽出される。
        trees = Files.walk(tempDir.resolve("aaa.txt"))
                .map(p -> p.toAbsolutePath().toString().replace(tempDir.toString(), ""))
                .sorted()
                .collect(Collectors.toList());
        assertThat(trees).isEqualTo(List.of(p2("aaa.txt")));

        // list()を使うと、そのディレクトリ直下のentryだけが抽出される。
        // walk(1) との違い : list() ではそのディレクトリ自体は含まれない。(walk()では含まれる)
        trees = Files.list(tempDir)
                .map(p -> p.toAbsolutePath().toString().replace(tempDir.toString(), ""))
                .sorted()
                .collect(Collectors.toList());
        assertThat(trees).isEqualTo(List.of(
                p2("aaa.txt"),
                p2("bbb.jpg"),
                p2("dir1"),
                p2("dir2")));

        // list()で regular-file を指定すると、NotDirectoryException(IOException 由来の check例外)発生
        assertThatThrownBy(() -> {
            Files.list(tempDir.resolve("aaa.txt"))
                    .map(p -> toString()).sorted().collect(Collectors.toList());
        }).isInstanceOf(NotDirectoryException.class);

        // find() の例。predicate は Path と BasicFileAttributes の2つを引数に取る BiPredicate になってる。
        // 引数順序として maxDepth の指定が必須。
        trees = Files.find(
                tempDir,
                10,
                (p, attr) -> !Files.isDirectory(p) && p.endsWith("bbb.jpg"))
                .map(p -> p.toAbsolutePath().toString().replace(tempDir.toString(), ""))
                .sorted()
                .collect(Collectors.toList());
        assertThat(trees).isEqualTo(List.of(
                p2("bbb.jpg"),
                p2("dir1", "bbb.jpg"),
                p2("dir1", "dir1a", "bbb.jpg"),
                p2("dir2", "bbb.jpg")));

        /* NOTE: walk(), find() とも, maxDepth がnegative valueの場合は
         * IllegalArgumentException 発生。
         */
    }
}
