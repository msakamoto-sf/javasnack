package javasnack.ojcp;

import static org.assertj.core.api.Assertions.assertThat;

import javasnack.tool.StringWriterPrinter;

public class OjcpTools {
    public static void assertLines(StringWriterPrinter swp, String... lines) {
        assertThat(swp.toString()).isEqualToIgnoringNewLines(swp.cat(lines));
    }

    public static boolean returnTrue(final StringWriterPrinter swp, final String msg) {
        swp.out.println(msg);
        return true;
    }

    public static boolean returnFalse(final StringWriterPrinter swp, final String msg) {
        swp.out.println(msg);
        return false;
    }
}
