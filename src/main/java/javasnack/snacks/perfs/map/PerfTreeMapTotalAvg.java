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

package javasnack.snacks.perfs.map;

import java.math.BigInteger;
import java.util.TreeMap;

import javasnack.tool.RandomString;

public class PerfTreeMapTotalAvg implements Runnable {

    static String DUMMY_FILLING = "";

    long putting(TreeMap<String, String> m, String[] seeds, int mass) {
        long startTime = System.nanoTime();
        for (int i = 0; i < mass; i++) {
            m.put(seeds[i], DUMMY_FILLING);
        }
        return System.nanoTime() - startTime;
    }

    long getting(TreeMap<String, String> m, String[] keys, int mass) {
        long startTime = System.nanoTime();
        for (int i = 0; i < mass; i++) {
            m.get(keys[i]);
        }
        return System.nanoTime() - startTime;
    }

    static final int MASS = 500000;
    static final int ITER = 50;

    @Override
    public void run() {

        String[] keys = new String[MASS];
        for (int i = 0; i < MASS; i++) {
            keys[i] = RandomString.get(10, 30);
        }

        @SuppressWarnings("unchecked")
        TreeMap<String, String>[] maps = new TreeMap[ITER];

        BigInteger sumOfPutting = new BigInteger("0");
        for (int i = 0; i < ITER; i++) {
            TreeMap<String, String> m = new TreeMap<String, String>();
            long elapsed = putting(m, keys, MASS);
            maps[i] = m;
            System.out.println(String.format("puttings[%d] = %d nano sec.", i,
                    elapsed));
            sumOfPutting = sumOfPutting.add(BigInteger.valueOf(elapsed));
        }
        long avg1 = sumOfPutting.divide(BigInteger.valueOf(ITER)).longValue();

        BigInteger sumOfGetting = new BigInteger("0");
        for (int i = 0; i < ITER; i++) {
            TreeMap<String, String> m = maps[i];
            long elapsed = getting(m, keys, MASS);
            System.out.println(String.format("gettings[%d] = %d nano sec.", i,
                    elapsed));
            sumOfGetting = sumOfGetting.add(BigInteger.valueOf(elapsed));
        }
        long avg2 = sumOfGetting.divide(BigInteger.valueOf(ITER)).longValue();

        System.out.println("-----------------------------------------");
        System.out.println(String.format(
                "putting avg = %d nano (%d milli) sec.", avg1, avg1 / 1000000));
        System.out.println(String.format(
                "getting avg = %d nano (%d milli) sec.", avg2, avg2 / 1000000));

        System.out.println("(END)");
    }

}
