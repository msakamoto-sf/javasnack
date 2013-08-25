/*
 * Copyright 2013 the original author or authors.
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

public class RandomDistrubution implements Runnable {
    @Override
    public void run() {
        Map<String, Integer> seeds = new LinkedHashMap<String, Integer>();
        seeds.put("abc", 1);
        seeds.put("def", 2);
        seeds.put("ghi", 5);

        List<String> distlist = new ArrayList<String>(seeds.size());
        for (Map.Entry<String, Integer> entry : seeds.entrySet()) {
            int ammount = entry.getValue();
            String dist = entry.getKey();
            for (int i = 0; i < ammount; i++) {
                distlist.add(dist);
            }
        }
        int num = distlist.size();

        Random r = new Random();

        for (int i = 0; i < 100; i++) {
            int randp = r.nextInt(num);
            System.out.println(distlist.get(randp));
        }
    }

}
