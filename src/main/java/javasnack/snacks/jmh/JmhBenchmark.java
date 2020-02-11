package javasnack.snacks.jmh;

import javasnack.RunnableSnack;

public class JmhBenchmark implements RunnableSnack {
    @Override
    public void run(String... args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }
}
