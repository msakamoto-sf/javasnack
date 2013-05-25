package javasnack.snacks;

public class RunningJREVersionIs implements Runnable {

    @Override
    public void run() {
        System.out.println(System.getProperty("java.version"));
    }

}
