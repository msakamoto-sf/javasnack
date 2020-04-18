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

import javasnack.RunnableSnack;
import javasnack.snacks.perfs.ElapsedWith;
import javasnack.tool.RandomString;

/**
 * {@link LinkedHashMap#put(Object, Object)} と {@link LinkedHashMap#get(Object)} の処理時間を細かくダンプするサンプル。
 * 対象のLinkedHashMapはバケット数16, スレッショルド 0.75 に設定している。
 * put()/get()については全体的に HashMap 版と同様、全体的にコンスタントな傾向が確認できる。
 * リンクリストの操作がある分、HashMap 版よりは時間がかかっている。TreeMap版よりは早い。
 * 
 * @author msakamoto
 */
public class PerfLinkedHashMapFinePutGet implements RunnableSnack {

    long putting(LinkedHashMap<String, String> m, String key, final String filling) {
        long startTime = System.nanoTime();
        m.put(key, filling);
        return System.nanoTime() - startTime;
    }

    ElapsedWith<String> getting(LinkedHashMap<String, String> m, String key) {
        long startTime = System.nanoTime();
        final String r = m.get(key);
        return ElapsedWith.of(r, System.nanoTime() - startTime);
    }

    static final int MASS = 500;

    @Override
    public void run(final String... args) {

        String[] keys = new String[MASS];
        for (int i = 0; i < MASS; i++) {
            keys[i] = RandomString.get(10, 30);
        }

        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>(16,
                0.75f);

        BigInteger sumOfPutting = BigInteger.ZERO;
        for (int i = 0; i < MASS; i++) {
            long elapsed = putting(m, keys[i], RandomString.get(10, 30));
            System.out.println(String.format("put()[%d] = %d nano sec.", i,
                    elapsed));
            sumOfPutting = sumOfPutting.add(BigInteger.valueOf(elapsed));
        }
        long avg1 = sumOfPutting.divide(BigInteger.valueOf(MASS)).longValue();

        BigInteger sumOfGetting = BigInteger.ZERO;
        for (int i = 0; i < MASS; i++) {
            long elapsed = getting(m, keys[i]).elapsed;
            System.out.println(String.format("get()[%d] = %d nano sec.", i,
                    elapsed));
            sumOfGetting = sumOfGetting.add(BigInteger.valueOf(elapsed));
        }
        long avg2 = sumOfGetting.divide(BigInteger.valueOf(MASS)).longValue();

        System.out.println("-----------------------------------------");
        System.out.println(String.format("put() avg = %d nano (%d milli) sec.",
                avg1, avg1 / 1000000));
        System.out.println(String.format("get() avg = %d nano (%d milli) sec.",
                avg2, avg2 / 1000000));

        System.out.println("(END)");
    }

}
