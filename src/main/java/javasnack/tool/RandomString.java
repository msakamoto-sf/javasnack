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
