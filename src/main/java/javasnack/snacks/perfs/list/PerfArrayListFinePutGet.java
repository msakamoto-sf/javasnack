/*
 * Copyright 2013 "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package javasnack.snacks.perfs.list;

import java.math.BigInteger;
import java.util.ArrayList;

import javasnack.tool.RandomString;

public class PerfArrayListFinePutGet implements Runnable {

    long adding(ArrayList<String> list, String val) {
        long startTime = System.nanoTime();
        list.add(val);
        return System.nanoTime() - startTime;
    }

    long getting(ArrayList<String> list, int index) {
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

        ArrayList<String> list = new ArrayList<String>(20);

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
