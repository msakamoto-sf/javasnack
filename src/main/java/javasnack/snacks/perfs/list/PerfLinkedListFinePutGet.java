package javasnack.snacks.perfs.list;

import java.math.BigInteger;
import java.util.LinkedList;

import javasnack.tool.RandomString;

public class PerfLinkedListFinePutGet implements Runnable {

    long adding(LinkedList<String> list, String val) {
        long startTime = System.nanoTime();
        list.add(val);
        return System.nanoTime() - startTime;
    }

    long getting(LinkedList<String> list, int index) {
        long startTime = System.nanoTime();
        list.get(index);
        return System.nanoTime() - startTime;
    }

    @Override
    public void run() {

        int MASS = 500;
        String[] keys = new String[MASS];
        for (int i = 0; i < MASS; i++) {
            keys[i] = RandomString.get(10, 30);
        }

        LinkedList<String> list = new LinkedList<String>();

        BigInteger adding_sum = new BigInteger("0");
        for (int i = 0; i < MASS; i++) {
            long elapsed = adding(list, keys[i]);
            System.out.println(String.format("add()[%d] = %d nano sec.", i,
                    elapsed));
            adding_sum = adding_sum.add(BigInteger.valueOf(elapsed));
        }
        long avg1 = adding_sum.divide(BigInteger.valueOf(MASS)).longValue();

        BigInteger getting_sum = new BigInteger("0");
        for (int i = 0; i < MASS; i++) {
            long elapsed = getting(list, i);
            System.out.println(String.format("get()[%d] = %d nano sec.", i,
                    elapsed));
            getting_sum = getting_sum.add(BigInteger.valueOf(elapsed));
        }
        long avg2 = getting_sum.divide(BigInteger.valueOf(MASS)).longValue();

        System.out.println("-----------------------------------------");
        System.out.println(String.format("add() avg = %d nano (%d milli) sec.",
                avg1, avg1 / 1000000));
        System.out.println(String.format("get() avg = %d nano (%d milli) sec.",
                avg2, avg2 / 1000000));

        System.out.println("(END)");
    }

}
