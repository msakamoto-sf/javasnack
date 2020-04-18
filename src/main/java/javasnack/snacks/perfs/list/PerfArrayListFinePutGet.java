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

import javasnack.RunnableSnack;
import javasnack.snacks.perfs.ElapsedWith;
import javasnack.tool.RandomString;

/**
 * インデックス 0 - 499 までの {@link ArrayList#add(Object)} と {@link ArrayList#get(int)}
 * の時間を細かくダンプ表示する。
 * 
 * 全体的に一定範囲の処理時間に収まり、計算量は安定している。
 * add() についてだけ、ある程度エントリーが追加された状態でさらに追加となると、
 * 内部の配列を拡張するための処理が走るためデータ量が増えるほどその処理時間が増えていく。
 * 
 * @author msakamoto
 */
public class PerfArrayListFinePutGet implements RunnableSnack {

    long adding(ArrayList<String> list, String val) {
        long startTime = System.nanoTime();
        list.add(val);
        return System.nanoTime() - startTime;
    }

    ElapsedWith<String> getting(ArrayList<String> list, int index) {
        long startTime = System.nanoTime();
        final String r = list.get(index);
        return ElapsedWith.of(r, System.nanoTime() - startTime);
    }

    static final int MASS = 500;

    @Override
    public void run(final String... args) {

        String[] keys = new String[MASS];
        for (int i = 0; i < MASS; i++) {
            keys[i] = RandomString.get(10, 30);
        }

        ArrayList<String> list = new ArrayList<String>(20);

        BigInteger sumOfAdding = BigInteger.ZERO;
        for (int i = 0; i < MASS; i++) {
            long elapsed = adding(list, keys[i]);
            System.out.println(String.format("add()[%d] = %d nano sec.", i,
                    elapsed));
            sumOfAdding = sumOfAdding.add(BigInteger.valueOf(elapsed));
        }
        long avg1 = sumOfAdding.divide(BigInteger.valueOf(MASS)).longValue();

        BigInteger sumOfGetting = BigInteger.ZERO;
        for (int i = 0; i < MASS; i++) {
            long elapsed = getting(list, i).elapsed;
            System.out.println(String.format("get()[%d] = %d nano sec.", i,
                    elapsed));
            sumOfGetting = sumOfGetting.add(BigInteger.valueOf(elapsed));
        }
        long avg2 = sumOfGetting.divide(BigInteger.valueOf(MASS)).longValue();

        System.out.println("-----------------------------------------");
        System.out.println(String.format("add() avg = %d nano (%d milli) sec.",
                avg1, avg1 / 1000000));
        System.out.println(String.format("get() avg = %d nano (%d milli) sec.",
                avg2, avg2 / 1000000));

        System.out.println("(END)");
    }

}
