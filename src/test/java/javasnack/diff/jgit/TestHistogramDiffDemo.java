package javasnack.diff.jgit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.diff.HistogramDiff;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.diff.RawTextComparator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * JGit の HistogramDiff の使い方のデモと、formatterのサンプル
 */
/* see:
 * - https://www.eclipse.org/jgit/
 * - https://github.com/eclipse/jgit
 * 
 * HistogramDiff:
 * - https://www.codeaffine.com/2016/06/16/jgit-diff/
 * - https://stackoverflow.com/questions/32365271/whats-the-difference-between-git-diff-patience-and-git-diff-histogram
 * 
 * diff アルゴリズムの性能比較:
 * - https://github.com/msakamoto-sf/java-diff-libs-benchmark
 */
public class TestHistogramDiffDemo {

    public static RawText rawtext(String text) {
        return new RawText(text.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    public void testLineBasedDemoUsage() {
        final HistogramDiff hd = new HistogramDiff();
        RawText t1 = rawtext("");
        RawText t2 = rawtext("");
        EditList el = hd.diff(RawTextComparator.DEFAULT, t1, t2);
        assertThat(el.size()).isEqualTo(0);

        t1 = rawtext("aaa");
        t2 = rawtext("aaa");
        el = hd.diff(RawTextComparator.DEFAULT, t1, t2);
        assertThat(el.size()).isEqualTo(0);

        t1 = rawtext("日本語");
        t2 = rawtext("日本語");
        el = hd.diff(RawTextComparator.DEFAULT, t1, t2);
        assertThat(el.size()).isEqualTo(0);

        t1 = rawtext("");
        t2 = rawtext("aaa");
        el = hd.diff(RawTextComparator.DEFAULT, t1, t2);
        assertThat(el.size()).isEqualTo(1);
        assertThat(el.get(0)).isEqualTo(new Edit(0, 0, 0, 1));
        assertThat(el.get(0).getLengthA()).isEqualTo(0);
        assertThat(el.get(0).getLengthB()).isEqualTo(1);
        assertThat(el.get(0).getType()).isEqualTo(Edit.Type.INSERT);

        t1 = rawtext("");
        t2 = rawtext("aaa\nbbb");
        el = hd.diff(RawTextComparator.DEFAULT, t1, t2);
        assertThat(el.size()).isEqualTo(1);
        assertThat(el.get(0)).isEqualTo(new Edit(0, 0, 0, 2));
        assertThat(el.get(0).getLengthA()).isEqualTo(0);
        assertThat(el.get(0).getLengthB()).isEqualTo(2);
        assertThat(el.get(0).getType()).isEqualTo(Edit.Type.INSERT);

        t1 = rawtext("aaa\nbbb\nccc");
        t2 = rawtext("aaa\n");
        el = hd.diff(RawTextComparator.DEFAULT, t1, t2);
        assertThat(el.size()).isEqualTo(1);
        assertThat(el.get(0)).isEqualTo(new Edit(1, 3, 1, 1));
        assertThat(el.get(0).getLengthA()).isEqualTo(2);
        assertThat(el.get(0).getLengthB()).isEqualTo(0);
        assertThat(el.get(0).getType()).isEqualTo(Edit.Type.DELETE);

        t1 = rawtext("aaa\nbbb\nccc\nddd");
        t2 = rawtext("aaa\nddd");
        el = hd.diff(RawTextComparator.DEFAULT, t1, t2);
        assertThat(el.size()).isEqualTo(1);
        assertThat(el.get(0)).isEqualTo(new Edit(1, 3, 1, 1));
        assertThat(el.get(0).getLengthA()).isEqualTo(2);
        assertThat(el.get(0).getLengthB()).isEqualTo(0);
        assertThat(el.get(0).getType()).isEqualTo(Edit.Type.DELETE);

        t1 = rawtext("aaa\nbbb\nccc");
        t2 = rawtext("aaa\nBBB\nccc");
        el = hd.diff(RawTextComparator.DEFAULT, t1, t2);
        assertThat(el.size()).isEqualTo(1);
        assertThat(el.get(0)).isEqualTo(new Edit(1, 2, 1, 2));
        assertThat(el.get(0).getLengthA()).isEqualTo(1);
        assertThat(el.get(0).getLengthB()).isEqualTo(1);
        assertThat(el.get(0).getType()).isEqualTo(Edit.Type.REPLACE);

        t1 = rawtext("aaa\nbbb\nccc\nddd\neee\nfff");
        t2 = rawtext("aaa\nBBB\nccc\neee\nfff\nggg\nhhh");
        el = hd.diff(RawTextComparator.DEFAULT, t1, t2);
        assertThat(el.size()).isEqualTo(3);
        assertThat(el.get(0)).isEqualTo(new Edit(1, 2, 1, 2));
        assertThat(el.get(0).getLengthA()).isEqualTo(1);
        assertThat(el.get(0).getLengthB()).isEqualTo(1);
        assertThat(el.get(0).getType()).isEqualTo(Edit.Type.REPLACE);
        assertThat(el.get(1)).isEqualTo(new Edit(3, 4, 3, 3));
        assertThat(el.get(1).getLengthA()).isEqualTo(1);
        assertThat(el.get(1).getLengthB()).isEqualTo(0);
        assertThat(el.get(1).getType()).isEqualTo(Edit.Type.DELETE);
        assertThat(el.get(2)).isEqualTo(new Edit(5, 6, 4, 7));
        assertThat(el.get(2).getLengthA()).isEqualTo(1);
        assertThat(el.get(2).getLengthB()).isEqualTo(3);
        assertThat(el.get(2).getType()).isEqualTo(Edit.Type.REPLACE);

        t1 = rawtext("aaa");
        t2 = rawtext("aAa");
        el = hd.diff(RawTextComparator.DEFAULT, t1, t2);
        assertThat(el.size()).isEqualTo(1);
        assertThat(el.get(0)).isEqualTo(new Edit(0, 1, 0, 1));
        assertThat(el.get(0).getLengthA()).isEqualTo(1);
        assertThat(el.get(0).getLengthB()).isEqualTo(1);
        assertThat(el.get(0).getType()).isEqualTo(Edit.Type.REPLACE);

        t1 = rawtext("aaa\n");
        t2 = rawtext("aaa");
        el = hd.diff(RawTextComparator.DEFAULT, t1, t2);
        assertThat(el.size()).isEqualTo(1);
        assertThat(el.get(0)).isEqualTo(new Edit(0, 1, 0, 1));
        assertThat(el.get(0).getLengthA()).isEqualTo(1);
        assertThat(el.get(0).getLengthB()).isEqualTo(1);
        assertThat(el.get(0).getType()).isEqualTo(Edit.Type.REPLACE);

        t1 = rawtext("aaa");
        t2 = rawtext("aaa\n");
        el = hd.diff(RawTextComparator.DEFAULT, t1, t2);
        assertThat(el.size()).isEqualTo(1);
        assertThat(el.get(0)).isEqualTo(new Edit(0, 1, 0, 1));
        assertThat(el.get(0).getLengthA()).isEqualTo(1);
        assertThat(el.get(0).getLengthB()).isEqualTo(1);
        assertThat(el.get(0).getType()).isEqualTo(Edit.Type.REPLACE);
        //        el.forEach(e -> {
        //            System.out.println(e);
        //        });
    }

    public static class LineBasedDiffBiFormatted {
        public String s1 = "";
        public String s2 = "";
    }

    public static class LineBasedDiffBiFormatter {
        public static LineBasedDiffBiFormatted format(
                final List<String> lines1,
                final List<String> lines2,
                final EditList editList) {
            if (lines1.size() == 0 && lines2.size() == 0) {
                return new LineBasedDiffBiFormatted();
            }
            final List<String> buf1 = new ArrayList<>(lines1.size() + lines2.size());
            final List<String> buf2 = new ArrayList<>(lines1.size() + lines2.size());
            //System.out.println("###########");
            //System.out.println(lines1);
            //System.out.println(lines2);
            int lastIndex1 = 0;
            int lastIndex2 = 0;
            for (final Edit e : editList) {
                //System.out.println("e=" + e.toString() + " / lastIndex1=" + lastIndex1 + ", lastIndex2=" + lastIndex2);
                switch (e.getType()) {
                case INSERT:
                    for (int i = lastIndex1; (i < lines1.size()) && (i < e.getBeginA()); i++) {
                        final String line1 = lines1.get(i);
                        buf1.add(line1);
                    }
                    for (int i = lastIndex2; (i < lines2.size()) && (i < e.getBeginB()); i++) {
                        final String line2 = lines2.get(i);
                        buf2.add(line2);
                    }
                    for (int i = e.getBeginB(); (i < lines2.size()) && (i < e.getEndB()); i++) {
                        final String line2 = lines2.get(i);
                        buf1.add("-" + line2 + "-");
                        buf2.add(line2);
                    }
                    lastIndex1 = e.getEndA();
                    lastIndex2 = e.getEndB();
                    break;
                case REPLACE:
                    for (int i = lastIndex1; (i < lines1.size()) && (i < e.getBeginA()); i++) {
                        final String line1 = lines1.get(i);
                        buf1.add(line1);
                    }
                    for (int i = lastIndex2; (i < lines2.size()) && (i < e.getBeginB()); i++) {
                        final String line2 = lines2.get(i);
                        buf2.add(line2);
                    }
                    for (int i = e.getBeginA(); (i < lines1.size()) && (i < e.getEndA()); i++) {
                        final String line1 = lines1.get(i);
                        buf1.add("-" + line1 + "-");
                    }
                    for (int i = e.getBeginB(); (i < lines2.size()) && (i < e.getEndB()); i++) {
                        final String line2 = lines2.get(i);
                        buf2.add("+" + line2 + "+");
                    }
                    lastIndex1 = e.getEndA();
                    lastIndex2 = e.getEndB();
                    break;
                case DELETE:
                    for (int i = lastIndex1; (i < lines1.size()) && (i < e.getBeginA()); i++) {
                        final String line1 = lines1.get(i);
                        buf1.add(line1);
                    }
                    for (int i = lastIndex2; (i < lines2.size()) && (i < e.getBeginB()); i++) {
                        final String line2 = lines2.get(i);
                        buf2.add(line2);
                    }
                    for (int i = e.getBeginA(); (i < lines1.size()) && (i < e.getEndA()); i++) {
                        final String line1 = lines1.get(i);
                        buf1.add(line1);
                        buf2.add("-" + line1 + "-");
                    }
                    lastIndex1 = e.getEndA();
                    lastIndex2 = e.getEndB();
                    break;
                default:
                    // illegal type : do nothing.
                }
            }
            //System.out.println("[1]=" + lastIndex1);
            //System.out.println("[2]=" + lastIndex2);
            for (int i = lastIndex1; i < lines1.size(); i++) {
                final String line1 = lines1.get(i);
                buf1.add(line1);
            }
            for (int i = lastIndex2; i < lines2.size(); i++) {
                final String line2 = lines2.get(i);
                buf2.add(line2);
            }
            //System.out.println(buf1);
            //System.out.println(buf2);
            final LineBasedDiffBiFormatted r = new LineBasedDiffBiFormatted();
            r.s1 = String.join("\n", buf1);
            r.s2 = String.join("\n", buf2);
            return r;
        }
    }

    public static List<String> split(final String src) {
        final String normalized = src.replace("\r\n", "\n");
        final String[] lines = normalized.split("\\n");
        if (lines.length == 1 && "".equals(lines[0])) {
            return Collections.emptyList();
        }
        final List<String> r = new ArrayList<>(lines.length);
        for (int i = 0; i < lines.length; i++) {
            r.add(lines[i]);
        }
        return Collections.unmodifiableList(r);
    }

    static Stream<Arguments> provideLineBasedDiffArguments() {
        // @formatter:off
        return Stream.of(
                arguments("", "aaa", "-aaa-", "aaa"),
                arguments("aaa", "", "aaa", "-aaa-"),
                arguments("aaa", "bbb", "-aaa-", "+bbb+"),
                arguments("aaa", "aaa", "aaa", "aaa"),
                arguments("aaa\nbbb", "aaa\nbbb", "aaa\nbbb", "aaa\nbbb"),
                arguments("aaa\n", "aaa\nbbb", "aaa\n-bbb-", "aaa\nbbb"),
                arguments("aaa\nbbb", "aaa\n", "aaa\nbbb", "aaa\n-bbb-"),
                arguments("aaa\nbbb", "aaa\nccc", "aaa\n-bbb-", "aaa\n+ccc+"),
                arguments("aaa\nbbb\n", "aaa\nbbb\nccc", "aaa\nbbb\n-ccc-", "aaa\nbbb\nccc"),
                arguments("aaa\nbbb\nccc", "aaa\nbbb\n", "aaa\nbbb\nccc", "aaa\nbbb\n-ccc-"),
                arguments("aaa\nbbb\nccc", "aaa\nbbb\nddd", "aaa\nbbb\n-ccc-", "aaa\nbbb\n+ddd+"),
                arguments("", "aaa\nbbb\n", "-aaa-\n-bbb-", "aaa\nbbb"),
                arguments("aaa\nbbb\n", "", "aaa\nbbb", "-aaa-\n-bbb-"),
                arguments("aaa\nbbb\n", "ccc\nddd\n", "-aaa-\n-bbb-", "+ccc+\n+ddd+"),
                arguments("aaa\n", "aaa\nbbb\nccc", "aaa\n-bbb-\n-ccc-", "aaa\nbbb\nccc"),
                arguments("aaa\nbbb\nccc", "aaa\n", "aaa\nbbb\nccc", "aaa\n-bbb-\n-ccc-"),
                arguments("aaa\nbbb\nccc", "aaa\nBBB\nCCC", "aaa\n-bbb-\n-ccc-", "aaa\n+BBB+\n+CCC+"),
                arguments("aaa\nbbb\nccc", "aaa\nbbb\nccc", "aaa\nbbb\nccc", "aaa\nbbb\nccc"),
                arguments("aaa\nbbb\n", "aaa\nbbb\nccc\nddd", "aaa\nbbb\n-ccc-\n-ddd-", "aaa\nbbb\nccc\nddd"),
                arguments("aaa\nbbb\nccc\nddd", "aaa\nbbb\n", "aaa\nbbb\nccc\nddd", "aaa\nbbb\n-ccc-\n-ddd-"),
                arguments("aaa\nbbb\nccc\nddd", "aaa\nbbb\nCCC\nDDD", "aaa\nbbb\n-ccc-\n-ddd-", "aaa\nbbb\n+CCC+\n+DDD+"),
                arguments("", "aaa\nbbb\nccc\n", "-aaa-\n-bbb-\n-ccc-", "aaa\nbbb\nccc"),
                arguments("aaa\nbbb\nccc\n", "", "aaa\nbbb\nccc", "-aaa-\n-bbb-\n-ccc-"),
                arguments("aaa\nbbb\nccc\n", "ddd\neee\nfff\n", "-aaa-\n-bbb-\n-ccc-", "+ddd+\n+eee+\n+fff+"),
                arguments("aaa\n", "aaa\nbbb\nccc\nddd", "aaa\n-bbb-\n-ccc-\n-ddd-", "aaa\nbbb\nccc\nddd"),
                arguments("aaa\nbbb\nccc\nddd", "aaa\n", "aaa\nbbb\nccc\nddd", "aaa\n-bbb-\n-ccc-\n-ddd-"),
                arguments("aaa\nbbb\nccc\nddd", "aaa\nBBB\nCCC\nDDD", "aaa\n-bbb-\n-ccc-\n-ddd-", "aaa\n+BBB+\n+CCC+\n+DDD+"),
                arguments("aaa\nbbb\n", "aaa\nbbb\nccc\nddd\neee", "aaa\nbbb\n-ccc-\n-ddd-\n-eee-", "aaa\nbbb\nccc\nddd\neee"),
                arguments("aaa\nbbb\nccc\nddd\neee", "aaa\nbbb\n", "aaa\nbbb\nccc\nddd\neee", "aaa\nbbb\n-ccc-\n-ddd-\n-eee-"),
                arguments("aaa\nbbb\nccc\nddd\neee", "aaa\nbbb\nCCC\nDDD\nEEE", "aaa\nbbb\n-ccc-\n-ddd-\n-eee-", "aaa\nbbb\n+CCC+\n+DDD+\n+EEE+"),
                arguments("bbb", "AAA\nbbb", "-AAA-\nbbb", "AAA\nbbb"),
                arguments("AAA\nbbb\n", "bbb\n", "AAA\nbbb", "-AAA-\nbbb"),
                arguments("AAA\nbbb\n", "BBB\nbbb\n", "-AAA-\nbbb", "+BBB+\nbbb"),
                arguments("aaa\nbbb", "aaa\nAAA\nbbb", "aaa\n-AAA-\nbbb", "aaa\nAAA\nbbb"),
                arguments("aaa\nAAA\nbbb\n", "aaa\nbbb\n", "aaa\nAAA\nbbb", "aaa\n-AAA-\nbbb"),
                arguments("aaa\nAAA\nbbb\n", "aaa\nBBB\nbbb\n", "aaa\n-AAA-\nbbb", "aaa\n+BBB+\nbbb"),
                arguments("aaa\nbbb\nccc\n", "aaa\nbbb\nBBB\nccc\n", "aaa\nbbb\n-BBB-\nccc", "aaa\nbbb\nBBB\nccc"),
                arguments("aaa\nbbb\nBBB\nccc\n", "aaa\nbbb\nccc\n", "aaa\nbbb\nBBB\nccc", "aaa\nbbb\n-BBB-\nccc"),
                arguments("aaa\nbbb\nAAA\nccc\n", "aaa\nbbb\nBBB\nccc\n", "aaa\nbbb\n-AAA-\nccc", "aaa\nbbb\n+BBB+\nccc"),
                arguments("bbb", "AAA\nBBB\nbbb", "-AAA-\n-BBB-\nbbb", "AAA\nBBB\nbbb"),
                arguments("AAA\nBBB\nbbb\n", "bbb\n", "AAA\nBBB\nbbb", "-AAA-\n-BBB-\nbbb"),
                arguments("AAA\nBBB\nbbb\n", "CCC\nDDD\nbbb\n", "-AAA-\n-BBB-\nbbb", "+CCC+\n+DDD+\nbbb"),
                arguments("aaa\nbbb", "aaa\nAAA\nBBB\nbbb", "aaa\n-AAA-\n-BBB-\nbbb", "aaa\nAAA\nBBB\nbbb"),
                arguments("aaa\nAAA\nBBB\nbbb\n", "aaa\nbbb\n", "aaa\nAAA\nBBB\nbbb", "aaa\n-AAA-\n-BBB-\nbbb"),
                arguments("aaa\nAAA\nBBB\nbbb\n", "aaa\nCCC\nDDD\nbbb\n", "aaa\n-AAA-\n-BBB-\nbbb", "aaa\n+CCC+\n+DDD+\nbbb"),
                arguments("aaa\nbbb\nccc\n", "aaa\nbbb\nBBB\nCCC\nccc\n", "aaa\nbbb\n-BBB-\n-CCC-\nccc", "aaa\nbbb\nBBB\nCCC\nccc"),
                arguments("aaa\nbbb\nBBB\nCCC\nccc\n", "aaa\nbbb\nccc\n", "aaa\nbbb\nBBB\nCCC\nccc", "aaa\nbbb\n-BBB-\n-CCC-\nccc"),
                arguments("aaa\nbbb\nAAA\nBBB\nccc\n", "aaa\nbbb\nCCC\nDDD\nccc\n", "aaa\nbbb\n-AAA-\n-BBB-\nccc", "aaa\nbbb\n+CCC+\n+DDD+\nccc"),
                arguments(
                        "bbb","AAA\nBBB\nCCC\nbbb",
                        "-AAA-\n-BBB-\n-CCC-\nbbb","AAA\nBBB\nCCC\nbbb"),
                arguments(
                        "AAA\nBBB\nCCC\nbbb\n", "bbb\n",
                        "AAA\nBBB\nCCC\nbbb", "-AAA-\n-BBB-\n-CCC-\nbbb"),
                arguments(
                        "AAA\nBBB\nCCC\nbbb\n", "DDD\nEEE\nFFF\nbbb\n",
                        "-AAA-\n-BBB-\n-CCC-\nbbb", "+DDD+\n+EEE+\n+FFF+\nbbb"),
                arguments(
                        "aaa\nbbb", "aaa\nAAA\nBBB\nCCC\nbbb",
                        "aaa\n-AAA-\n-BBB-\n-CCC-\nbbb", "aaa\nAAA\nBBB\nCCC\nbbb"),
                arguments(
                        "aaa\nAAA\nBBB\nCCC\nbbb\n", "aaa\nbbb\n",
                        "aaa\nAAA\nBBB\nCCC\nbbb", "aaa\n-AAA-\n-BBB-\n-CCC-\nbbb"),
                arguments(
                        "aaa\nAAA\nBBB\nCCC\nbbb\n", "aaa\nDDD\nEEE\nFFF\nbbb\n",
                        "aaa\n-AAA-\n-BBB-\n-CCC-\nbbb", "aaa\n+DDD+\n+EEE+\n+FFF+\nbbb"),
                arguments(
                        "aaa\nbbb\nccc\n", "aaa\nbbb\nBBB\nCCC\nDDD\nccc\n",
                        "aaa\nbbb\n-BBB-\n-CCC-\n-DDD-\nccc", "aaa\nbbb\nBBB\nCCC\nDDD\nccc"),
                arguments(
                        "aaa\nbbb\nBBB\nCCC\nDDD\nccc\n", "aaa\nbbb\nccc\n",
                        "aaa\nbbb\nBBB\nCCC\nDDD\nccc", "aaa\nbbb\n-BBB-\n-CCC-\n-DDD-\nccc"),
                arguments(
                        "aaa\nbbb\nAAA\nBBB\nCCC\nccc\n", "aaa\nbbb\nDDD\nEEE\nFFF\nccc\n",
                        "aaa\nbbb\n-AAA-\n-BBB-\n-CCC-\nccc", "aaa\nbbb\n+DDD+\n+EEE+\n+FFF+\nccc"),
                arguments("aaa\n", "XXX\naaa\nYYY\n", "-XXX-\naaa\n-YYY-", "XXX\naaa\nYYY"),
                arguments("XXX\naaa\nYYY\n", "aaa\n", "XXX\naaa\nYYY", "-XXX-\naaa\n-YYY-"),
                arguments("XXX\naaa\nYYY\n", "xxx\naaa\nyyy\n", "-XXX-\naaa\n-YYY-", "+xxx+\naaa\n+yyy+"),
                arguments("aaa\nbbb\n", "aaa\nXXX\nbbb\nYYY\n",
                        "aaa\n-XXX-\nbbb\n-YYY-", "aaa\nXXX\nbbb\nYYY"),
                arguments(
                        "aaa\nXXX\nbbb\nYYY\n", "aaa\nbbb\n",
                        "aaa\nXXX\nbbb\nYYY", "aaa\n-XXX-\nbbb\n-YYY-"),
                arguments(
                        "aaa\nXXX\nbbb\nYYY\n", "aaa\nxxx\nbbb\nyyy\n",
                        "aaa\n-XXX-\nbbb\n-YYY-", "aaa\n+xxx+\nbbb\n+yyy+"),
                arguments(
                        "aaa\nbbb\nccc\nddd\neee\n",
                        "aaa\nbbb\nXXX\nccc\nYYY\nZZZ\nddd\neee\n",
                        "aaa\nbbb\n-XXX-\nccc\n-YYY-\n-ZZZ-\nddd\neee",
                        "aaa\nbbb\nXXX\nccc\nYYY\nZZZ\nddd\neee"),
                arguments(
                        "aaa\nbbb\nXXX\nccc\nYYY\nZZZ\nddd\neee\n",
                        "aaa\nbbb\nccc\nddd\neee\n",
                        "aaa\nbbb\nXXX\nccc\nYYY\nZZZ\nddd\neee",
                        "aaa\nbbb\n-XXX-\nccc\n-YYY-\n-ZZZ-\nddd\neee"),
                arguments(
                        "aaa\nbbb\nXXX\nccc\nYYY\nZZZ\nddd\neee\n",
                        "aaa\nbbb\nxxx\nccc\nyyy\nzzz\nddd\neee\n",
                        "aaa\nbbb\n-XXX-\nccc\n-YYY-\n-ZZZ-\nddd\neee",
                        "aaa\nbbb\n+xxx+\nccc\n+yyy+\n+zzz+\nddd\neee"),
                arguments(
                        "aaa\nbbb\nccc\nddd\neee\nfff\n",
                        "bbb\nccc\nXXX\nYYY\neee\nZZZ",
                        "aaa\nbbb\nccc\n-ddd-\neee\n-fff-",
                        "-aaa-\nbbb\nccc\n+XXX+\n+YYY+\neee\n+ZZZ+"),
                arguments(
                        "aaa\r\nbbb\nccc\n",
                        "aaa\nbbb\r\nccc",
                        "-aaa-\n-bbb-\n-ccc-",
                        "+aaa+\n+bbb+\n+ccc+"),
                arguments(
                        "aaa\n"
                        + "bbb\n"
                        + "ccc\n" 
                        + "ddd\n"
                        + "\n"
                        + "eee\n"
                        + "fff\n"
                        + "ggg\n"
                        + "hhh\n"
                        + "iii\n"
                        + "jjj\n"
                        + "kkk\n"
                        + "lll\n"
                        + "mmm\n"
                        + "nnn\n"
                        + "ooo\n"
                        + "ppp\n"
                        + "qqq\n"
                        + "rrr\n"
                        + "sss\n"
                        + "ttt\n"
                        + "uuu", 
                        "AAA\n"
                        + "BBB\n"
                        + "CCC\n"
                        + "DDD\n"
                        + "\n", 
                        "-aaa-\n"
                        + "-bbb-\n"
                        + "-ccc-\n"
                        + "-ddd-\n"
                        + "\n"
                        + "eee\n"
                        + "fff\n"
                        + "ggg\n"
                        + "hhh\n"
                        + "iii\n"
                        + "jjj\n"
                        + "kkk\n"
                        + "lll\n"
                        + "mmm\n"
                        + "nnn\n"
                        + "ooo\n"
                        + "ppp\n"
                        + "qqq\n"
                        + "rrr\n"
                        + "sss\n"
                        + "ttt\n"
                        + "uuu",
                        "+AAA+\n"
                        + "+BBB+\n"
                        + "+CCC+\n"
                        + "+DDD+\n"
                        + "-eee-\n"
                        + "-fff-\n"
                        + "-ggg-\n"
                        + "-hhh-\n"
                        + "-iii-\n"
                        + "-jjj-\n"
                        + "-kkk-\n"
                        + "-lll-\n"
                        + "-mmm-\n"
                        + "-nnn-\n"
                        + "-ooo-\n"
                        + "-ppp-\n"
                        + "-qqq-\n"
                        + "-rrr-\n"
                        + "-sss-\n"
                        + "-ttt-\n"
                        + "-uuu-"),
                arguments("", "", "", ""));
        // @formatter:on
    }

    @ParameterizedTest
    @MethodSource("provideLineBasedDiffArguments")
    public void testLineBasedDiffFormatter(
            final String s1,
            final String s2,
            final String expected1,
            final String expected2) {
        final HistogramDiff hd = new HistogramDiff();
        EditList el = hd.diff(RawTextComparator.DEFAULT, rawtext(s1), rawtext(s2));
        LineBasedDiffBiFormatted f = LineBasedDiffBiFormatter.format(split(s1), split(s2), el);
        assertThat(f.s1).isEqualTo(expected1);
        assertThat(f.s2).isEqualTo(expected2);
    }
}
