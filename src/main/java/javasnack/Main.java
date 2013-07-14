package javasnack;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javasnack.snacks.ByteRange1;
import javasnack.snacks.ByteRange2;
import javasnack.snacks.ByteRange3;
import javasnack.snacks.ByteRange4;
import javasnack.snacks.CollectionTypes1;
import javasnack.snacks.HelloWorld;
import javasnack.snacks.LocalJarDemo;
import javasnack.snacks.ListAvailableCharsets;
import javasnack.snacks.LivingUserThreads;
import javasnack.snacks.ReadConsoleInput;
import javasnack.snacks.RunningJREVersionIs;
import javasnack.snacks.SystemPropertiesAndEnvs;
import javasnack.snacks.UUIDDemo;
import javasnack.snacks.perfs.list.PerfArrayListFinePutGet;
import javasnack.snacks.perfs.list.PerfJavaArrayFinePutGet;
import javasnack.snacks.perfs.list.PerfLinkedListFinePutGet;
import javasnack.snacks.perfs.map.PerfHashMapFinePutGet;
import javasnack.snacks.perfs.map.PerfHashMapTotalAvg;
import javasnack.snacks.perfs.map.PerfLinkedHashMapFinePutGet;
import javasnack.snacks.perfs.map.PerfLinkedHashMapTotalAvg;
import javasnack.snacks.perfs.map.PerfTreeMapFinePutGet;
import javasnack.snacks.perfs.map.PerfTreeMapTotalAvg;

public class Main {
    static Runnable[] snacks = new Runnable[] {
        new HelloWorld(),
        new RunningJREVersionIs(),
        new LivingUserThreads(),
        new ByteRange1(),
        new ByteRange2(),
        new ByteRange3(),
        new ByteRange4(),
        new ReadConsoleInput(),
        new ListAvailableCharsets(),
        new UUIDDemo(),
        new SystemPropertiesAndEnvs(),
        new PerfJavaArrayFinePutGet(),
        new PerfArrayListFinePutGet(),
        new PerfLinkedListFinePutGet(),
        new PerfHashMapFinePutGet(),
        new PerfHashMapTotalAvg(),
        new PerfLinkedHashMapFinePutGet(),
        new PerfLinkedHashMapTotalAvg(),
        new PerfTreeMapFinePutGet(),
        new PerfTreeMapTotalAvg(),
        new CollectionTypes1(),
        new LocalJarDemo()
        };
    public static void main(String[] args) throws Exception {
        System.out.println("Welcome to Java Snack!!");
        for (int i = 0; i < snacks.length; i++) {
            Runnable r = snacks[i];
            System.out.println("Snack No.[" + i + "] - " + r.getClass().getCanonicalName());
        }
        System.out.print("Enter snack number (exit for -1):");
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            int i = Integer.parseInt(br.readLine().trim());
            if (i >= snacks.length) {
                System.out.println("Enter 0 - " + (snacks.length - 1) + " number.");
                return;
            }
            if (i < 0) {
                return;
            }
            new Thread(snacks[i]).start();
        } catch (NumberFormatException e) {
            System.out.println("Enter 0 - " + (snacks.length -1) + " number.");
        } finally {
            System.out.println("Exiting main thread...");
        }
    }
}
