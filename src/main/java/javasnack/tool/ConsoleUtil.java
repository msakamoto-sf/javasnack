package javasnack.tool;

import java.io.BufferedReader;
import java.io.Console;
import java.io.InputStreamReader;
import java.util.Objects;

public class ConsoleUtil {
    public static String readLine() {
        return readLine(null);
    }

    public static String readLine(String prompt) {
        final Console c = System.console();
        if (Objects.nonNull(c)) {
            return c.readLine();
        }
        String r = "";
        try {
            final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            if (Objects.nonNull(prompt)) {
                System.out.print(prompt);
            }
            final String readLine = br.readLine();
            if (Objects.isNull(readLine)) {
                return "";
            }
            r = readLine.trim();
        } catch (Exception ignore) {
        }
        return r;
    }
}
