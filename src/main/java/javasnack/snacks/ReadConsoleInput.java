package javasnack.snacks;

import java.io.BufferedReader;
import java.io.Console;
import java.io.InputStreamReader;

public class ReadConsoleInput implements Runnable {
    @Override
    public void run() {

        // Traditional STDIN Read
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    System.in));
            System.out.print("prompot:>");
            String input = br.readLine().trim();
            System.out.println("Input = [" + input + "]");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // JDK 1.6 : java.io.Console & System.console()
        Console console = System.console();
        if (null == console) {
            System.out
                    .println("'System.console()' returns null, Console is disabled.");
        } else {
            console.printf("Console Output Example %s%n", "abcdefg");
            String input = console.readLine("[%s %s]>", "abc", "def");
            console.printf("Input = [%s]%n", input);
            char[] password = console.readPassword("input dummy password>");
            console.printf("Input Password = [%s]%n", new String(password));
        }

        System.out.println("(END)");
    }

}
