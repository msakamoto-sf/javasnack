package javasnack.snacks.jmh;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;

import javasnack.RunnableSnack;

public class JmhBenchmark implements RunnableSnack {
    @Override
    public void run(String... args) throws Exception {
        if (args.length < 1) {
            System.out.print("Enter JMH args (exit for ENTER):");
            final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            final String readLine = br.readLine();
            if (Objects.isNull(readLine)) {
                return;
            }
            if (readLine.trim().length() == 0) {
                System.err.println("empty args, skip JMH.");
                return;
            }
            args = readLine.trim().split("\\s");
        }
        try {
            org.openjdk.jmh.Main.main(args);
        } catch (Throwable t) {
            if ((t instanceof RuntimeException)
                    && (t.getMessage().contains("Unable to find the resource: /META-INF/BenchmarkList"))) {
                System.err.println("######");
                System.err.println(
                        "###### /META-INF/BenchmarkList not found. try 'mvn clean compile'"
                                + " (=> jmh-generator-annprocess will generate it :)");
                System.err.println(
                        "###### NOTE: Eclipse auto compile nor Project -> Clean does NOT generate that file X(");
                System.err.println("######");
            }
            throw t;
        }
    }
}
