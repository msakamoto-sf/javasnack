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
package javasnack.tool;

import java.util.Random;

public class RandomString {

    protected static int STD_AL_NUM_MARK_START = 33; // '!'
    protected static int STD_AL_NUM_MARK_END = 126; // '~'
    protected static int STD_AL_NUM_MARK_RANGE = STD_AL_NUM_MARK_END
            - STD_AL_NUM_MARK_START;
    protected static Random rand = new Random();

    public static char getRandomChar() {
        int r = rand.nextInt(STD_AL_NUM_MARK_RANGE) + STD_AL_NUM_MARK_START;
        return (char) r;
    }

    public static String get(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(getRandomChar());
        }
        return sb.toString();
    }

    public static String get(int minLen, int maxLen) {
        if (minLen < 0) {
            return new String("");
        }
        if (maxLen < 0) {
            return new String("");
        }
        if (maxLen <= minLen) {
            return get(minLen);
        }
        int range = maxLen - minLen;
        int len = rand.nextInt(range) + minLen;
        return get(len);
    }
}
