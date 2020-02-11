package javasnack.snacks.jmh;

public class JmhBenchmark {

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < args.length; i++) {
            System.out.println("args[" + i + "] - " + args[i]);
        }
        org.openjdk.jmh.Main.main(args);
    }
}
