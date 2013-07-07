package javasnack.snacks.perfs.map;

import java.math.BigInteger;
import java.util.LinkedHashMap;

import javasnack.tool.RandomString;

public class PerfLinkedHashMapTotalAvg implements Runnable {

    static String DUMMY_FILLING = "";

    long putting(LinkedHashMap<String, String> m, String[] seeds, int mass) {
        long startTime = System.nanoTime();
        for (int i = 0; i < mass; i++) {
            m.put(seeds[i], DUMMY_FILLING);
        }
        return System.nanoTime() - startTime;
    }

    long getting(LinkedHashMap<String, String> m, String[] keys, int mass) {
        long startTime = System.nanoTime();
        for (int i = 0; i < mass; i++) {
            m.get(keys[i]);
        }
        return System.nanoTime() - startTime;
    }

    @Override
    public void run() {

        int MASS = 500000;
        String[] keys = new String[MASS];
        for (int i = 0; i < MASS; i++) {
            keys[i] = RandomString.get(10, 30);
        }

        int ITER = 50;

        @SuppressWarnings("unchecked")
        LinkedHashMap<String, String>[] maps = new LinkedHashMap[ITER];

        BigInteger putting_sum = new BigInteger("0");
        for (int i = 0; i < ITER; i++) {
            LinkedHashMap<String, String> m = new LinkedHashMap<String, String>(
                    16, 0.75f);
            long elapsed = putting(m, keys, MASS);
            maps[i] = m;
            System.out.println(String.format("puttings[%d] = %d nano sec.", i,
                    elapsed));
            putting_sum = putting_sum.add(BigInteger.valueOf(elapsed));
        }
        long avg1 = putting_sum.divide(BigInteger.valueOf(ITER)).longValue();

        BigInteger getting_sum = new BigInteger("0");
        for (int i = 0; i < ITER; i++) {
            LinkedHashMap<String, String> m = maps[i];
            long elapsed = getting(m, keys, MASS);
            System.out.println(String.format("gettings[%d] = %d nano sec.", i,
                    elapsed));
            getting_sum = getting_sum.add(BigInteger.valueOf(elapsed));
        }
        long avg2 = getting_sum.divide(BigInteger.valueOf(ITER)).longValue();

        System.out.println("-----------------------------------------");
        System.out.println(String.format(
                "putting avg = %d nano (%d milli) sec.", avg1, avg1 / 1000000));
        System.out.println(String.format(
                "getting avg = %d nano (%d milli) sec.", avg2, avg2 / 1000000));

        System.out.println("(END)");
    }

}
