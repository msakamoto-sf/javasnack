package javasnack.snacks.perfs.map;

import java.math.BigInteger;
import java.util.TreeMap;

import javasnack.tool.RandomString;

public class PerfTreeMapFinePutGet implements Runnable {

    static String DUMMY_FILLING = "";

    long putting(TreeMap<String, String> m, String key) {
        long startTime = System.nanoTime();
        m.put(key, DUMMY_FILLING);
        return System.nanoTime() - startTime;
    }

    long getting(TreeMap<String, String> m, String key) {
        long startTime = System.nanoTime();
        m.get(key);
        return System.nanoTime() - startTime;
    }

    @Override
    public void run() {

        int MASS = 500;
        String[] keys = new String[MASS];
        for (int i = 0; i < MASS; i++) {
            keys[i] = RandomString.get(10, 30);
        }

        TreeMap<String, String> m = new TreeMap<String, String>();

        BigInteger putting_sum = new BigInteger("0");
        for (int i = 0; i < MASS; i++) {
            long elapsed = putting(m, keys[i]);
            System.out.println(String.format("put()[%d] = %d nano sec.", i,
                    elapsed));
            putting_sum = putting_sum.add(BigInteger.valueOf(elapsed));
        }
        long avg1 = putting_sum.divide(BigInteger.valueOf(MASS)).longValue();

        BigInteger getting_sum = new BigInteger("0");
        for (int i = 0; i < MASS; i++) {
            long elapsed = getting(m, keys[i]);
            System.out.println(String.format("get()[%d] = %d nano sec.", i,
                    elapsed));
            getting_sum = getting_sum.add(BigInteger.valueOf(elapsed));
        }
        long avg2 = getting_sum.divide(BigInteger.valueOf(MASS)).longValue();

        System.out.println("-----------------------------------------");
        System.out.println(String.format("put() avg = %d nano (%d milli) sec.",
                avg1, avg1 / 1000000));
        System.out.println(String.format("get() avg = %d nano (%d milli) sec.",
                avg2, avg2 / 1000000));

        System.out.println("(END)");
    }

}
