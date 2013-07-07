package javasnack.snacks.perfs.list;

import java.math.BigInteger;

import javasnack.tool.RandomString;

public class PerfJavaArrayFinePutGet implements Runnable {

    long setting(String[] arr, int index, String val) {
        long startTime = System.nanoTime();
        arr[index] = val;
        return System.nanoTime() - startTime;
    }

    long getting(String[] arr, int index) {
        String r = null;
        long startTime = System.nanoTime();
        r = arr[index];
        return System.nanoTime() - startTime;
    }

    @Override
    public void run() {

        int MASS = 500;
        String[] arr = new String[MASS];

        BigInteger setting_sum = new BigInteger("0");
        for (int i = 0; i < MASS; i++) {
            long elapsed = setting(arr, i, RandomString.get(10, 30));
            System.out.println(String.format("arr[%d] = %d nano sec.", i,
                    elapsed));
            setting_sum = setting_sum.add(BigInteger.valueOf(elapsed));
        }
        long avg1 = setting_sum.divide(BigInteger.valueOf(MASS)).longValue();

        BigInteger getting_sum = new BigInteger("0");
        for (int i = 0; i < MASS; i++) {
            long elapsed = getting(arr, i);
            System.out.println(String.format("get()[%d] = %d nano sec.", i,
                    elapsed));
            getting_sum = getting_sum.add(BigInteger.valueOf(elapsed));
        }
        long avg2 = getting_sum.divide(BigInteger.valueOf(MASS)).longValue();

        System.out.println("-----------------------------------------");
        System.out.println(String.format("arr() avg = %d nano (%d milli) sec.",
                avg1, avg1 / 1000000));
        System.out.println(String.format("get() avg = %d nano (%d milli) sec.",
                avg2, avg2 / 1000000));

        System.out.println("(END)");
    }

}
