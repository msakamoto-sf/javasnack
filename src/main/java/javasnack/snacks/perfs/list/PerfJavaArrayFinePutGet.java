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

import javasnack.tool.RandomString;

public class PerfJavaArrayFinePutGet implements Runnable {

    long setting(String[] arr, int index, String val) {
        long startTime = System.nanoTime();
        arr[index] = val;
        return System.nanoTime() - startTime;
    }

    String bufForGetting = "";

    long getting(String[] arr, int index) {
        long startTime = System.nanoTime();
        bufForGetting = arr[index];
        return System.nanoTime() - startTime;
    }

    static final int MASS = 500;

    @Override
    public void run() {

        String[] arr = new String[MASS];

        BigInteger sumOfSetting = BigInteger.ZERO;
        for (int i = 0; i < MASS; i++) {
            long elapsed = setting(arr, i, RandomString.get(10, 30));
            System.out.println(String.format("arr[%d] = %d nano sec.", i,
                    elapsed));
            sumOfSetting = sumOfSetting.add(BigInteger.valueOf(elapsed));
        }
        long avg1 = sumOfSetting.divide(BigInteger.valueOf(MASS)).longValue();

        BigInteger sumOfGetting = BigInteger.ZERO;
        for (int i = 0; i < MASS; i++) {
            long elapsed = getting(arr, i);
            System.out.println(String.format("get()[%d] = %d nano sec.", i,
                    elapsed));
            sumOfGetting = sumOfGetting.add(BigInteger.valueOf(elapsed));
        }
        long avg2 = sumOfGetting.divide(BigInteger.valueOf(MASS)).longValue();

        System.out.println("-----------------------------------------");
        System.out.println(String.format("arr() avg = %d nano (%d milli) sec.",
                avg1, avg1 / 1000000));
        System.out.println(String.format("get() avg = %d nano (%d milli) sec.",
                avg2, avg2 / 1000000));

        System.out.println("(END)");
    }

}
