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
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import javasnack.RunnableSnack;
import javasnack.snacks.perfs.ElapsedWith;
import javasnack.tool.RandomString;

/**
 * {@link LinkedHashMap#put(Object, Object)} と {@link LinkedHashMap#get(Object)} を大量に呼び出して処理時間の平均値を見るサンプル。
 * 
 * 平均値で見ると、処理時間が短い順で HashMap, LinkedHashMap(HashMapより若干増), TreeMap(HashMapの2倍以上増) の順になる。 
 * 
 * @author msakamoto
 */
public class PerfLinkedHashMapTotalAvg implements RunnableSnack {

    long putting(LinkedHashMap<String, String> m, String[] seeds, int mass, final String filling) {
        long startTime = System.nanoTime();
        for (int i = 0; i < mass; i++) {
            m.put(seeds[i], filling);
        }
        return System.nanoTime() - startTime;
    }

    ElapsedWith<List<String>> getting(LinkedHashMap<String, String> m, String[] keys, int mass) {
        // put() のコストが一定範囲に収まるようにして、影響を平準化する。
        final List<String> drop = new LinkedList<>();
        long startTime = System.nanoTime();
        for (int i = 0; i < mass; i++) {
            drop.add(m.get(keys[i]));
        }
        return ElapsedWith.of(drop, System.nanoTime() - startTime);
    }

    static final int MASS = 500000;
    static final int ITER = 50;

    @Override
    public void run(final String... args) {

        String[] keys = new String[MASS];
        for (int i = 0; i < MASS; i++) {
            keys[i] = RandomString.get(10, 30);
        }

        @SuppressWarnings("unchecked")
        LinkedHashMap<String, String>[] maps = new LinkedHashMap[ITER];

        BigInteger sumOfPutting = BigInteger.ZERO;
        for (int i = 0; i < ITER; i++) {
            LinkedHashMap<String, String> m = new LinkedHashMap<String, String>(
                    16, 0.75f);
            long elapsed = putting(m, keys, MASS, RandomString.get(10, 30));
            maps[i] = m;
            System.out.println(String.format("puttings[%d] = %d nano sec.", i,
                    elapsed));
            sumOfPutting = sumOfPutting.add(BigInteger.valueOf(elapsed));
        }
        long avg1 = sumOfPutting.divide(BigInteger.valueOf(ITER)).longValue();

        BigInteger sumOfGetting = BigInteger.ZERO;
        for (int i = 0; i < ITER; i++) {
            LinkedHashMap<String, String> m = maps[i];
            long elapsed = getting(m, keys, MASS).elapsed;
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
