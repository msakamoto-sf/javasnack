package javasnack.ojcp;

import static org.assertj.core.api.Assertions.assertThat;

import javasnack.tool.StringWriterPrinter;

public class OjcpTools {
    public static void assertLines(StringWriterPrinter swp, String... lines) {
        assertThat(swp.toString()).isEqualToIgnoringNewLines(swp.cat(lines));
    }
}
