package javasnack.snacks;

import testjar1.Greeting;

public class LocalJarDemo implements Runnable {

    @Override
    public void run() {
        Greeting g = new Greeting();
        System.out.println(g.morning("Load from pom.xml's <systemPath>"));
    }

}
