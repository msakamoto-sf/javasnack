package javasnack.diff;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.stream.Stream;

import org.apache.commons.text.diff.CommandVisitor;
import org.apache.commons.text.diff.EditScript;
import org.apache.commons.text.diff.StringsComparator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Apache Commons Text の diff パッケージの使い方のデモ
 */
/* see:
 * - http://commons.apache.org/proper/commons-text/
 * - http://commons.apache.org/proper/commons-text/javadocs/api-release/index.html
 */
public class TestCommonsTextMyersDiffDemo {

    EditScript<Character> diff(final String left, final String right) {
        StringsComparator cmp = new StringsComparator(left, right);
        return cmp.getScript();
    }

    static class DiffVisitor implements CommandVisitor<Character> {

        StringBuilder inserted = new StringBuilder();
        StringBuilder kept = new StringBuilder();
        StringBuilder deleted = new StringBuilder();

        @Override
        public void visitInsertCommand(Character c) {
            inserted.append(c);
        }

        @Override
        public void visitKeepCommand(Character c) {
            kept.append(c);
        }

        @Override
        public void visitDeleteCommand(Character c) {
            deleted.append(c);
        }

        public void reset() {
            inserted = new StringBuilder();
            kept = new StringBuilder();
            deleted = new StringBuilder();
        }

        public void assertStrings(final String insertedChars, final String keptChars, final String deletedChars) {
            assertThat(inserted.toString()).isEqualTo(insertedChars);
            assertThat(kept.toString()).isEqualTo(keptChars);
            assertThat(deleted.toString()).isEqualTo(deletedChars);
        }
    }

    static Stream<Arguments> provideBasicDemoUsageArguments() {
        // @formatter:off
        return Stream.of(
                arguments("", "aaa", "aaa", "", ""),
                arguments("aaa", "", "", "", "aaa"),
                arguments("aaa", "aaa", "", "aaa", ""),
                arguments("aaa", "aaaa", "a", "aaa", ""),
                arguments("aaaa", "aaa", "", "aaa", "a"),
                arguments("abcdefg", "bdegh", "h", "bdeg", "acf"),
                arguments("aaa", "bbb", "bbb", "", "aaa"),
                arguments("aaa\nbbb\nccc", "bbb\nccd\neee", "d\neee", "bbb\ncc", "aaa\nc"),
                arguments("", "", "", "", ""));
        // @formatter:on
    }

    @ParameterizedTest
    @MethodSource("provideBasicDemoUsageArguments")
    public void testBasicDemoUsage(
            final String left,
            final String right,
            final String expectedInserted,
            final String expectedKept,
            final String expectedDeleted) {
        final DiffVisitor visitor = new DiffVisitor();
        EditScript<Character> script = diff(left, right);
        script.visit(visitor);
        visitor.assertStrings(expectedInserted, expectedKept, expectedDeleted);
    }

    enum EditStatus {
        INSERTING, KEEPING, DELETING;
    }

    static class DiffLeftRightPatchedVisitor implements CommandVisitor<Character> {
        final StringBuilder patched = new StringBuilder();
        EditStatus editStatus = EditStatus.KEEPING;

        @Override
        public void visitInsertCommand(Character c) {
            switch (editStatus) {
            case KEEPING:
                patched.append("+");
                break;
            case DELETING:
                patched.append("-");
                patched.append("+");
                break;
            default:
            }
            editStatus = EditStatus.INSERTING;
            patched.append(c);
        }

        @Override
        public void visitKeepCommand(Character c) {
            switch (editStatus) {
            case INSERTING:
                patched.append("+");
                break;
            case DELETING:
                patched.append("-");
                break;
            default:
            }
            editStatus = EditStatus.KEEPING;
            patched.append(c);
        }

        @Override
        public void visitDeleteCommand(Character c) {
            switch (editStatus) {
            case INSERTING:
                patched.append("+");
                patched.append("-");
                break;
            case KEEPING:
                patched.append("-");
                break;
            default:
            }
            editStatus = EditStatus.DELETING;
            patched.append(c);
        }

        @Override
        public String toString() {
            switch (editStatus) {
            case INSERTING:
                patched.append("+");
                break;
            case DELETING:
                patched.append("-");
                break;
            default:
            }
            return patched.toString();
        }
    }

    static Stream<Arguments> provideDiffPatchedVisitorArguments() {
        // @formatter:off
        return Stream.of(
                arguments("", "aaa", "+aaa+"),
                arguments("aaa", "", "-aaa-"),
                arguments("aaa", "bbb", "+bbb+-aaa-"),
                arguments("aaa", "aaa", "aaa"),
                arguments("aaa\nbbb", "aaa\nbbb", "aaa\nbbb"),
                arguments("aaa\n", "aaa\nbbb", "aaa\n+bbb+"),
                arguments("aaa\nbbb", "aaa\n", "aaa\n-bbb-"),
                arguments("aaa\nbbb", "aaa\nccc", "aaa\n+ccc+-bbb-"),
                arguments("aaa\nbbb\n", "aaa\nbbb\nccc", "aaa\nbbb\n+ccc+"),
                arguments("aaa\nbbb\nccc", "aaa\nbbb\n", "aaa\nbbb\n-ccc-"),
                arguments("aaa\nbbb\nccc", "aaa\nbbb\nddd", "aaa\nbbb\n+ddd+-ccc-"),
                arguments("", "aaa\nbbb\n", "+aaa\nbbb\n+"),
                arguments("aaa\nbbb\n", "", "-aaa\nbbb\n-"),
                arguments("aaa\nbbb\n", "ccc\nddd\n", "+ccc+-aaa-\n+ddd+-bbb-\n"),
                arguments("aaa\n", "aaa\nbbb\nccc", "aaa+\nbbb+\n+ccc+"),
                arguments("aaa\nbbb\nccc", "aaa\n", "aaa-\nbbb-\n-ccc-"),
                arguments("aaa\nbbb\nccc", "aaa\nBBB\nCCC", "aaa\n+BBB+-bbb-\n+CCC+-ccc-"),
                arguments("aaa\nbbb\nccc", "aaa\nbbb\nccc", "aaa\nbbb\nccc"),
                arguments("aaa\nbbb\n", "aaa\nbbb\nccc\nddd", "aaa\nbbb+\nccc+\n+ddd+"),
                arguments("aaa\nbbb\nccc\nddd", "aaa\nbbb\n", "aaa\nbbb-\nccc-\n-ddd-"),
                arguments("aaa\nbbb\nccc\nddd", "aaa\nbbb\nCCC\nDDD", "aaa\nbbb\n+CCC+-ccc-\n+DDD+-ddd-"),
                arguments("", "aaa\nbbb\nccc\n", "+aaa\nbbb\nccc\n+"),
                arguments("aaa\nbbb\nccc\n", "", "-aaa\nbbb\nccc\n-"),
                arguments("aaa\nbbb\nccc\n", "ddd\neee\nfff\n", "+ddd+-aaa-\n+eee+-bbb-\n+fff+-ccc-\n"),
                arguments("aaa\n", "aaa\nbbb\nccc\nddd", "aaa+\nbbb\nccc+\n+ddd+"),
                arguments("aaa\nbbb\nccc\nddd", "aaa\n", "aaa-\nbbb\nccc-\n-ddd-"),
                arguments("aaa\nbbb\nccc\nddd", "aaa\nBBB\nCCC\nDDD", "aaa\n+BBB+-bbb-\n+CCC+-ccc-\n+DDD+-ddd-"),
                arguments("aaa\nbbb\n", "aaa\nbbb\nccc\nddd\neee", "aaa\nbbb+\nccc\nddd+\n+eee+"),
                arguments("aaa\nbbb\nccc\nddd\neee", "aaa\nbbb\n", "aaa\nbbb-\nccc\nddd-\n-eee-"),
                arguments("aaa\nbbb\nccc\nddd\neee", "aaa\nbbb\nCCC\nDDD\nEEE", "aaa\nbbb\n+CCC+-ccc-\n+DDD+-ddd-\n+EEE+-eee-"),
                arguments("bbb", "AAA\nbbb", "+AAA\n+bbb"),
                arguments("AAA\nbbb\n", "bbb\n", "-AAA\n-bbb\n"),
                arguments("AAA\nbbb\n", "BBB\nbbb\n", "+BBB+-AAA-\nbbb\n"),
                arguments("aaa\nbbb", "aaa\nAAA\nbbb", "aaa+\nAAA+\nbbb"),
                arguments("aaa\nAAA\nbbb\n", "aaa\nbbb\n", "aaa-\nAAA-\nbbb\n"),
                arguments("aaa\nAAA\nbbb\n", "aaa\nBBB\nbbb\n", "aaa\n+BBB+-AAA-\nbbb\n"),
                arguments("aaa\nbbb\nccc\n", "aaa\nbbb\nBBB\nccc\n", "aaa\nbbb+\nBBB+\nccc\n"),
                arguments("aaa\nbbb\nBBB\nccc\n", "aaa\nbbb\nccc\n", "aaa\nbbb-\nBBB-\nccc\n"),
                arguments("aaa\nbbb\nAAA\nccc\n", "aaa\nbbb\nBBB\nccc\n", "aaa\nbbb\n+BBB+-AAA-\nccc\n"),
                arguments("bbb", "AAA\nBBB\nbbb", "+AAA\nBBB\n+bbb"),
                arguments("AAA\nBBB\nbbb\n", "bbb\n", "-AAA\nBBB\n-bbb\n"),
                arguments("AAA\nBBB\nbbb\n", "CCC\nDDD\nbbb\n", "+CCC+-AAA-\n+DDD+-BBB-\nbbb\n"),
                arguments("aaa\nbbb", "aaa\nAAA\nBBB\nbbb", "aaa+\nAAA+\n+BBB\n+bbb"),
                arguments("aaa\nAAA\nBBB\nbbb\n", "aaa\nbbb\n", "aaa-\nAAA-\n-BBB\n-bbb\n"),
                arguments("aaa\nAAA\nBBB\nbbb\n", "aaa\nCCC\nDDD\nbbb\n", "aaa\n+CCC+-AAA-\n+DDD+-BBB-\nbbb\n"),
                arguments("aaa\nbbb\nccc\n", "aaa\nbbb\nBBB\nCCC\nccc\n", "aaa\nbbb+\nBBB+\n+CCC\n+ccc\n"),
                arguments("aaa\nbbb\nBBB\nCCC\nccc\n", "aaa\nbbb\nccc\n", "aaa\nbbb-\nBBB-\n-CCC\n-ccc\n"),
                arguments("aaa\nbbb\nAAA\nBBB\nccc\n", "aaa\nbbb\nCCC\nDDD\nccc\n", "aaa\nbbb\n+CCC+-AAA-\n+DDD+-BBB-\nccc\n"),
                arguments("bbb", "AAA\nBBB\nCCC\nbbb", "+AAA\nBBB\nCCC\n+bbb"),
                arguments("AAA\nBBB\nCCC\nbbb\n", "bbb\n", "-AAA\nBBB\nCCC\n-bbb\n"),
                arguments("AAA\nBBB\nCCC\nbbb\n", "DDD\nEEE\nFFF\nbbb\n",
                        "+DDD+-AAA-\n+EEE+-BBB-\n+FFF+-CCC-\nbbb\n"),
                arguments("aaa\nbbb", "aaa\nAAA\nBBB\nCCC\nbbb", "aaa+\nAAA\nBBB\nCCC+\nbbb"),
                arguments("aaa\nAAA\nBBB\nCCC\nbbb\n", "aaa\nbbb\n", "aaa-\nAAA\nBBB\nCCC-\nbbb\n"),
                arguments(
                        "aaa\nAAA\nBBB\nCCC\nbbb\n", "aaa\nDDD\nEEE\nFFF\nbbb\n",
                        "aaa\n+DDD+-AAA-\n+EEE+-BBB-\n+FFF+-CCC-\nbbb\n"),
                arguments(
                        "aaa\nbbb\nccc\n", "aaa\nbbb\nBBB\nCCC\nDDD\nccc\n",
                        "aaa\nbbb+\nBBB\nCCC\nDDD+\nccc\n"),
                arguments(
                        "aaa\nbbb\nBBB\nCCC\nDDD\nccc\n", "aaa\nbbb\nccc\n",
                        "aaa\nbbb-\nBBB\nCCC\nDDD-\nccc\n"),
                arguments(
                        "aaa\nbbb\nAAA\nBBB\nCCC\nccc\n", "aaa\nbbb\nDDD\nEEE\nFFF\nccc\n",
                        "aaa\nbbb\n+DDD+-AAA-\n+EEE+-BBB-\n+FFF+-CCC-\nccc\n"),
                arguments("aaa\n", "XXX\naaa\nYYY\n", "+XXX\n+aaa\n+YYY\n+"),
                arguments("XXX\naaa\nYYY\n", "aaa\n", "-XXX\n-aaa\n-YYY\n-"),
                arguments("XXX\naaa\nYYY\n", "xxx\naaa\nyyy\n", "+xxx+-XXX-\naaa\n+yyy+-YYY-\n"),
                arguments("aaa\nbbb\n", "aaa\nXXX\nbbb\nYYY\n", "aaa+\nXXX+\nbbb\n+YYY\n+"),
                arguments("aaa\nXXX\nbbb\nYYY\n", "aaa\nbbb\n", "aaa-\nXXX-\nbbb\n-YYY\n-"),
                arguments(
                        "aaa\nXXX\nbbb\nYYY\n", "aaa\nxxx\nbbb\nyyy\n",
                        "aaa\n+xxx+-XXX-\nbbb\n+yyy+-YYY-\n"),
                arguments(
                        "aaa\nbbb\nccc\nddd\neee\n",
                        "aaa\nbbb\nXXX\nccc\nYYY\nZZZ\nddd\neee\n",
                        "aaa\nbbb+\nXXX+\nccc+\nYYY\nZZZ+\nddd\neee\n"),
                arguments(
                        "aaa\nbbb\nXXX\nccc\nYYY\nZZZ\nddd\neee\n",
                        "aaa\nbbb\nccc\nddd\neee\n",
                        "aaa\nbbb-\nXXX-\nccc-\nYYY\nZZZ-\nddd\neee\n"),
                arguments(
                        "aaa\nbbb\nXXX\nccc\nYYY\nZZZ\nddd\neee\n",
                        "aaa\nbbb\nxxx\nccc\nyyy\nzzz\nddd\neee\n",
                        "aaa\nbbb\n+xxx+-XXX-\nccc\n+yyy+-YYY-\n+zzz+-ZZZ-\nddd\neee\n"),
                arguments(
                        "aaa\nbbb\nccc\nddd\neee\nfff\n",
                        "bbb\nccc\nXXX\nYYY\neee\nZZZ",
                        "-aaa\n-bbb\nccc+\nXXX+\n+YYY+-ddd-\neee\n+ZZZ+-fff\n-"),
                arguments(
                        "aaa\r\nbbb\nccc\n",
                        "aaa\nbbb\r\nccc",
                        "aaa-\r-\nbbb+\r+\nccc-\n-"),
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
                        "+AAA+-aaa-\n"
                        + "+BBB+-bbb-\n"
                        + "+CCC+-ccc-\n"
                        + "+DDD+-ddd\n"
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
                        + "ooo-\n"
                        + "-ppp\n"
                        + "qqq\n"
                        + "rrr-\n"
                        + "-sss\n"
                        + "ttt\n"
                        + "uuu-"),
                arguments("", "", ""));
        // @formatter:on
    }

    @ParameterizedTest
    @MethodSource("provideDiffPatchedVisitorArguments")
    public void testDiffPatchedVisitor(
            final String left,
            final String right,
            final String patched) {
        final DiffLeftRightPatchedVisitor visitor = new DiffLeftRightPatchedVisitor();
        EditScript<Character> script = diff(left, right);
        script.visit(visitor);
        assertThat(visitor.toString()).isEqualTo(patched);
    }

}
