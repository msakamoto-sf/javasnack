package javasnack.diff.jgit;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;

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
    public void testBasicUsage() {
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
}
