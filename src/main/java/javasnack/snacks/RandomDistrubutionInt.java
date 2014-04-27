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
package javasnack.snacks;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RandomDistrubutionInt implements Runnable {
    class ErrorneousInteger {
        public final int base;
        public final int error;

        public ErrorneousInteger(int base, int error) {
            this.base = base;
            this.error = error;
        }

        public int nextInt(Random r) {
            int plus = r.nextInt(2);
            int differ = r.nextInt(error + 1);
            int error = (plus > 0) ? differ : -1 * differ;
            return base + error;
        }
    }

    @Override
    public void run() {
        Map<ErrorneousInteger, Integer> seeds = new LinkedHashMap<ErrorneousInteger, Integer>();
        seeds.put(new ErrorneousInteger(40, 0), 1);
        seeds.put(new ErrorneousInteger(50, 1), 2);
        seeds.put(new ErrorneousInteger(60, 2), 3);

        List<ErrorneousInteger> distlist = new ArrayList<ErrorneousInteger>(
                seeds.size());
        for (Map.Entry<ErrorneousInteger, Integer> entry : seeds.entrySet()) {
            int ammount = entry.getValue();
            ErrorneousInteger dist = entry.getKey();
            for (int i = 0; i < ammount; i++) {
                distlist.add(dist);
            }
        }
        int num = distlist.size();

        Random r = new Random();

        for (int i = 0; i < 100; i++) {
            int randp = r.nextInt(num);
            System.out.println(distlist.get(randp).nextInt(r));
        }
    }

}
