package javasnack.diff.jgit;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.diff.HistogramDiff;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.diff.RawTextComparator;
import org.junit.jupiter.api.Test;

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
        assertThat(el.get(0).getType()).isEqualTo(Edit.Type.INSERT);

        t1 = rawtext("");
        t2 = rawtext("aaa\nbbb");
        el = hd.diff(RawTextComparator.DEFAULT, t1, t2);
        assertThat(el.size()).isEqualTo(1);
        assertThat(el.get(0)).isEqualTo(new Edit(0, 0, 0, 2));
        assertThat(el.get(0).getType()).isEqualTo(Edit.Type.INSERT);

        t1 = rawtext("aaa\nbbb\nccc");
        t2 = rawtext("aaa\n");
        el = hd.diff(RawTextComparator.DEFAULT, t1, t2);
        assertThat(el.size()).isEqualTo(1);
        assertThat(el.get(0)).isEqualTo(new Edit(1, 3, 1, 1));
        assertThat(el.get(0).getType()).isEqualTo(Edit.Type.DELETE);

        t1 = rawtext("aaa\nbbb\nccc\nddd");
        t2 = rawtext("aaa\nddd");
        el = hd.diff(RawTextComparator.DEFAULT, t1, t2);
        assertThat(el.size()).isEqualTo(1);
        assertThat(el.get(0)).isEqualTo(new Edit(1, 3, 1, 1));
        assertThat(el.get(0).getType()).isEqualTo(Edit.Type.DELETE);

        t1 = rawtext("aaa\nbbb\nccc");
        t2 = rawtext("aaa\nBBB\nccc");
        el = hd.diff(RawTextComparator.DEFAULT, t1, t2);
        assertThat(el.size()).isEqualTo(1);
        assertThat(el.get(0)).isEqualTo(new Edit(1, 2, 1, 2));
        assertThat(el.get(0).getType()).isEqualTo(Edit.Type.REPLACE);

        t1 = rawtext("aaa\nbbb\nccc\nddd\neee\nfff");
        t2 = rawtext("aaa\nBBB\nccc\neee\nfff\nggg\nhhh");
        el = hd.diff(RawTextComparator.DEFAULT, t1, t2);
        assertThat(el.size()).isEqualTo(3);
        assertThat(el.get(0)).isEqualTo(new Edit(1, 2, 1, 2));
        assertThat(el.get(0).getType()).isEqualTo(Edit.Type.REPLACE);
        assertThat(el.get(1)).isEqualTo(new Edit(3, 4, 3, 3));
        assertThat(el.get(1).getType()).isEqualTo(Edit.Type.DELETE);
        assertThat(el.get(2)).isEqualTo(new Edit(5, 6, 4, 7));
        assertThat(el.get(2).getType()).isEqualTo(Edit.Type.REPLACE);

        t1 = rawtext("aaa");
        t2 = rawtext("aAa");
        el = hd.diff(RawTextComparator.DEFAULT, t1, t2);
        assertThat(el.size()).isEqualTo(1);
        assertThat(el.get(0)).isEqualTo(new Edit(0, 1, 0, 1));
        assertThat(el.get(0).getType()).isEqualTo(Edit.Type.REPLACE);

        t1 = rawtext("aaa\n");
        t2 = rawtext("aaa");
        el = hd.diff(RawTextComparator.DEFAULT, t1, t2);
        assertThat(el.size()).isEqualTo(1);
        assertThat(el.get(0)).isEqualTo(new Edit(0, 1, 0, 1));
        assertThat(el.get(0).getType()).isEqualTo(Edit.Type.REPLACE);

        t1 = rawtext("aaa");
        t2 = rawtext("aaa\n");
        el = hd.diff(RawTextComparator.DEFAULT, t1, t2);
        assertThat(el.size()).isEqualTo(1);
        assertThat(el.get(0)).isEqualTo(new Edit(0, 1, 0, 1));
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
            // TODO
            System.out.println("###########");
            System.out.println(lines1);
            System.out.println(lines2);
            int lastIndex1 = 0;
            int lastIndex2 = 0;
            for (final Edit e : editList) {
                // TODO
                System.out.println("e=" + e.toString() + " / lastIndex1=" + lastIndex1 + ", lastIndex2=" + lastIndex2);
                switch (e.getType()) {
                case INSERT:
                    for (int i = lastIndex1; i < e.getBeginA(); i++) {
                        final String line1 = lines1.get(i);
                        buf1.add(line1);
                    }
                    for (int i = lastIndex2; i < e.getBeginB(); i++) {
                        final String line2 = lines2.get(i);
                        buf2.add(line2);
                    }
                    for (int i = e.getBeginB(); i < e.getEndB(); i++) {
                        final String line2 = lines2.get(i);
                        buf1.add("-" + line2 + "-");
                        buf2.add(line2);
                    }
                    lastIndex1 = e.getEndA() + 1;
                    lastIndex2 = e.getEndB();
                    break;
                case REPLACE:
                    for (int i = lastIndex1; i < e.getBeginA(); i++) {
                        final String line1 = lines1.get(i);
                        buf1.add(line1);
                    }
                    for (int i = lastIndex2; i < e.getBeginB(); i++) {
                        final String line2 = lines2.get(i);
                        buf2.add(line2);
                    }
                    for (int i = e.getBeginA(); i < e.getEndA(); i++) {
                        final String line1 = lines1.get(i);
                        buf1.add("-" + line1 + "-");
                    }
                    for (int i = e.getBeginB(); i < e.getEndB(); i++) {
                        final String line2 = lines2.get(i);
                        buf2.add("+" + line2 + "+");
                    }
                    lastIndex1 = e.getEndA() + 1;
                    lastIndex2 = e.getEndB() + 1;
                    break;
                case DELETE:
                    for (int i = lastIndex1; i < e.getBeginA(); i++) {
                        final String line1 = lines1.get(i);
                        buf1.add(line1);
                    }
                    for (int i = lastIndex2; i < e.getBeginB(); i++) {
                        final String line2 = lines2.get(i);
                        buf2.add(line2);
                    }
                    for (int i = e.getBeginA(); i < e.getEndA(); i++) {
                        final String line1 = lines1.get(i);
                        buf1.add(line1);
                        buf2.add("-" + line1 + "-");
                    }
                    lastIndex1 = e.getEndA();
                    lastIndex2 = e.getEndB() + 1;
                    break;
                default:
                    // illegal type : do nothing.
                }
            }
            // TODO
            System.out.println("[1]=" + lastIndex1);
            System.out.println("[2]=" + lastIndex2);
            for (int i = lastIndex1; i < lines1.size(); i++) {
                final String line1 = lines1.get(i);
                buf1.add(line1);
            }
            for (int i = lastIndex2; i < lines2.size(); i++) {
                final String line2 = lines2.get(i);
                buf2.add(line2);
            }
            // TODO
            System.out.println(buf1);
            System.out.println(buf2);
            final LineBasedDiffBiFormatted r = new LineBasedDiffBiFormatted();
            r.s1 = String.join("\n", buf1);
            r.s2 = String.join("\n", buf2);
            return r;
        }
    }

    public static List<String> split(final String src) {
        final String normalized = src.replace("\r\n", "\n");
        final String[] lines = normalized.split("\\n");
        System.out.println(lines.length); // TODO
        if (lines.length == 1 && "".equals(lines[0])) {
            return Collections.emptyList();
        }
        final List<String> r = new ArrayList<>(lines.length);
        for (int i = 0; i < lines.length; i++) {
            r.add(lines[i]);
        }
        return Collections.unmodifiableList(r);
    }

    @Test
    public void testLineBasedDiffFormatter1() {
        final HistogramDiff hd = new HistogramDiff();
        String s1 = "";
        String s2 = "";
        EditList el = hd.diff(RawTextComparator.DEFAULT, rawtext(s1), rawtext(s2));
        LineBasedDiffBiFormatted f = LineBasedDiffBiFormatter.format(split(s1), split(s2), el);
        assertThat(f.s1).isEqualTo("");
        assertThat(f.s2).isEqualTo("");

        s1 = "";
        s2 = "aaa";
        el = hd.diff(RawTextComparator.DEFAULT, rawtext(s1), rawtext(s2));
        f = LineBasedDiffBiFormatter.format(split(s1), split(s2), el);
        assertThat(f.s1).isEqualTo("-aaa-");
        assertThat(f.s2).isEqualTo("aaa");

        s1 = "aaa";
        s2 = "";
        el = hd.diff(RawTextComparator.DEFAULT, rawtext(s1), rawtext(s2));
        f = LineBasedDiffBiFormatter.format(split(s1), split(s2), el);
        assertThat(f.s1).isEqualTo("aaa");
        assertThat(f.s2).isEqualTo("-aaa-");

        s1 = "aaa";
        s2 = "bbb";
        el = hd.diff(RawTextComparator.DEFAULT, rawtext(s1), rawtext(s2));
        f = LineBasedDiffBiFormatter.format(split(s1), split(s2), el);
        assertThat(f.s1).isEqualTo("-aaa-");
        assertThat(f.s2).isEqualTo("+bbb+");

        s1 = "aaa";
        s2 = "aaa";
        el = hd.diff(RawTextComparator.DEFAULT, rawtext(s1), rawtext(s2));
        f = LineBasedDiffBiFormatter.format(split(s1), split(s2), el);
        assertThat(f.s1).isEqualTo("aaa");
        assertThat(f.s2).isEqualTo("aaa");

        s1 = "aaa\nbbb";
        s2 = "aaa\nbbb";
        el = hd.diff(RawTextComparator.DEFAULT, rawtext(s1), rawtext(s2));
        f = LineBasedDiffBiFormatter.format(split(s1), split(s2), el);
        assertThat(f.s1).isEqualTo("aaa\nbbb");
        assertThat(f.s2).isEqualTo("aaa\nbbb");

        s1 = "aaa\n";
        s2 = "aaa\nbbb";
        el = hd.diff(RawTextComparator.DEFAULT, rawtext(s1), rawtext(s2));
        f = LineBasedDiffBiFormatter.format(split(s1), split(s2), el);
        assertThat(f.s1).isEqualTo("aaa\n-bbb-");
        assertThat(f.s2).isEqualTo("aaa\nbbb");

        s1 = "aaa\nbbb";
        s2 = "aaa\n";
        el = hd.diff(RawTextComparator.DEFAULT, rawtext(s1), rawtext(s2));
        f = LineBasedDiffBiFormatter.format(split(s1), split(s2), el);
        assertThat(f.s1).isEqualTo("aaa\nbbb");
        assertThat(f.s2).isEqualTo("aaa\n-bbb-");

        s1 = "aaa\nbbb";
        s2 = "aaa\nccc";
        el = hd.diff(RawTextComparator.DEFAULT, rawtext(s1), rawtext(s2));
        f = LineBasedDiffBiFormatter.format(split(s1), split(s2), el);
        assertThat(f.s1).isEqualTo("aaa\n-bbb-");
        assertThat(f.s2).isEqualTo("aaa\n+ccc+");

        s1 = "aaa\nbbb\n";
        s2 = "aaa\nbbb\nccc";
        el = hd.diff(RawTextComparator.DEFAULT, rawtext(s1), rawtext(s2));
        f = LineBasedDiffBiFormatter.format(split(s1), split(s2), el);
        assertThat(f.s1).isEqualTo("aaa\nbbb\n-ccc-");
        assertThat(f.s2).isEqualTo("aaa\nbbb\nccc");

        s1 = "aaa\nbbb\nccc";
        s2 = "aaa\nbbb\n";
        el = hd.diff(RawTextComparator.DEFAULT, rawtext(s1), rawtext(s2));
        f = LineBasedDiffBiFormatter.format(split(s1), split(s2), el);
        assertThat(f.s1).isEqualTo("aaa\nbbb\nccc");
        assertThat(f.s2).isEqualTo("aaa\nbbb\n-ccc-");

        s1 = "aaa\nbbb\nccc";
        s2 = "aaa\nbbb\nddd";
        el = hd.diff(RawTextComparator.DEFAULT, rawtext(s1), rawtext(s2));
        f = LineBasedDiffBiFormatter.format(split(s1), split(s2), el);
        assertThat(f.s1).isEqualTo("aaa\nbbb\n-ccc-");
        assertThat(f.s2).isEqualTo("aaa\nbbb\n+ddd+");
    }

    @Test
    public void testLineBasedDiffFormatter2() {
        final HistogramDiff hd = new HistogramDiff();
        String s1 = "";
        String s2 = "aaa\nbbb\n";
        EditList el = hd.diff(RawTextComparator.DEFAULT, rawtext(s1), rawtext(s2));
        LineBasedDiffBiFormatted f = LineBasedDiffBiFormatter.format(split(s1), split(s2), el);
        assertThat(f.s1).isEqualTo("-aaa-\n-bbb-");
        assertThat(f.s2).isEqualTo("aaa\nbbb");

        s1 = "aaa\nbbb\n";
        s2 = "";
        el = hd.diff(RawTextComparator.DEFAULT, rawtext(s1), rawtext(s2));
        f = LineBasedDiffBiFormatter.format(split(s1), split(s2), el);
        assertThat(f.s1).isEqualTo("aaa\nbbb");
        assertThat(f.s2).isEqualTo("-aaa-\n-bbb-");

        s1 = "aaa\nbbb\n";
        s2 = "ccc\nddd\n";
        el = hd.diff(RawTextComparator.DEFAULT, rawtext(s1), rawtext(s2));
        f = LineBasedDiffBiFormatter.format(split(s1), split(s2), el);
        assertThat(f.s1).isEqualTo("-aaa-\n-bbb-");
        assertThat(f.s2).isEqualTo("+ccc+\n+ddd+");

        s1 = "aaa\n";
        s2 = "aaa\nbbb\nccc";
        el = hd.diff(RawTextComparator.DEFAULT, rawtext(s1), rawtext(s2));
        f = LineBasedDiffBiFormatter.format(split(s1), split(s2), el);
        assertThat(f.s1).isEqualTo("aaa\n-bbb-\n-ccc-");
        assertThat(f.s2).isEqualTo("aaa\nbbb\nccc");

        s1 = "aaa\nbbb\nccc";
        s2 = "aaa\n";
        el = hd.diff(RawTextComparator.DEFAULT, rawtext(s1), rawtext(s2));
        f = LineBasedDiffBiFormatter.format(split(s1), split(s2), el);
        assertThat(f.s1).isEqualTo("aaa\nbbb\nccc");
        assertThat(f.s2).isEqualTo("aaa\n-bbb-\n-ccc-");

        s1 = "aaa\nbbb\nccc";
        s2 = "aaa\nBBB\nCCC";
        el = hd.diff(RawTextComparator.DEFAULT, rawtext(s1), rawtext(s2));
        f = LineBasedDiffBiFormatter.format(split(s1), split(s2), el);
        assertThat(f.s1).isEqualTo("aaa\n-bbb-\n-ccc-");
        assertThat(f.s2).isEqualTo("aaa\n+BBB+\n+CCC+");

        s1 = "aaa\nbbb\nccc";
        s2 = "aaa\nbbb\nccc";
        el = hd.diff(RawTextComparator.DEFAULT, rawtext(s1), rawtext(s2));
        f = LineBasedDiffBiFormatter.format(split(s1), split(s2), el);
        assertThat(f.s1).isEqualTo("aaa\nbbb\nccc");
        assertThat(f.s2).isEqualTo("aaa\nbbb\nccc");

        s1 = "aaa\nbbb\n";
        s2 = "aaa\nbbb\nccc\nddd";
        el = hd.diff(RawTextComparator.DEFAULT, rawtext(s1), rawtext(s2));
        f = LineBasedDiffBiFormatter.format(split(s1), split(s2), el);
        assertThat(f.s1).isEqualTo("aaa\nbbb\n-ccc-\n-ddd-");
        assertThat(f.s2).isEqualTo("aaa\nbbb\nccc\nddd");

        s1 = "aaa\nbbb\nccc\nddd";
        s2 = "aaa\nbbb\n";
        el = hd.diff(RawTextComparator.DEFAULT, rawtext(s1), rawtext(s2));
        f = LineBasedDiffBiFormatter.format(split(s1), split(s2), el);
        assertThat(f.s1).isEqualTo("aaa\nbbb\nccc\nddd");
        assertThat(f.s2).isEqualTo("aaa\nbbb\n-ccc-\n-ddd-");

        s1 = "aaa\nbbb\nccc\nddd";
        s2 = "aaa\nbbb\nCCC\nDDD";
        el = hd.diff(RawTextComparator.DEFAULT, rawtext(s1), rawtext(s2));
        f = LineBasedDiffBiFormatter.format(split(s1), split(s2), el);
        assertThat(f.s1).isEqualTo("aaa\nbbb\n-ccc-\n-ddd-");
        assertThat(f.s2).isEqualTo("aaa\nbbb\n+CCC+\n+DDD+");
    }

    @Test
    public void testLineBasedDiffFormatter3() {
        final HistogramDiff hd = new HistogramDiff();
        String s1 = "";
        String s2 = "aaa\nbbb\nccc\n";
        EditList el = hd.diff(RawTextComparator.DEFAULT, rawtext(s1), rawtext(s2));
        LineBasedDiffBiFormatted f = LineBasedDiffBiFormatter.format(split(s1), split(s2), el);
        assertThat(f.s1).isEqualTo("-aaa-\n-bbb-\n-ccc-");
        assertThat(f.s2).isEqualTo("aaa\nbbb\nccc");

        s1 = "aaa\nbbb\nccc\n";
        s2 = "";
        el = hd.diff(RawTextComparator.DEFAULT, rawtext(s1), rawtext(s2));
        f = LineBasedDiffBiFormatter.format(split(s1), split(s2), el);
        assertThat(f.s1).isEqualTo("aaa\nbbb\nccc");
        assertThat(f.s2).isEqualTo("-aaa-\n-bbb-\n-ccc-");

        s1 = "aaa\nbbb\nccc\n";
        s2 = "ddd\neee\nfff\n";
        el = hd.diff(RawTextComparator.DEFAULT, rawtext(s1), rawtext(s2));
        f = LineBasedDiffBiFormatter.format(split(s1), split(s2), el);
        assertThat(f.s1).isEqualTo("-aaa-\n-bbb-\n-ccc-");
        assertThat(f.s2).isEqualTo("+ddd+\n+eee+\n+fff+");

        s1 = "aaa\n";
        s2 = "aaa\nbbb\nccc\nddd";
        el = hd.diff(RawTextComparator.DEFAULT, rawtext(s1), rawtext(s2));
        f = LineBasedDiffBiFormatter.format(split(s1), split(s2), el);
        assertThat(f.s1).isEqualTo("aaa\n-bbb-\n-ccc-\n-ddd-");
        assertThat(f.s2).isEqualTo("aaa\nbbb\nccc\nddd");

        s1 = "aaa\nbbb\nccc\nddd";
        s2 = "aaa\n";
        el = hd.diff(RawTextComparator.DEFAULT, rawtext(s1), rawtext(s2));
        f = LineBasedDiffBiFormatter.format(split(s1), split(s2), el);
        assertThat(f.s1).isEqualTo("aaa\nbbb\nccc\nddd");
        assertThat(f.s2).isEqualTo("aaa\n-bbb-\n-ccc-\n-ddd-");

        s1 = "aaa\nbbb\nccc\nddd";
        s2 = "aaa\nBBB\nCCC\nDDD";
        el = hd.diff(RawTextComparator.DEFAULT, rawtext(s1), rawtext(s2));
        f = LineBasedDiffBiFormatter.format(split(s1), split(s2), el);
        assertThat(f.s1).isEqualTo("aaa\n-bbb-\n-ccc-\n-ddd-");
        assertThat(f.s2).isEqualTo("aaa\n+BBB+\n+CCC+\n+DDD+");

        s1 = "aaa\nbbb\n";
        s2 = "aaa\nbbb\nccc\nddd\neee";
        el = hd.diff(RawTextComparator.DEFAULT, rawtext(s1), rawtext(s2));
        f = LineBasedDiffBiFormatter.format(split(s1), split(s2), el);
        assertThat(f.s1).isEqualTo("aaa\nbbb\n-ccc-\n-ddd-\n-eee-");
        assertThat(f.s2).isEqualTo("aaa\nbbb\nccc\nddd\neee");

        s1 = "aaa\nbbb\nccc\nddd\neee";
        s2 = "aaa\nbbb\n";
        el = hd.diff(RawTextComparator.DEFAULT, rawtext(s1), rawtext(s2));
        f = LineBasedDiffBiFormatter.format(split(s1), split(s2), el);
        assertThat(f.s1).isEqualTo("aaa\nbbb\nccc\nddd\neee");
        assertThat(f.s2).isEqualTo("aaa\nbbb\n-ccc-\n-ddd-\n-eee-");

        s1 = "aaa\nbbb\nccc\nddd\neee";
        s2 = "aaa\nbbb\nCCC\nDDD\nEEE";
        el = hd.diff(RawTextComparator.DEFAULT, rawtext(s1), rawtext(s2));
        f = LineBasedDiffBiFormatter.format(split(s1), split(s2), el);
        assertThat(f.s1).isEqualTo("aaa\nbbb\n-ccc-\n-ddd-\n-eee-");
        assertThat(f.s2).isEqualTo("aaa\nbbb\n+CCC+\n+DDD+\n+EEE+");
    }
}
