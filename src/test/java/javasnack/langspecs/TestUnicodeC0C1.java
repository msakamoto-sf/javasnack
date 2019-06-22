package javasnack.langspecs;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import javasnack.tool.StreamTool;
import javasnack.tool.UnsignedByte;

public class TestUnicodeC0C1 {

    String getAttrs(int codepoint) {
        StringBuilder sb = new StringBuilder();
        sb.append(Character.isAlphabetic(codepoint) ? "AL" : "__");
        sb.append("-");
        sb.append(Character.isLetter(codepoint) ? "LT" : "__");
        sb.append("-");
        sb.append(Character.isDigit(codepoint) ? "DG" : "__");
        sb.append("-");
        sb.append(Character.isUpperCase(codepoint) ? "UP" : "__");
        sb.append("-");
        sb.append(Character.isLowerCase(codepoint) ? "LO" : "__");
        sb.append("-");
        sb.append(Character.isTitleCase(codepoint) ? "TC" : "__");
        sb.append("-");
        sb.append(Character.isSpaceChar(codepoint) ? "SP" : "__");
        sb.append("-");
        sb.append(Character.isWhitespace(codepoint) ? "WS" : "__");
        sb.append("-");
        sb.append(Character.isISOControl(codepoint) ? "ISOCTL" : "______");
        return sb.toString();
    }

    @Test
    public void test0x00To0xFF() throws IOException {
        final var data = UnsignedByte.create0x00to0xFFString();
        final var sb = new StringBuilder();
        for (int i = 0; i < data.length(); i++) {
            int cp = Character.codePointAt(data, i);
            sb.append("binary:[" + i + "(0x" + Integer.toHexString(i).toUpperCase() + ")],");
            sb.append("attr=[" + getAttrs(cp) + "],");
            sb.append("name=[" + Character.getName(cp) + "]");
            sb.append("\n");
        }
        final var expected = StreamTool.res2str("testdata/unicode_c0_c1_attr_dump.txt");
        assertThat(sb.toString()).isEqualToIgnoringNewLines(expected);
    }

}
