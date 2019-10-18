package javasnack.diff;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.github.difflib.algorithm.DiffException;
import com.github.difflib.algorithm.jgit.HistogramDiff;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Chunk;
import com.github.difflib.patch.Patch;

/**
 * java-diff-utils の JGit Histogram diff アルゴリズムの使い方のデモ
 */
/* see:
 * - https://github.com/java-diff-utils/java-diff-utils
 * - https://java-diff-utils.github.io/java-diff-utils/
 */
public class TestJavaDiffUtilsJGitHistogramDiffDemo {

    static Stream<Arguments> provideBasicDemoUsageArguments() {
        // @formatter:off
        return Stream.of(
                arguments("", "aaa", "aaa", ""),
                arguments("aaa", "", "", "aaa"),
                arguments("aaa", "aaa", "", ""),
                arguments("aaa", "aaaa", "a", ""),
                arguments("aaaa", "aaa", "", "a"),
                arguments("abcdefg", "bdegh", "h", "acf"),
                arguments("aaa", "bbb", "bbb", "aaa"),
                arguments("aaa\nbbb\nccc", "bbb\nccd\neee", "d\neee", "aaa\nc"),
                arguments("", "", "", ""));
        // @formatter:on
    }

    @ParameterizedTest
    @MethodSource("provideBasicDemoUsageArguments")
    public void testBasicDemoUsage(
            final String left,
            final String right,
            final String expectedDeleted,
            final String expectedInserted) throws DiffException {
        final List<String> origList = new ArrayList<>();
        final List<String> revList = new ArrayList<>();
        for (Character character : right.toCharArray()) {
            origList.add(character.toString());
        }
        for (Character character : left.toCharArray()) {
            revList.add(character.toString());
        }

        // JGit Histogram diff
        final Patch<String> patch = Patch.generate(origList, revList,
                new HistogramDiff<String>().computeDiff(origList, revList, null));
        final List<AbstractDelta<String>> deltas = patch.getDeltas();
        final StringBuilder inserted = new StringBuilder();
        final StringBuilder deleted = new StringBuilder();
        for (AbstractDelta<String> d : deltas) {
            final Chunk<String> src = d.getSource();
            final Chunk<String> target = d.getTarget();
            switch (d.getType()) {
            case CHANGE:
                deleted.append(String.join("", src.getLines()));
                inserted.append(String.join("", target.getLines()));
                break;
            case DELETE:
                deleted.append(String.join("", src.getLines()));
                break;
            case EQUAL:
                break;
            case INSERT:
                inserted.append(String.join("", target.getLines()));
                break;
            default:
                break;
            }
        }
        assertThat(inserted.toString()).isEqualTo(expectedInserted);
        assertThat(deleted.toString()).isEqualTo(expectedDeleted);
    }

    String diffstr(final Patch<String> patch) {
        final StringBuilder r = new StringBuilder();
        final List<AbstractDelta<String>> deltas = patch.getDeltas();
        for (AbstractDelta<String> d : deltas) {
            final Chunk<String> src = d.getSource();
            final Chunk<String> target = d.getTarget();
            switch (d.getType()) {
            case CHANGE:
                r.append("+" + String.join("", src.getLines()) + "+");
                r.append("-" + String.join("", target.getLines()) + "-");
                break;
            case DELETE:
                r.append("+" + String.join("", src.getLines()) + "+");
                break;
            case EQUAL:
                break;
            case INSERT:
                r.append("-" + String.join("", target.getLines()) + "-");
                break;
            default:
                break;
            }
        }
        return r.toString();
    }

    static Stream<Arguments> provideDiffResultsArguments() {
        // @formatter:off
        return Stream.of(
                arguments("", "aaa", "+aaa+"),
                arguments("aaa", "", "-aaa-"),
                arguments("aaa", "bbb", "+bbb+-aaa-"),
                arguments("aaa", "aaa", ""),
                arguments("aaa\nbbb", "aaa\nbbb", ""),
                arguments("aaa\n", "aaa\nbbb", "+bbb+"),
                arguments("aaa\nbbb", "aaa\n", "-bbb-"),
                arguments("aaa\nbbb", "aaa\nccc", "+ccc+-bbb-"),
                arguments("aaa\nbbb\n", "aaa\nbbb\nccc", "+ccc+"),
                arguments("aaa\nbbb\nccc", "aaa\nbbb\n", "-ccc-"),
                arguments("aaa\nbbb\nccc", "aaa\nbbb\nddd", "+ddd+-ccc-"),
                arguments("", "aaa\nbbb\n", "+aaa\nbbb\n+"),
                arguments("aaa\nbbb\n", "", "-aaa\nbbb\n-"),
                arguments("aaa\nbbb\n", "ccc\nddd\n", "+ccc+-aaa-+ddd+-bbb-"),
                arguments("aaa\n", "aaa\nbbb\nccc", "+bbb\nccc+"),
                arguments("aaa\nbbb\nccc", "aaa\n", "-bbb\nccc-"),
                arguments("aaa\nbbb\nccc", "aaa\nBBB\nCCC", "+BBB+-bbb-+CCC+-ccc-"),
                arguments("aaa\nbbb\nccc", "aaa\nbbb\nccc", ""),
                arguments("aaa\nbbb\n", "aaa\nbbb\nccc\nddd", "+ccc\nddd+"),
                arguments("aaa\nbbb\nccc\nddd", "aaa\nbbb\n", "-ccc\nddd-"),
                arguments("aaa\nbbb\nccc\nddd", "aaa\nbbb\nCCC\nDDD", "+CCC+-ccc-+DDD+-ddd-"),
                arguments("", "aaa\nbbb\nccc\n", "+aaa\nbbb\nccc\n+"),
                arguments("aaa\nbbb\nccc\n", "", "-aaa\nbbb\nccc\n-"),
                arguments("aaa\nbbb\nccc\n", "ddd\neee\nfff\n", "+ddd+-aaa-+eee+-bbb-+fff+-ccc-"),
                arguments("aaa\n", "aaa\nbbb\nccc\nddd", "+bbb\nccc\nddd+"),
                arguments("aaa\nbbb\nccc\nddd", "aaa\n", "-bbb\nccc\nddd-"),
                arguments("aaa\nbbb\nccc\nddd", "aaa\nBBB\nCCC\nDDD", "+BBB+-bbb-+CCC+-ccc-+DDD+-ddd-"),
                arguments("aaa\nbbb\n", "aaa\nbbb\nccc\nddd\neee", "+ccc\nddd\neee+"),
                arguments("aaa\nbbb\nccc\nddd\neee", "aaa\nbbb\n", "-ccc\nddd\neee-"),
                arguments("aaa\nbbb\nccc\nddd\neee", "aaa\nbbb\nCCC\nDDD\nEEE", "+CCC+-ccc-+DDD+-ddd-+EEE+-eee-"),
                arguments("bbb", "AAA\nbbb", "+AAA\n+"),
                arguments("AAA\nbbb\n", "bbb\n", "-AAA\n-"),
                arguments("AAA\nbbb\n", "BBB\nbbb\n", "+BBB+-AAA-"),
                arguments("aaa\nbbb", "aaa\nAAA\nbbb", "+AAA\n+"),
                arguments("aaa\nAAA\nbbb\n", "aaa\nbbb\n", "-AAA\n-"),
                arguments("aaa\nAAA\nbbb\n", "aaa\nBBB\nbbb\n", "+BBB+-AAA-"),
                arguments("aaa\nbbb\nccc\n", "aaa\nbbb\nBBB\nccc\n", "+BBB\n+"),
                arguments("aaa\nbbb\nBBB\nccc\n", "aaa\nbbb\nccc\n", "-BBB\n-"),
                arguments("aaa\nbbb\nAAA\nccc\n", "aaa\nbbb\nBBB\nccc\n", "+BBB+-AAA-"),
                arguments("bbb", "AAA\nBBB\nbbb", "+AAA\nBBB\n+"),
                arguments("AAA\nBBB\nbbb\n", "bbb\n", "-AAA\nBBB\n-"),
                arguments("AAA\nBBB\nbbb\n", "CCC\nDDD\nbbb\n", "+CCC+-AAA-+DDD+-BBB-"),
                arguments("aaa\nbbb", "aaa\nAAA\nBBB\nbbb", "+AAA\nBBB\n+"),
                arguments("aaa\nAAA\nBBB\nbbb\n", "aaa\nbbb\n", "-AAA\nBBB\n-"),
                arguments("aaa\nAAA\nBBB\nbbb\n", "aaa\nCCC\nDDD\nbbb\n", "+CCC+-AAA-+DDD+-BBB-"),
                arguments("aaa\nbbb\nccc\n", "aaa\nbbb\nBBB\nCCC\nccc\n", "+BBB\nCCC\n+"),
                arguments("aaa\nbbb\nBBB\nCCC\nccc\n", "aaa\nbbb\nccc\n", "-BBB\nCCC\n-"),
                arguments("aaa\nbbb\nAAA\nBBB\nccc\n", "aaa\nbbb\nCCC\nDDD\nccc\n", "+CCC+-AAA-+DDD+-BBB-"),
                arguments("bbb", "AAA\nBBB\nCCC\nbbb", "+AAA\nBBB\nCCC\n+"),
                arguments("AAA\nBBB\nCCC\nbbb\n", "bbb\n", "-AAA\nBBB\nCCC\n-"),
                arguments("AAA\nBBB\nCCC\nbbb\n", "DDD\nEEE\nFFF\nbbb\n",
                        "+DDD+-AAA-+EEE+-BBB-+FFF+-CCC-"),
                arguments("aaa\nbbb", "aaa\nAAA\nBBB\nCCC\nbbb", "+AAA\nBBB\nCCC\n+"),
                arguments("aaa\nAAA\nBBB\nCCC\nbbb\n", "aaa\nbbb\n", "-AAA\nBBB\nCCC\n-"),
                arguments(
                        "aaa\nAAA\nBBB\nCCC\nbbb\n", "aaa\nDDD\nEEE\nFFF\nbbb\n",
                        "+DDD+-AAA-+EEE+-BBB-+FFF+-CCC-"),
                arguments(
                        "aaa\nbbb\nccc\n", "aaa\nbbb\nBBB\nCCC\nDDD\nccc\n",
                        "+BBB\nCCC\nDDD\n+"),
                arguments(
                        "aaa\nbbb\nBBB\nCCC\nDDD\nccc\n", "aaa\nbbb\nccc\n",
                        "-BBB\nCCC\nDDD\n-"),
                arguments(
                        "aaa\nbbb\nAAA\nBBB\nCCC\nccc\n", "aaa\nbbb\nDDD\nEEE\nFFF\nccc\n",
                        "+DDD+-AAA-+EEE+-BBB-+FFF+-CCC-"),
                arguments("aaa\n", "XXX\naaa\nYYY\n", "+XXX\n++YYY\n+"),
                arguments("XXX\naaa\nYYY\n", "aaa\n", "-XXX\n--YYY\n-"),
                arguments("XXX\naaa\nYYY\n", "xxx\naaa\nyyy\n", "+xxx+-XXX-+yyy+-YYY-"),
                arguments("aaa\nbbb\n", "aaa\nXXX\nbbb\nYYY\n", "+XXX\n++YYY\n+"),
                arguments("aaa\nXXX\nbbb\nYYY\n", "aaa\nbbb\n", "-XXX\n--YYY\n-"),
                arguments(
                        "aaa\nXXX\nbbb\nYYY\n", "aaa\nxxx\nbbb\nyyy\n",
                        "+xxx+-XXX-+yyy+-YYY-"),
                arguments(
                        "aaa\nbbb\nccc\nddd\neee\n",
                        "aaa\nbbb\nXXX\nccc\nYYY\nZZZ\nddd\neee\n",
                        "+XXX\n++YYY\nZZZ\n+"),
                arguments(
                        "aaa\nbbb\nXXX\nccc\nYYY\nZZZ\nddd\neee\n",
                        "aaa\nbbb\nccc\nddd\neee\n",
                        "-XXX\n--YYY\nZZZ\n-"),
                arguments(
                        "aaa\nbbb\nXXX\nccc\nYYY\nZZZ\nddd\neee\n",
                        "aaa\nbbb\nxxx\nccc\nyyy\nzzz\nddd\neee\n",
                        "+xxx+-XXX-+yyy+-YYY-+zzz+-ZZZ-"),
                arguments(
                        "aaa\nbbb\nccc\nddd\neee\nfff\n",
                        "bbb\nccc\nXXX\nYYY\neee\nZZZ",
                        "-aaa\n-+XXX\nYYY+-ddd-+ZZZ+-fff\n-"),
                arguments(
                        "aaa\r\nbbb\nccc\n",
                        "aaa\nbbb\r\nccc",
                        "+\nbbb+-bbb\n--\n-"),
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
                        "+AAA+-aaa-"
                        + "+BBB+-bbb-"
                        + "+CCC+-ccc-"
                        + "+DDD+-ddd--eee\n"
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
                        + "uuu-"),
                arguments("", "", ""));
        // @formatter:on
    }

    @ParameterizedTest
    @MethodSource("provideDiffResultsArguments")
    public void testDiffResults(
            final String left,
            final String right,
            final String patched) throws DiffException {
        final List<String> origList = new ArrayList<>();
        final List<String> revList = new ArrayList<>();
        for (Character character : right.toCharArray()) {
            origList.add(character.toString());
        }
        for (Character character : left.toCharArray()) {
            revList.add(character.toString());
        }

        // JGit Histogram diff
        final Patch<String> patch = Patch.generate(origList, revList,
                new HistogramDiff<String>().computeDiff(origList, revList, null));

        assertThat(diffstr(patch)).isEqualTo(patched);
    }

}
